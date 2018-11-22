package be.belga.reporter.mobile.reporter.screens.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import belga.be.belgareporter.R;

/**
 * Created by vinh.bui on 6/1/2018.
 */

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin;
    private FrameLayout mProgressView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ReporterApplication.getInstance().getUser() != null) {
            showHomepage(getIntent().getBooleanExtra("open_notifications", false));
            return;
        }

        setContentView(R.layout.activity_start);
        mProgressView = (FrameLayout) findViewById(R.id.loading_progress_layout);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getWindow().setStatusBarColor(this.getResources().getColor(android.R.color.black));
        getSupportActionBar().hide();
        return true;
    }

    public void showHomepage() {
        showHomepage(false);
    }

    public void showHomepage(boolean openNotification) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("open_notification", openNotification);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void openLoginFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack("MyFacilitiesListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = fm.beginTransaction();
        LoginFragment signupFragment = LoginFragment.getInstance();
        ft.add(R.id.container_main, signupFragment);
        ft.addToBackStack("SignupFragment");
        ft.commit();
    }

    public void openForgotPasswordFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack("MyFacilitiesListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction ft = fm.beginTransaction();
        ForgotPasswordFragment forgotPasswordFragment = ForgotPasswordFragment.getInstance();
        ft.add(R.id.container_main, forgotPasswordFragment);
        ft.addToBackStack("ForgotPasswordFragment");
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                openLoginFragment();
                break;
        }
    }

    public void showErrorMessage(String message) {
        showProgress(false);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(true)
                .setTitle(R.string.app_name)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
    }
}
