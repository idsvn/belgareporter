package be.belga.reporter.mobile.reporter.network;

import android.app.Activity;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import be.belga.reporter.utils.StringUtils;
import cz.msebera.android.httpclient.Header;

public class ReporterJsonHttpResponseHandler extends JsonHttpResponseHandler {

    private Activity activity;

    private String showIfNoUserDisplayError;

    private String errorMsg = null;

    public ReporterJsonHttpResponseHandler(Activity activity) {
        this(activity, null);
    }

    public ReporterJsonHttpResponseHandler(Activity activity, String showIfNoUserDisplayError) {
        this.activity = activity;
        this.showIfNoUserDisplayError = showIfNoUserDisplayError;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        if (activity != null) {
            HttpClient.ShowErrorResult errorResult = HttpClient.checkAndAlertReservErrors(activity, errorResponse, statusCode == HttpClient.FORCE_LOGOUT_CODE);

            if (errorResult.isSuccessful()) {
                errorMsg = errorResult.getErrorMsg();
            } else {
                errorMsg = showIfNoUserDisplayError;
                if (!StringUtils.isNullOrEmpty(showIfNoUserDisplayError)) {
                    Toast.makeText(activity, showIfNoUserDisplayError, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
    }

    public void setShowIfNoUserDisplayError(String showIfNoUserDisplayError) {
        this.showIfNoUserDisplayError = showIfNoUserDisplayError;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
