package alpvax.rau.application;

import android.app.Application;
import android.content.res.Configuration;
import com.firebase.client.Firebase;

/**
 * Created by Nick on 13/10/15.
 */
public class RauApplication extends Application {

    private static RauApplication instance;

    public RauApplication instance(){
        return instance;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
