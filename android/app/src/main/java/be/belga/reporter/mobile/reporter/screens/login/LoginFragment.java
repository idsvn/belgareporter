package be.belga.reporter.mobile.reporter.screens.login;

import android.app.ActionBar;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.belga.reporter.mobile.reporter.analytics.AnalyticsManager;
import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.network.APIUrls;
import be.belga.reporter.mobile.reporter.network.HttpClient;
import be.belga.reporter.mobile.reporter.network.ReporterJsonHttpResponseHandler;
import be.belga.reporter.mobile.views.IconTextView;
import belga.be.belgareporter.R;
import cz.msebera.android.httpclient.Header;

/**
 * A login screen that offers login via email/password.
 */
public class LoginFragment extends Fragment implements LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final String TAG = LoginFragment.class.getSimpleName();

    private Toolbar mToolbar;
    private IconTextView btnBack;
    private TextView tvShowPassword;
    private EditText editEmail;
    private EditText editPassword;
    private Button btnLogin;
    private Button btnForgotPassword;
    private StartActivity startActivity;
    private View mProgressView;

    public static LoginFragment getInstance() {
        LoginFragment instance = new LoginFragment();
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
        View result = inflater.inflate(R.layout.fragment_login, container, false);

        mToolbar = result.findViewById(R.id.login_toolbar);
        btnBack = result.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        tvShowPassword = result.findViewById(R.id.tv_show_password);
        tvShowPassword.setOnClickListener(this);
        editEmail = result.findViewById(R.id.edittext_email);
        editPassword = result.findViewById(R.id.edittext_password);
        btnLogin = result.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        btnForgotPassword = result.findViewById(R.id.btn_forgot_password);
        btnForgotPassword.setOnClickListener(this);
        mProgressView = result.findViewById(R.id.login_progress);

        setupToolbar();

        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvShowPassword.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private void attemptLogin() {
        editEmail.setError(null);
        editPassword.setError(null);

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Store values at the time of the login attempt.
        final String email = editEmail.getText().toString();
        final String password = editPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            editPassword.setError(getString(R.string.error_invalid_password));
            tvShowPassword.setVisibility(View.GONE);
            focusView = editPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            editPassword.setError(getString(R.string.error_field_required));
            tvShowPassword.setVisibility(View.GONE);
            focusView = editPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            editEmail.setError(getString(R.string.error_field_required));
            focusView = editEmail;
            cancel = true;
        }

        if (cm.getActiveNetworkInfo() == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.no_internet_connection_message)
                    .setCancelable(false)
                    .setTitle("Connection error")
                    .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            if (cancel) {
                focusView.requestFocus();
            } else {
                startActivity.showProgress(true, mProgressView);
                try {
                    JSONObject requestParams = new JSONObject();
                    requestParams.put("username", email);
                    requestParams.put("password", password);
                    requestParams.put("email", email);
                    HttpClient.post(getActivity(), APIUrls.getLoginUrl(), requestParams, false,
                            new ReporterJsonHttpResponseHandler(getActivity(), getString(R.string.some_error_occurred)) {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        if (response.getBoolean("success")) {
//                                String token = response.getString("token");
                                            JSONObject data = response.getJSONObject("data");
                                            JSONObject userMetadataData = data.getJSONObject("userMetadata");

                                            ReporterApplication.getInstance().setToken("");
                                            ReporterApplication.getInstance().setUser(data.toString());
                                            ReporterApplication.getInstance().setUserMetadata(userMetadataData.toString());

                                            startActivity.showHomepage();

                                            // for Analytic only
                                            HashMap<String, String> logData = new HashMap<>();
                                            logData.put("username", email);
                                            AnalyticsManager.getInstance().trackEvent("Login successful", logData);
                                        } else {
                                            String errorMsg = response.optString("userDisplayError", getString(R.string.some_error_occurred));
                                            if ("Incorrect username or password!".equals(errorMsg)) {
                                                editEmail.setError(errorMsg);
                                                editPassword.setError(errorMsg);
                                                editPassword.requestFocus();
                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setMessage(errorMsg)
                                                        .setCancelable(true)
                                                        .setTitle(R.string.app_name)
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.dismiss();
                                                                    }
                                                                }

                                                        );
                                                final AlertDialog alert = builder.create();
                                                alert.show();
                                            }

                                            // for Analytic only
                                            HashMap<String, String> logData = new HashMap<>();
                                            logData.put("username", email);
                                            AnalyticsManager.getInstance().trackEvent("Login failed", logData);
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage(), e);
                                    }

                                    startActivity.showProgress(false, mProgressView);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                                    super.onFailure(statusCode, headers, error, responseBody);
                                    startActivity.showProgress(false, mProgressView);

                                    try {
                                        if (responseBody != null) {
                                            startActivity.showErrorMessage(responseBody.get("errorMessage").toString());
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage(), e);
                                    }

                                    if (responseBody != null) {
                                        Log.e("Login", responseBody.toString());
                                    } else if (error != null) {
                                        Log.e("Login", error.getMessage());
                                    }

                                    HashMap<String, String> logData = new HashMap<>();
                                    logData.put("email", email);

                                    AnalyticsManager.getInstance().trackEvent("Login failed", logData);
                                }
                            }, true);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                getFragmentManager().popBackStackImmediate();
                break;
            case R.id.btn_login:
                attemptLogin();
                if (editPassword.getError() == null) {
                    tvShowPassword.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_show_password:
                if (tvShowPassword.getText().equals(getString(R.string.hidden_password_icon))) {
                    tvShowPassword.setText(R.string.show_password_icon);
                    editPassword.setTransformationMethod(null);
                } else {
                    tvShowPassword.setText(R.string.hidden_password_icon);
                    editPassword.setTransformationMethod(new PasswordTransformationMethod());
                }

                editPassword.setSelection(editPassword.getText().length());
                break;
            case R.id.btn_forgot_password:
                startActivity.openForgotPasswordFragment();
                break;
        }
    }

    private void setupToolbar() {
        startActivity.setActionBar(mToolbar);

        ActionBar actionBar = startActivity.getActionBar();

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}