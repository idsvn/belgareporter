package be.belga.reporter.mobile.reporter.application;

import android.support.v4.app.Fragment;

import java.util.HashMap;

import be.belga.reporter.mobile.reporter.analytics.AnalyticsManager;

public abstract class ReporterFragment extends Fragment {

    private boolean isLogging = true;

    public boolean onFinish() {
        return true;
    }

    final public void onVisible() {
        if (isLogging)
            AnalyticsManager.getInstance().openScreen(getAnalyticName(), getAnalyticParameters());
        _onVisible();
    }

    protected void _onVisible() {

    }

    protected abstract String getAnalyticName();

    protected abstract HashMap<String, String> getAnalyticParameters();


    public void setLogging(boolean logging) {
        isLogging = logging;
    }
}
