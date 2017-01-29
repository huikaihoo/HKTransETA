package hoo.hktranseta.common;

public class Constants {

    public static class AppMode {
        public static final int DEBUG = 0;
        public static final int FULL = 1;
        public static final int RELEASE = 2;

        private static final String DEBUG_STR = "debug";
        private static final String FULL_STR = "full";

        public static int getAppMode(String parameters) {
            if ( parameters.equals(DEBUG_STR) ) {
                return DEBUG;
            }
            else if( parameters.equals(FULL_STR) ) {
                return FULL;
            }
            else {
                return RELEASE;
            }
        }
    }

    // Main Activity
    public interface Activity {
        int KMB = 1;
        int NWFB = 2;
        int TRAM = 3;
        int FERRY = 4;
        int MTR = 5;
    }

    public interface Permission{
        int PERMISSIONS_REQUEST_LOCATION = 1;
    }
    // Share Preferences Key
    public interface Prefs {
        String START_ACTIVITY = "start_activity";

        // GeneralPreferenceFragment
        String ETA_UPDATE_FREQUENCY = "eta_update_frequency";
        String DATABASE_UPDATE_FREQUENCY = "database_update_frequency";
        String CLEAR_ROUTE_DATA = "clear_route_data";
        String CLEAR_FOLLOWED_LIST = "clear_followed_list";
        String TESTING = "testing";
        String PARAMETERS = "parameters";
        String APP_VERSION = "app_version";
    }

    // Intent Extra Key
    public interface Extra {
        // WebViewActivity
        String TITLE = "title";
        String URL = "url";

        // Common
        String ACTIONBAR_TITLE = "actionbar_title";
        String ACTIONBAR_SUBTITLE = "actionbar_subtitle";
        String COUNTDOWN_FINISH_TIME = "countdown_finish_time";

        // StreetViewActivity
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";

        // KmbRouteFragment
        String KMB_ROUTE_NO = "kmb_route_no";
        String KMB_BOUND_ID = "kmb_bound_id";
        String KMB_SELECTED_ITEM_POSITION = "kmb_selected_item_position";
        String KMB_SELECTED_ITEM_ID = "kmb_selected_item_id";
        String KMB_SELECTED_SERVICE_TYPE_POSITION = "kmb_selected_service_type_position";
    }

    public interface Database {
        String DB_NAME = "app-db";
    }

    public interface Url {
        String KMB_SEARCH = "http://search.kmb.hk/KMBWebSite/Function/FunctionRequest.ashx";
        String KMB_HEARERS_REFERER = "http://search.kmb.hk/KMBWebSite/index.aspx?lang=tc";
        String KMB_MOBILE = "http://m.kmb.hk/tc/result.html?busno=";
    }

    public interface Connection {
        // Headers
        String HEADERS_REFERER = "Referer";
        String HEADERS_X_REQUESTED_WITH = "X-Requested-With";
        String HEADERS_PRAGMA = "Pragma";

        // Headers value
        String HEADERS_XMLHTTPREQUEST = "XMLHttpRequest";
        String HEADERS_NO_CACHE = "no-cache";

        // HTTP Status
        int HTTP_STATUS_OK = 200;
    }

    public interface Eta {
        int LOADING = 1;
        int SUCCESS = 2;
        int ERROR = 3;
        int NODATA = 4;
    }

    public interface Kmb {
        // Search Query Key
        String ACTION = "action";
        String LANG = "lang";
        String ROUTE = "route";
        String BOUND = "bound";
        String SERVICE_TYPE = "serviceType";
        String BSI_CODE = "bsiCode";
        String SEQ = "seq";
        
        // Search Query Value
        String GET_ROUTE_BOUND = "getRouteBound";
        String GET_SPECIAL_ROUTE = "getSpecialRoute";
        String GET_STOPS = "getStops";
        String GET_ETA = "getEta";
        String LANG_TC_1 = "1";
    }
}
