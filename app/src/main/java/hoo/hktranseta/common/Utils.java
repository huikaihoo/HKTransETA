package hoo.hktranseta.common;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;

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
}
