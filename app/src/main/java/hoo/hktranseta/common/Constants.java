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

    // Share Preferences Key
    public interface Prefs {
        String START_ACTIVITY = "start_activity";

        // GeneralPreferenceFragment
        String ETA_UPDATE_INTERVAL = "eta_update_interval";
        String TESTING = "testing";
        String PARAMETERS = "parameters";
        String APP_VERSION = "app_version";
    }

    // Intent Extra Key
    public interface Extra {
        // WebViewActivity
        String TITLE = "title";
        String URL = "url";
    }

    public interface AppPreferences {
        int getAppMode();
        int getIntFromDefaultSharePreferences(String key, int defaultValue);
        void setIntFromDefaultSharePreferences(String key, int value);
        String getStringFromDefaultSharePreferences(String key, String defaultValue);
        void setStringFromDefaultSharePreferences(String key, String value);
    }
}
