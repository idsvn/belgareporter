package be.belga.reporter.mobile.reporter.analytics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.utils.StringUtils;

public class AnalyticsManager {

    private static AnalyticsManager sharedInstance = new AnalyticsManager();
    private HashSet<Analytic> analytics = new HashSet<>();


    private AnalyticsManager() {

    }

    public static AnalyticsManager getInstance() {
        return sharedInstance;
    }

    public void addAnalytic(Analytic analytic) {
        analytics.add(analytic);
    }


    public void removeAnalytic(Analytic analytic) {
        analytics.remove(analytic);
    }

    public void initialize(HashMap<String, String> data) {
        for (Analytic analytic : analytics) {
            analytic.initialize(data);
        }
    }

    public void addUserData(HashMap<String, String> data) {
        for (Analytic analytic : analytics) {
            analytic.addUserData(data);
        }
    }


    public void start() {
        for (Analytic analytic : analytics) {
            analytic.start();
        }
    }

    public void stop() {
        for (Analytic analytic : analytics) {
            analytic.stop();
        }
    }

    public void openScreen(String screenName) {
        trackEvent(screenName, null);
    }

    public void openScreen(String screenName, Map<String, String> data) {
        if (StringUtils.isNullOrEmpty(screenName)) ;
        for (Analytic analytic : analytics) {
            analytic.openScreen(screenName, data);
        }
    }

    public void trackEvent(String eventName) {
        trackEvent(eventName, null);
    }

    public void trackEvent(String eventName, Map<String, String> data) {
        if (StringUtils.isNullOrEmpty(eventName)) ;
        for (Analytic analytic : analytics) {
            analytic.trackEvent(eventName, data);
        }
    }

    public void onDestroy() {
        for (Analytic analytic : analytics) {
            analytic.onDestroy();
        }
    }


    public void signout() {
        for (Analytic analytic : analytics) {
            analytic.signout();
        }
    }


    public void onServerChange(boolean isDev) {
        for (Analytic analytic : analytics) {
            analytic.onServerChange(ReporterApplication.getInstance(), isDev);
        }
    }


}
