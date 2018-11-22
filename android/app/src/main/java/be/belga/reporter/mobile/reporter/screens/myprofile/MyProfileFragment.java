package be.belga.reporter.mobile.reporter.screens.myprofile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import be.belga.reporter.mobile.reporter.analytics.AnalyticsManager;
import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.application.ReporterFragment;
import be.belga.reporter.mobile.reporter.manager.PostManager;
import be.belga.reporter.mobile.reporter.model.User;
import be.belga.reporter.mobile.reporter.network.APIUrls;
import be.belga.reporter.mobile.reporter.network.HttpClient;
import be.belga.reporter.mobile.reporter.network.ReporterJsonHttpResponseHandler;
import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import belga.be.belgareporter.R;
import cz.msebera.android.httpclient.Header;

public class MyProfileFragment extends ReporterFragment {
    private static final String TAG = MyProfileFragment.class.getSimpleName();

    private MainActivity mainActivity;

    private User user;

    private TextView userFullName;
    private TextView txtEmail;
    private Button btnLogout;

    //    Created by Tai 20/11/2018
    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtEmail;
    private RadioButton rdMale;
    private RadioButton rdFemale;
    private Button btnSave;

    private LinearLayout edit_layout;
    private LinearLayout show_layout;
    private boolean show = true;

    public static MyProfileFragment getInstance() {
        MyProfileFragment fragment = new MyProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        user = ReporterApplication.getInstance().getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_my_profile, container, false);

        show_layout = (LinearLayout) result.findViewById(R.id.layout_show_profile);

        userFullName = (TextView) result.findViewById(R.id.textview_user_name);
        txtEmail = (TextView) result.findViewById(R.id.textview_user_email);
        btnLogout = (Button) result.findViewById(R.id.btn_logout);

        userFullName.setText(user.getFirstName() + " " + user.getLastName());
        txtEmail.setText(user.getEmail());

        //    Created by Tai 20/11/2018
        edit_layout = (LinearLayout) result.findViewById(R.id.layout_edit_profile);

        edtFirstName = (EditText) result.findViewById(R.id.edittext_first_name);
        edtLastName = (EditText) result.findViewById(R.id.edittext_last_name);
        edtEmail = (EditText) result.findViewById(R.id.edittext_user_email);
        rdMale = (RadioButton) result.findViewById(R.id.rd_male);
        rdFemale = (RadioButton) result.findViewById(R.id.rd_female);
        btnSave = (Button) result.findViewById(R.id.btn_save);

        edtFirstName.setText(user.getFirstName());
        edtLastName.setText(user.getLastName());
        edtEmail.setText(user.getEmail());

        if (user.getGender().equals("MALE")) {
            rdMale.setChecked(true);
        } else
            rdFemale.setChecked(true);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.signout();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fisrtName = edtFirstName.getText().toString();
                final String lastName = edtLastName.getText().toString();
                final String email = edtEmail.getText().toString();
                final String gender;
                if (rdMale.isChecked())
                    gender = "MALE";
                else
                    gender = "FEMALE";
                try {
                    JSONObject requestParams = new JSONObject();
                    requestParams.put("firstName", fisrtName);
                    requestParams.put("lastName", lastName);
                    requestParams.put("email", email);
                    requestParams.put("gender", gender);
                    requestParams.put("username", user.getUsername());
                    requestParams.put("password", user.getPassword());
                    userFullName.setText(fisrtName + " " + lastName);
                    mainActivity.uploadName(fisrtName, lastName);
                    HttpClient.post(getActivity(), APIUrls.getUploadUserUrl(), requestParams, false,
                            new ReporterJsonHttpResponseHandler(getActivity(), getString(R.string.some_error_occurred)) {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        if (response.getBoolean("success")) {
                                            JSONObject data = response.getJSONObject("data");

                                            ReporterApplication.getInstance().setToken("");
                                            ReporterApplication.getInstance().setUser(data.toString());
                                            ReporterApplication.getInstance().setUpdateUser(data.toString(), fisrtName, lastName, email, gender);

                                            Toast.makeText(getActivity(), "Save profile success", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage(), e);
                                    }
                                }
                            }, true);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });

        setHasOptionsMenu(true);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.menu_nav_profile, menu);

        mainActivity.setTitle(getString(R.string.my_profile));
        mainActivity.setDrawerEnable(false);

        menu.findItem(R.id.edit_menu).setIcon(mainActivity.setFontAwesomeMenu(18, R.string.edit_icon));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mainActivity.isFragmentOnTop(this)) {
            switch (item.getItemId()) {
                case R.id.edit_menu:
                    if (show) {
                        show_layout.setVisibility(View.GONE);
                        edit_layout.setVisibility(View.VISIBLE);
                        btnLogout.setVisibility(View.INVISIBLE);
                        btnSave.setVisibility(View.VISIBLE);
                        show = false;
                    } else {
                        show_layout.setVisibility(View.VISIBLE);
                        edit_layout.setVisibility(View.INVISIBLE);
                        btnLogout.setVisibility(View.VISIBLE);
                        btnSave.setVisibility(View.INVISIBLE);
                        show = true;
                    }
                    return true;
                case android.R.id.home:
                    mainActivity.hideSoftKeyboard(getActivity());
                    mainActivity.getSupportFragmentManager().popBackStackImmediate();
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
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
