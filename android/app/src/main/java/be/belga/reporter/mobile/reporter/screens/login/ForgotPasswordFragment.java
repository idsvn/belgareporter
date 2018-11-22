package be.belga.reporter.mobile.reporter.screens.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import be.belga.reporter.mobile.reporter.application.ReporterFragment;
import belga.be.belgareporter.R;

public class ForgotPasswordFragment extends ReporterFragment {

    private StartActivity startActivity;

    public static ForgotPasswordFragment getInstance() {
        ForgotPasswordFragment instance = new ForgotPasswordFragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity = (StartActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View result = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        return result;
    }

    @Override
    protected String getAnalyticName() {
        return null;
    }

    @Override
    protected HashMap<String, String> getAnalyticParameters() {
        return null;
    }
}
