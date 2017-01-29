package hoo.hktranseta.common;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import hoo.hktranseta.common.customtabs.CustomTabsHelper;

public class Utils {

    /**
     * Method to return current timestamp in second.
     * @return timestamp in second
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * Method to launch a Custom Tabs Activity.
     * @param context The source Context.
     * @param url The URL to load in the Custom Tab.
     * @param id Toolbar color's resource id
     */
    public static void startCustomTabs(Context context, String url, @ColorRes int id) {
        String packageName = CustomTabsHelper.getPackageNameToUse(context);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.setShowTitle(true)
                .addDefaultShareMenuItem()
                .enableUrlBarHiding()
                .setToolbarColor(ContextCompat.getColor(context, id))
                .build();

        if ( packageName != null ) {
            customTabsIntent.intent.setPackage(packageName);
        }

        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    /**
     * Method to convert HK1980GRID coordinates to WGS84GEO coordinates
     * Source: https://github.com/helixcn/HK80/blob/master/R/HK1980GRID_TO_WGS84GEO.R
     * Verification: https://data.gov.hk/tc/datagovhk-api/coordinate-conversion
     * @param n HK1980 Grid Northing
     * @param e HK1980 Grid Easting
     * @return WGS84 Geometric coordinates
     */
    public static LatLng hk1980GridToLatLng(double n, double e) {
        //////// HK1980GRID_TO_HK80GEO ////////
        // 1. HK80
        double N0 = 819069.80;
        double E0 = 836694.05;
        //double phi0 = 22.0 + (18.0/60.0) + 43.68/(3600.0);
        double lambda0 = 114.0 + (10.0/60.0) + (42.80/3600.0);
        double m0 = 1.0;
        double M0 = 2468395.723;
        //double niu_s = 6381480.500;
        //double rou_s = 6359840.760;
        //double psi_s = 1.003402560;
        double a = 6378388.0;
        double e2 = 6.722670022e-3;

        double A0 = 1.0 - (e2/4.0) - (3.0*(Math.pow(e2,2.0))/64.0);
        double A2 = (3.0/8.0)*(e2 + (Math.pow(e2,2.0))/4.0);
        double A4 = (15.0/256.0)*(Math.pow(e2,2));
        double delta_N = n - N0;

        // 2. iterations
        double phi_rou;

        double fa = 0.5;
        double fd = 1e-30;
        double fb = ((((delta_N + M0) / m0)/a + A2 * Math.sin(2*fa) - A4 * Math.sin(4*fa))/A0 );
        int k = 0;
        while (( (fa-fb) > fd) || ((fa-fb) < -1 * fd)){
            fa = fb;
            fb = ((((delta_N + M0) / m0)/a + A2 * Math.sin(2*fa) - A4 * Math.sin(4*fa))/A0 );
            k = k + 1;
            if (k>1000){
                Log.d("hk1980GridToLatLng", "The equation does not converge. Computation Stopped.");
                fb = 0;
                break;
            }
        }
        phi_rou = fb;

        // 3. Verification of the value
        //  a*(A0 * phi_rou - A2 * sin(2 * phi_rou) + A4 * sin(4*phi_rou))
        // (delta_N + M0) / m0

        double t_rou = Math.tan(phi_rou);
        double niu_rou = a/Math.sqrt(1.0 - e2*(Math.pow(Math.sin(phi_rou), 2.0)));
        double rou_rou = a*(1.0 - e2)/(Math.pow(1 - e2*(Math.pow(Math.sin(phi_rou), 2.0)), 1.5));
        double psi_rou = niu_rou/rou_rou;

        double delta_E = e - E0;

        // Equation 4
        double lambda = (lambda0/(180.0/Math.PI) + (1.0/Math.cos(phi_rou))*(delta_E/(m0*niu_rou)) -
                (1/Math.cos(phi_rou))*(Math.pow(delta_E, 3.0)/(6*Math.pow(m0, 3.0)*Math.pow(niu_rou, 3.0)))*(psi_rou + 2*Math.pow(t_rou, 2.0)));

        // Equation 5
        double phi = phi_rou - (t_rou/(m0*rou_rou))*(Math.pow(delta_E, 2.0)/(2*m0*(niu_rou)));

        double latitude = phi*(180.0/Math.PI);
        double longitude = lambda*(180.0/Math.PI);

        //////// HK80GEO_TO_WGS84GEO ////////
        latitude -= (5.5/3600.0);
        longitude += (8.8/3600.0);

        return new LatLng(latitude, longitude);
    }
}
