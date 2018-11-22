package be.belga.reporter.mobile.reporter.analytics;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

public abstract class Analytic {

    public static final String USERNAME_KEY = "username";

    private String name;

    public Analytic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract void initialize(Map<String, String> data);

    abstract void addUserData(HashMap<String, String> data);

    abstract void start();

    abstract void stop();

    abstract void signout();

    abstract void openScreen(String screenName, Map<String, String> data);

    abstract void trackEvent(String eventName, Map<String, String> data);

    abstract void onDestroy();

    public void onServerChange(Application application, boolean isDev) {

    }

}
