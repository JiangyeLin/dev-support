package android.trc.com.trdevapp;

import android.app.Application;
import android.trc.com.trdevapp.cache.AppConfig;

/**
 * JiangyeLin on 2018/8/20
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.init(this);
    }
}
