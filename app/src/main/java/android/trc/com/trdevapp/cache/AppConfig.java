package android.trc.com.trdevapp.cache;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.trc.com.trdevapp.constants.Platform;

public class AppConfig {

    public static final String KEY_PLATFORM = "platform";

    public static void init(Application application) {
        sSharedPreferences = application.getSharedPreferences("AppConfig.xml", Context.MODE_PRIVATE);
    }

    private static SharedPreferences sSharedPreferences;

    public static void setPlatform(String platform) {
        sSharedPreferences.edit().putString(KEY_PLATFORM, platform).apply();
    }

    public static String getPlatform() {
        return sSharedPreferences.getString(KEY_PLATFORM, Platform.FINANCE);
    }
}
