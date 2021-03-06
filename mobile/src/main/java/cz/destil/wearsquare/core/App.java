package cz.destil.wearsquare.core;

import android.app.Application;

import com.crittercism.app.Crittercism;

import cz.destil.wearsquare.data.Preferences;

/**
 * App instance.
 *
 * @author David Vávra (david@vavra.me)
 */
public class App extends Application {

    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Crittercism.initialize(getApplicationContext(), "53e294fd178784226a000002");
        sInstance = this;
        Preferences.init();
    }

    public static App get() {
        return sInstance;
    }
}
