package be.belga.reporter.mobile.reporter.screens.setting;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import belga.be.belgareporter.R;

public class NetworkFragment extends DialogFragment {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String TAG = NetworkFragment.class.getSimpleName();
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    // The user's current network preference setting.
    public static String sPref = null;
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();

    private MainActivity mainActivity;

    private RadioGroup containerRadio;
    private RadioButton radioAnyNetwork;
    private RadioButton radioWifiNetwork;
    private Button btnDone;

    public static NetworkFragment getInstance() {
        NetworkFragment fragment = new NetworkFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        mainActivity.registerReceiver(receiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        containerRadio = (RadioGroup) view.findViewById(R.id.container_radio);
        radioAnyNetwork = (RadioButton) view.findViewById(R.id.radio_any_network);
        radioWifiNetwork = (RadioButton) view.findViewById(R.id.radio_wifi_network);
        btnDone = (Button) view.findViewById(R.id.btn_done);

        getDialog().setTitle(getString(R.string.upload_setting));

        containerRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton buttonChecked = (RadioButton) group.findViewById(checkedId);

                if (buttonChecked != null) {
                    switch (buttonChecked.getId()) {
                        case R.id.radio_wifi_network:
                            Toast.makeText(getActivity(), "wifi", Toast.LENGTH_LONG).show();
                            break;
                        case R.id.radio_any_network:
                            Toast.makeText(getActivity(), "any", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregisters BroadcastReceiver when app is destroyed.
        if (receiver != null) {
            mainActivity.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        updateConnectedFlags();

        if (refreshDisplay) {
            loadPage();
        }
    }

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    public void updateConnectedFlags() {
        ConnectivityManager connMgr = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    // Uses AsyncTask subclass to download the XML feed from stackoverflow.com.
    public void loadPage() {
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            Toast.makeText(mainActivity, sPref, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mainActivity, sPref, Toast.LENGTH_LONG).show();

        }
    }

}
