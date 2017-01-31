package hoo.hktranseta.main.gov.worker;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.greenrobot.greendao.query.WhereCondition;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

import hoo.hktranseta.common.BaseApplication;
import hoo.hktranseta.common.Constants;
import hoo.hktranseta.common.Utils;
import hoo.hktranseta.common.worker.GreenDaoManager;
import hoo.hktranseta.common.worker.SharedPrefsManager;
import hoo.hktranseta.main.gov.model.DaoSession;
import hoo.hktranseta.main.gov.model.GovBusRoute;
import hoo.hktranseta.main.gov.model.GovBusRouteDao;

public class GovDataManager {
    private static final String TAG = "GovDataManager";
    private static GovDataManager mInstance = null;

    private DaoSession mDaoSession;
    private Headers mHeaders;

    public GovDataManager() {
        mDaoSession = GreenDaoManager.getInstance();
        mHeaders = new Headers();
        mHeaders.add(Constants.Connection.HEADERS_X_REQUESTED_WITH, Constants.Connection.HEADERS_XMLHTTPREQUEST);
        mHeaders.add(Constants.Connection.HEADERS_PRAGMA, Constants.Connection.HEADERS_NO_CACHE);
    }

    public static GovDataManager getInstance() {
        if (mInstance == null){
            mInstance = new GovDataManager();
        }
        return mInstance;
    }

    private static long getExpiryTimestamp() {
        SharedPrefsManager sharedPrefsManager = SharedPrefsManager.getInstance();
        return Utils.getCurrentTimestamp()
                - sharedPrefsManager.getInt(Constants.Prefs.DATABASE_UPDATE_FREQUENCY, 8) * 60 * 60;
    }

    /**
     * Method to get syscode for hketransport API
     * @return syscode
     */
    @SuppressLint("DefaultLocale")
    public static String getSyscode() {
        String s = "tdmwytdm";
        String hexes = "0123456789ABCDEF";
        String randstr = String.format("%05d", new Random().nextInt(10000));
        String timestampVal = String.valueOf(Utils.getCurrentTimestamp());

        String tsstr = timestampVal.substring(2, 3) + timestampVal.substring(9, 10)
                + timestampVal.substring(4, 5) + timestampVal.substring(6, 7)
                + timestampVal.substring(3, 4) + timestampVal.substring(0, 1)
                + timestampVal.substring(8, 9) + timestampVal.substring(7, 8)
                + timestampVal.substring(5, 6) + timestampVal.substring(1, 2);

        String tsc = s.substring(2, 3) + s.substring(7, 8) + s.substring(5, 6)
                + s.substring(4, 5) + s.substring(6, 7) + s.substring(3, 4)
                + s.substring(0, 1) + s.substring(1, 2);

        String md5str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(new StringBuilder(String.valueOf(tsstr)).append(tsc).append(randstr).toString().getBytes());
            byte[] raw = md.digest();
            StringBuilder hex = new StringBuilder(raw.length * 2);
            for (byte b : raw) {
                hex.append(hexes.charAt((b & 240) >> 4)).append(hexes.charAt(b & 15));
            }
            md5str = hex.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return new StringBuilder(String.valueOf(tsstr)).append(md5str.toLowerCase()).append(randstr).toString();
    }

    public void clearAllDbData () {
        mDaoSession.getGovBusRouteDao().deleteAll();
    }

    /////////////////////////
    //  GovBusRoute List   //
    /////////////////////////

    public List<GovBusRoute> getGovBusRouteList(String[] routeTypeList){
        List<GovBusRoute> result = getGovBusRouteListByDb(routeTypeList, getExpiryTimestamp());
        if (result == null || result.size() == 0) {
            getGovBusRouteListByNet();
            result = getGovBusRouteListByDb(routeTypeList, 0);
        }
        return GovDataConverter.filterDuplicateRoute(result);
    }

    private List<GovBusRoute> getGovBusRouteListByDb(String[] routeTypeList, long expiryTimestamp) {
        GovBusRouteDao govBusRouteDao = mDaoSession.getGovBusRouteDao();

        String condition = "";
        for (String routeType : routeTypeList) {
            if (!condition.isEmpty()) {
                condition += " OR ";
            }
            condition += GovBusRouteDao.Properties.RouteType1.columnName +  " = '" + routeType + "'"
                    + " OR " + GovBusRouteDao.Properties.RouteType2.columnName +  " = '" + routeType + "'";
        }
//        condition += " GROUP BY " + GovBusRouteDao.Properties.RouteNo.columnName;
//        condition += "  HAVING MIN( " + GovBusRouteDao.Properties.Special.columnName + ") ";
//                + " AND MAX( " + GovBusRouteDao.Properties.BoundCnt.columnName + ") "
//                + " AND MAX( " + GovBusRouteDao.Properties.Circular.columnName + ") ";

        return govBusRouteDao.queryBuilder()
                .where(new WhereCondition.StringCondition(condition))
                .orderAsc(GovBusRouteDao.Properties.Id)
                .list();
    }

    private boolean getGovBusRouteListByNet() {
        // Set Uri
        Uri uri = Uri.parse(Constants.Url.GOV_SEARCH)
                .buildUpon()
                .appendQueryParameter(Constants.Gov.ROUTE_NAME, "")
                .appendQueryParameter(Constants.Gov.COMPANY_INDEX, Constants.Gov.COMPANY_INDEX_ALL_0)
                .appendQueryParameter(Constants.Gov.LANG, Constants.Gov.LANG_TC)
                .appendQueryParameter(Constants.Gov.SYSCODE, getSyscode())
                .build();

        // Get Respond
        Future<Response<String>> conn = Ion.with(BaseApplication.getContext())
                .load(uri.toString())
                .setLogging(TAG, Log.DEBUG)
                .addHeaders(mHeaders.getMultiMap())
                .setTimeout(60 * 1000)
                .asString()
                .withResponse();

        Response<String> response = null;

        try {
            // first attempt
            response = conn.get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            // second attempt
            try {
                response = conn.get();
            } catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
            }
        }

        // Get BoundSize from Response
        if (response == null) {
            Log.d(TAG, "response: null");
            return false;
        }
        Log.d(TAG, "HTTP status: " + response.getHeaders().code());

        if (response.getHeaders().code() == Constants.Connection.HTTP_STATUS_OK) {
            String strResponse = response.getResult();
            Log.d(TAG, strResponse);

            GovBusRouteDao govBusRouteDao = mDaoSession.getGovBusRouteDao();
            if (!strResponse.isEmpty()) {
                govBusRouteDao.deleteAll();
                govBusRouteDao.insertInTx(GovDataConverter.toGovBusRouteList(strResponse));
            }
        }
        return true;
    }
}
