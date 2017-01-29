package hoo.hktranseta.main.kmb.worker;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import hoo.hktranseta.common.BaseApplication;
import hoo.hktranseta.common.Constants;
import hoo.hktranseta.common.Utils;
import hoo.hktranseta.common.worker.GreenDaoManager;
import hoo.hktranseta.common.worker.SharedPrefsManager;
import hoo.hktranseta.main.kmb.model.db.DaoSession;
import hoo.hktranseta.main.kmb.model.db.KmbEtaDao;
import hoo.hktranseta.main.kmb.model.db.KmbRouteStop;
import hoo.hktranseta.main.kmb.model.db.KmbRouteStopDao;
import hoo.hktranseta.main.kmb.model.db.KmbServiceType;
import hoo.hktranseta.main.kmb.model.db.KmbServiceTypeDao;
import hoo.hktranseta.main.kmb.model.json.Eta;
import hoo.hktranseta.main.kmb.model.json.RouteBound;
import hoo.hktranseta.main.kmb.model.json.SpecialRoute;
import hoo.hktranseta.main.kmb.model.json.Stop;

public class KmbDataManager {

    private static final String TAG = "KmbDataManager";
    private static KmbDataManager mInstance = null;

    private DaoSession mDaoSession;
    private Headers mHeaders;

    public KmbDataManager() {
        mDaoSession = GreenDaoManager.getInstance();
        mHeaders = new Headers();
        mHeaders.add(Constants.Connection.HEADERS_REFERER, Constants.Url.KMB_HEARERS_REFERER);
        mHeaders.add(Constants.Connection.HEADERS_X_REQUESTED_WITH, Constants.Connection.HEADERS_XMLHTTPREQUEST);
        mHeaders.add(Constants.Connection.HEADERS_PRAGMA, Constants.Connection.HEADERS_NO_CACHE);
    }

    public static KmbDataManager getInstance() {
        if (mInstance == null){
            mInstance = new KmbDataManager();
        }
        return mInstance;
    }

    private static long getExpiryTimestamp() {
        SharedPrefsManager sharedPrefsManager = SharedPrefsManager.getInstance();
        return Utils.getCurrentTimestamp()
                - sharedPrefsManager.getInt(Constants.Prefs.DATABASE_UPDATE_FREQUENCY, 8) * 60 * 60;
    }

    public void clearAllDbData () {
        mDaoSession.getKmbRouteStopDao().deleteAll();
        mDaoSession.getKmbServiceTypeDao().deleteAll();
        mDaoSession.getKmbEtaDao().deleteAll();
    }

    /////////////////////////
    //  ServiceType List   //
    /////////////////////////

    public List<KmbServiceType> getDefaultServiceTypeList(String routeNo){
        List<KmbServiceType> result = getDefaultServiceTypeListByDb(routeNo, getExpiryTimestamp());
        if (result == null || result.size() == 0) {
            getServiceTypeListByNet(routeNo);
            result = getDefaultServiceTypeListByDb(routeNo, 0);
        }
        return result;
    }

    public List<KmbServiceType> getServiceTypeList(String routeNo){
        List<KmbServiceType> result = getServiceTypeListByDb(routeNo, getExpiryTimestamp());
        if (result == null || result.size() == 0) {
            getServiceTypeListByNet(routeNo);
            result = getServiceTypeListByDb(routeNo, 0);
        }
        for (KmbServiceType kmbServiceType : result) {
            getRouteStopList(kmbServiceType);
        }
        return result;
    }

    public List<KmbServiceType> getServiceTypeList(String routeNo, int boundId){
        List<KmbServiceType> result = getServiceTypeListByDb(routeNo, boundId, getExpiryTimestamp());
        if (result == null || result.size() == 0) {
            getServiceTypeListByNet(routeNo);
            result = getServiceTypeListByDb(routeNo, boundId, 0);
        }
        for (KmbServiceType kmbServiceType : result) {
            getRouteStopList(kmbServiceType);
        }
        return result;
    }

    private List<KmbServiceType> getDefaultServiceTypeListByDb(String routeNo, long expiryTimestamp) {
        KmbServiceTypeDao kmbServiceTypeDao = mDaoSession.getKmbServiceTypeDao();

        return kmbServiceTypeDao.queryBuilder()
                .where(KmbServiceTypeDao.Properties.RouteNo.eq(routeNo),
                        KmbServiceTypeDao.Properties.ServiceTypeId.eq(1),
                        KmbServiceTypeDao.Properties.UpdateTimestamp.ge(expiryTimestamp))
                .orderAsc(KmbServiceTypeDao.Properties.BoundId)
                .list();
    }

    private List<KmbServiceType> getServiceTypeListByDb(String routeNo, long expiryTimestamp) {
        KmbServiceTypeDao kmbServiceTypeDao = mDaoSession.getKmbServiceTypeDao();

        return kmbServiceTypeDao.queryBuilder()
                .where(KmbServiceTypeDao.Properties.RouteNo.eq(routeNo),
                        KmbServiceTypeDao.Properties.UpdateTimestamp.ge(expiryTimestamp))
                .orderAsc(KmbServiceTypeDao.Properties.BoundId,
                        KmbServiceTypeDao.Properties.ServiceTypeId)
                .list();
    }

    private List<KmbServiceType> getServiceTypeListByDb(String routeNo, int boundId, long expiryTimestamp) {
        KmbServiceTypeDao kmbServiceTypeDao = mDaoSession.getKmbServiceTypeDao();

        return kmbServiceTypeDao.queryBuilder()
                .where(KmbServiceTypeDao.Properties.RouteNo.eq(routeNo),
                        KmbServiceTypeDao.Properties.BoundId.eq(boundId),
                        KmbServiceTypeDao.Properties.UpdateTimestamp.ge(expiryTimestamp))
                .orderAsc(KmbServiceTypeDao.Properties.ServiceTypeId)
                .list();
    }

    private void deleteServiceTypeListByDb(String routeNo, int boundId, long expiryTimestamp) {
        KmbServiceTypeDao kmbServiceTypeDao = mDaoSession.getKmbServiceTypeDao();

        kmbServiceTypeDao.queryBuilder()
                .where(KmbServiceTypeDao.Properties.RouteNo.eq(routeNo),
                        KmbServiceTypeDao.Properties.BoundId.eq(boundId),
                        KmbServiceTypeDao.Properties.UpdateTimestamp.lt(expiryTimestamp))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    private boolean getServiceTypeListByNet(String routeNo) {
        // Set Uri
        Uri uri = Uri.parse(Constants.Url.KMB_SEARCH)
                .buildUpon()
                .appendQueryParameter(Constants.Kmb.ACTION, Constants.Kmb.GET_ROUTE_BOUND)
                .appendQueryParameter(Constants.Kmb.ROUTE, routeNo)
                .build();

        // Get Respond
        Future<Response<JsonObject>> conn = Ion.with(BaseApplication.getContext())
                .load(uri.toString())
                .setLogging(TAG, Log.DEBUG)
                .addHeaders(mHeaders.getMultiMap())
                .setTimeout(60 * 1000)
                .asJsonObject()
                .withResponse();

        Response<JsonObject> response = null;

        try {
            // first attempt
            response = conn.get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.getClass();
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
            JsonObject jsonObject = response.getResult();
            Log.d(TAG, jsonObject.toString());

            Gson gson = new GsonBuilder().create();
            RouteBound routeBound = gson.fromJson(jsonObject, RouteBound.class);

            if (!routeBound.result) {
                Log.d(TAG, "result: false");
                return false;
            }

            TreeSet<Integer> boundList = routeBound.getBoundList();
            Log.d(TAG, "routeNo=[" + routeNo + "]; boundList.size=[" + boundList.size() + "]");

            return getServiceTypeListByNet(routeNo, boundList);
        }

        return false;
    }

    private boolean getServiceTypeListByNet(String routeNo, Iterable<Integer> boundList) {
        List<Future<Response<JsonObject>>> connList = new ArrayList<>();

        for (int boundId : boundList) {
            // Set Uri
            Uri uri = Uri.parse(Constants.Url.KMB_SEARCH)
                    .buildUpon()
                    .appendQueryParameter(Constants.Kmb.ACTION, Constants.Kmb.GET_SPECIAL_ROUTE)
                    .appendQueryParameter(Constants.Kmb.ROUTE, routeNo)
                    .appendQueryParameter(Constants.Kmb.BOUND, String.valueOf(boundId))
                    .build();

            // Get Respond
            Future<Response<JsonObject>> conn = Ion.with(BaseApplication.getContext())
                    .load(uri.toString())
                    .setLogging(TAG, Log.DEBUG)
                    .addHeaders(mHeaders.getMultiMap())
                    .setTimeout(60 * 1000)
                    .asJsonObject()
                    .withResponse();

            connList.add(conn);
        }

        for (Future<Response<JsonObject>> conn : connList) {
            Response<JsonObject> response = null;

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
                JsonObject jsonObject = response.getResult();
                Log.d(TAG, jsonObject.toString());

                Gson gson = new GsonBuilder().create();
                SpecialRoute specialRoute = gson.fromJson(jsonObject, SpecialRoute.class);

                if (!specialRoute.result) {
                    Log.d(TAG, "result: false");
                    return false;
                }

                Log.d(TAG, "getServiceTypeListByNet:" + routeNo + ">" + specialRoute.data.routes.size());
                KmbServiceTypeDao kmbServiceTypeDao = mDaoSession.getKmbServiceTypeDao();
                kmbServiceTypeDao.insertOrReplaceInTx(KmbDataConverter.toKmbServiceTypeList(specialRoute));
                if (specialRoute.data.routes.size() > 0) {
                    deleteServiceTypeListByDb(routeNo, specialRoute.data.routes.get(0).bound, getExpiryTimestamp());
                }
            }
        }

        return true;
    }

    ///////////////////////
    //  RouteStop List   //
    ///////////////////////

    private void getRouteStopList(KmbServiceType kmbServiceType) {
        List<KmbRouteStop> result = kmbServiceType.getKmbRouteStopList();
        if (result == null || result.size() == 0 || result.get(0).getUpdateTimestamp() < getExpiryTimestamp()) {
            getRouteStopListByNet(kmbServiceType.getRouteNo(), kmbServiceType.getBoundId(), kmbServiceType.getServiceTypeId());
            kmbServiceType.resetKmbRouteStopList();
        }
    }

//    public List<KmbRouteStop> getRouteStopList(String routeNo, int boundId, int serviceTypeId){
//        List<KmbRouteStop> result = getRouteStopListByDb(routeNo, boundId, serviceTypeId);
//        if (result == null || result.size() == 0) {
//            getRouteStopListByNet(routeNo, boundId, serviceTypeId);
//            result = getRouteStopListByDb(routeNo, boundId, serviceTypeId);
//        }
//        //return kmbRouteStopList;
//        return result;
//    }

//    private List<KmbRouteStop> getRouteStopListByDb(String routeNo, int boundId, int serviceTypeId) {
//        KmbRouteStopDao kmbRouteStopDao = mDaoSession.getKmbRouteStopDao();
//
//        return kmbRouteStopDao.queryBuilder()
//                .where(KmbRouteStopDao.Properties.RouteNo.eq(routeNo),
//                        KmbRouteStopDao.Properties.BoundId.eq(boundId),
//                        KmbRouteStopDao.Properties.ServiceTypeId.eq(serviceTypeId))
//                .orderAsc(KmbRouteStopDao.Properties.Seq)
//                .list();
//    }

    private void deleteRouteStopListByDb(String routeNo, int boundId, int serviceTypeId, long expiryTimestamp) {
        KmbRouteStopDao kmbRouteStopDao = mDaoSession.getKmbRouteStopDao();

        kmbRouteStopDao.queryBuilder()
                .where(KmbRouteStopDao.Properties.RouteNo.eq(routeNo),
                        KmbRouteStopDao.Properties.BoundId.eq(boundId),
                        KmbRouteStopDao.Properties.ServiceTypeId.eq(serviceTypeId),
                        KmbRouteStopDao.Properties.UpdateTimestamp.lt(expiryTimestamp))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    private boolean getRouteStopListByNet(String routeNo, int boundId, int serviceTypeId) {
        // Set Uri
        Uri uri = Uri.parse(Constants.Url.KMB_SEARCH)
                .buildUpon()
                .appendQueryParameter(Constants.Kmb.ACTION, Constants.Kmb.GET_STOPS)
                .appendQueryParameter(Constants.Kmb.ROUTE, routeNo)
                .appendQueryParameter(Constants.Kmb.BOUND, String.valueOf(boundId))
                .appendQueryParameter(Constants.Kmb.SERVICE_TYPE, String.valueOf(serviceTypeId))
                .build();

        // Get Respond
        Future<Response<JsonObject>> conn = Ion.with(BaseApplication.getContext())
                .load(uri.toString())
                .setLogging(TAG, Log.DEBUG)
                .addHeaders(mHeaders.getMultiMap())
                .setTimeout(60 * 1000)
                .asJsonObject()
                .withResponse();

        Response<JsonObject> response = null;

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
            JsonObject jsonObject = response.getResult();
            Log.d(TAG, jsonObject.toString());

            Gson gson = new GsonBuilder().create();
            Stop stop = gson.fromJson(jsonObject, Stop.class);

            if (!stop.result) {
                Log.d(TAG, "result: false");
                return false;
            }

            Log.d(TAG, "getRouteStopListByNet:" + stop.data.routeStops.size());

            KmbRouteStopDao kmbServiceTypeDao = mDaoSession.getKmbRouteStopDao();
            kmbServiceTypeDao.insertOrReplaceInTx(KmbDataConverter.toKmbStopList(stop));
            if (stop.data.routeStops.size() > 0) {
                deleteRouteStopListByDb(routeNo, boundId, serviceTypeId, getExpiryTimestamp());
            }
        }

        return true;
    }

    ////////////////
    //  ETA List  //
    ////////////////

    public boolean getEtaList(KmbRouteStop kmbRouteStop) {
        List<KmbRouteStop> kmbRouteStopList = new ArrayList<>();
        kmbRouteStopList.add(kmbRouteStop);

        kmbRouteStop.resetKmbEtaList();

        return getEtaListByNet(kmbRouteStopList);
    }

    public boolean getEtaList(List<KmbRouteStop> kmbRouteStopList) {
        if (kmbRouteStopList.size() == 1) {
            return getEtaList(kmbRouteStopList.get(0));
        }
        else if (kmbRouteStopList.size() > 1) {
            for (KmbRouteStop kmbRouteStop : kmbRouteStopList) {
                kmbRouteStop.resetKmbEtaList();
            }
        }
        return getEtaListByNet(kmbRouteStopList);
    }

    private void deleteEtaListByDb(KmbRouteStop kmbRouteStop) {
        KmbEtaDao kmbEtaDao = mDaoSession.getKmbEtaDao();
        kmbEtaDao.deleteInTx(kmbRouteStop.getKmbEtaList());
    }

    private void deleteEtaListByDb(String routeNo, int boundId, int serviceTypeId) {
        KmbEtaDao kmbEtaDao = mDaoSession.getKmbEtaDao();

        kmbEtaDao.queryBuilder()
                .where(KmbEtaDao.Properties.RouteNo.eq(routeNo),
                        KmbEtaDao.Properties.BoundId.eq(boundId),
                        KmbEtaDao.Properties.ServiceTypeId.eq(serviceTypeId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    private boolean getEtaListByNet(List<KmbRouteStop> kmbRouteStopList) {
        List<Future<Response<JsonObject>>> connList = new ArrayList<>();

        for (KmbRouteStop kmbRouteStop : kmbRouteStopList) {
            // Set Uri
            Uri uri = Uri.parse(Constants.Url.KMB_SEARCH)
                    .buildUpon()
                    .appendQueryParameter(Constants.Kmb.ACTION, Constants.Kmb.GET_ETA)
                    .appendQueryParameter(Constants.Kmb.LANG, Constants.Kmb.LANG_TC_1)
                    .appendQueryParameter(Constants.Kmb.ROUTE, kmbRouteStop.getRouteNo())
                    .appendQueryParameter(Constants.Kmb.BOUND, String.valueOf(kmbRouteStop.getBoundId()))
                    .appendQueryParameter(Constants.Kmb.SERVICE_TYPE, String.valueOf(kmbRouteStop.getServiceTypeId()))
                    .appendQueryParameter(Constants.Kmb.BSI_CODE, kmbRouteStop.getStopId())
                    .appendQueryParameter(Constants.Kmb.SEQ, String.valueOf(kmbRouteStop.getSeq()))
                    .build();

            // Get Respond
            Future<Response<JsonObject>> conn = Ion.with(BaseApplication.getContext())
                    .load(uri.toString())
                    .setLogging(TAG, Log.DEBUG)
                    .addHeaders(mHeaders.getMultiMap())
                    .setTimeout(10 * 1000)
                    .asJsonObject()
                    .withResponse();

            connList.add(conn);
        }

        for (int i=0; i<connList.size(); i++) {
            KmbRouteStop kmbRouteStop = kmbRouteStopList.get(i);
            Future<Response<JsonObject>> conn = connList.get(i);
            Response<JsonObject> response = null;

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

            // Get ETA from Response
            if (response == null) {
                Log.d(TAG, "response: null");
                kmbRouteStop.etaStatus = Constants.Eta.ERROR;
                continue;
            }
            Log.d(TAG, "HTTP status: " + response.getHeaders().code());

            if (response.getHeaders().code() == Constants.Connection.HTTP_STATUS_OK) {
                JsonObject jsonObject = response.getResult();
                Log.d(TAG, jsonObject.toString());

                Gson gson = new GsonBuilder().create();
                Eta eta = gson.fromJson(jsonObject, Eta.class);

                if (!eta.result) {
                    Log.d(TAG, "result: false");
                    kmbRouteStop.etaStatus = Constants.Eta.ERROR;
                    continue;
                }

                // Delete Eta List before update to db
                deleteEtaListByDb(kmbRouteStop);
                kmbRouteStop.resetKmbEtaList();

                KmbEtaDao kmbEtaDao = mDaoSession.getKmbEtaDao();
                kmbEtaDao.insertOrReplaceInTx(KmbDataConverter.toKmbEta(kmbRouteStop, eta));
                kmbRouteStop.resetKmbEtaList();

                if (eta.data == null || eta.data.response.size() <= 0) {
                    kmbRouteStop.etaStatus = Constants.Eta.NODATA;
                } else {
                    kmbRouteStop.etaStatus = Constants.Eta.SUCCESS;
                }
            }
        }

        return true;
    }
}
