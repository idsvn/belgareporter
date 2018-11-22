package be.belga.reporter.mobile.reporter.network;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import be.belga.reporter.utils.Logger;
import belga.be.belgareporter.R;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by vinh.bui on 6/21/2018.
 */

public class HttpClient {
    public static final int FORCE_LOGOUT_CODE = 502;
    private static final String TAG = HttpClient.class.getSimpleName();
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient syncHttpClient = new SyncHttpClient();

    static {
        client.setTimeout(30000);
    }

    private boolean isInit = false;

    public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        getWithFullURL(url, params, responseHandler);

    }

    public static void getWithFullURL(String url, RequestParams params, final JsonHttpResponseHandler responseHandler) {
        getWithFullURL(url, params, responseHandler, false);
    }

    public static void getWithFullURL(String url, RequestParams params, final JsonHttpResponseHandler responseHandler, boolean newClient) {
        if (newClient) {
            AndroidNetworking.get(url)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            responseHandler.onSuccess(200, null, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("HttpClient", anError.getErrorBody());
                        }
                    });
        } else {
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject responseJSON) {
                    responseHandler.onSuccess(statusCode, headers, responseJSON);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                    responseHandler.onFailure(statusCode, headers, error, responseBody);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable error) {
                    onFailure(statusCode, headers, error, new JSONObject());
                }
            });
        }
    }

    public static void patch(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        patch(url, params, responseHandler, false);
    }

    public static void patch(String url, RequestParams params, final JsonHttpResponseHandler responseHandler, boolean isV2) {
        client.patch(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJSON) {

                responseHandler.onSuccess(statusCode, headers, responseJSON);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject responseBody) {
                responseHandler.onFailure(statusCode, headers, error, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable error) {
                onFailure(statusCode, headers, error, new JSONObject());
            }
        });


    }

    public static void post(Context context, String url, JSONObject jsonParams, JsonHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException {

        post(context, url, jsonParams, true, responseHandler, false);
    }

    public static void post(Context context, final String url, JSONObject jsonParams, boolean shouldVerifyToken, final JsonHttpResponseHandler responseHandler, boolean isV2) throws JSONException, UnsupportedEncodingException {
        Log.i("post", url);
        String token = ReporterApplication.getInstance().getToken();
        Header[] headers = null;

        if (token != null)
            if (isV2) {
            } else {
                jsonParams.put("token", token);
            }
        StringEntity entity = new StringEntity(jsonParams.toString());
        if (Logger.DEBUG && jsonParams != null)
            Logger.i("HTTPCLIENT", "Posting data " + jsonParams);
        if (Logger.DEBUG) Logger.i("HTTPCLIENT", "Posting url " + url);
        if (Logger.DEBUG) Logger.i("HTTPCLIENT", url);

        client.post(context, url, headers, entity, "application/json", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                if (Logger.DEBUG) Log.i("HTTPCLIENT", url + " result:" + response);
                if (!response.optBoolean("success", true)) {
                    responseHandler.onFailure(statusCode, headers, new Exception("Reserv Error"), response);
                    return;
                }
                responseHandler.onSuccess(statusCode, headers, response);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("HTTPCLIENT", "Posting url " + url + "\n" + errorResponse, throwable);
                responseHandler.onFailure(statusCode, headers, throwable, errorResponse);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable error) {
                Log.e("HTTPCLIENT", "Posting url " + url, error);
                onFailure(statusCode, headers, error, new JSONObject());
            }
        });
    }

    public static void put(FragmentActivity context, final String url, JSONObject jsonParams, final ReporterJsonHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException {
        Log.i("post", url);
        String token = ReporterApplication.getInstance().getToken();
        Header[] headers = null;

        jsonParams.put("token", token);
        StringEntity entity = new StringEntity(jsonParams.toString());
        if (Logger.DEBUG && jsonParams != null)
            Logger.i("HTTPCLIENT", "Posting data " + jsonParams);
        if (Logger.DEBUG) Logger.i("HTTPCLIENT", "Posting url " + url);
        if (Logger.DEBUG) Logger.i("HTTPCLIENT", url);

        client.put(context, url, headers, entity, "application/json", new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                if (Logger.DEBUG) Log.i("HTTPCLIENT", url + " result:" + response);
                if (!response.optBoolean("success", true)) {
                    responseHandler.onFailure(statusCode, headers, new Exception("Reserv Error"), response);
                    return;
                }
                responseHandler.onSuccess(statusCode, headers, response);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("HTTPCLIENT", "Posting url " + url + "\n" + errorResponse, throwable);
                responseHandler.onFailure(statusCode, headers, throwable, errorResponse);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable error) {
                Log.e("HTTPCLIENT", "Posting url " + url, error);
                onFailure(statusCode, headers, error, new JSONObject());
            }
        });
    }

    public static void uploadFile(Context context, String url, Uri uri, String key, String filename, AsyncHttpResponseHandler responseHandler) {
        InputStream myInputStream = null;
        try {
            myInputStream = context.getContentResolver().openInputStream(uri);
            RequestParams params = new RequestParams();
            params.put(key, myInputStream, filename, "image/jpg", true);

            client.post(context, url, params, responseHandler);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static ShowErrorResult checkAndAlertReservErrors(Context context, byte[] responseBody) {
        JSONObject responseJSON = null;
        try {

            if (responseBody == null) {
                return new ShowErrorResult(false, null);
            }

            responseJSON = new JSONObject(new String(responseBody));
            return checkAndAlertReservErrors(context, responseJSON);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return new ShowErrorResult(false, null);
    }

    public static ShowErrorResult checkAndAlertReservErrors(final Context context, JSONObject responseJSON) {

        return checkAndAlertReservErrors(context, responseJSON, false);
    }

    public static ShowErrorResult checkAndAlertReservErrors(final Context context, JSONObject responseJSON, final boolean forceLogout) {

        if (responseJSON == null) {
            return new ShowErrorResult(false, null);
        }

        String userDisplayError = responseJSON.optString("userDisplayError", null);
        if (userDisplayError != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(userDisplayError)
                    .setCancelable(true)
                    .setTitle(R.string.app_name)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    if (forceLogout && context instanceof MainActivity) {
                                        ((MainActivity) context).signout();
                                    }
                                }
                            }

                    );
            final AlertDialog alert = builder.create();
            alert.show();
            return new ShowErrorResult(true, userDisplayError);
        }
        return new ShowErrorResult(false, null);
    }

    public static void setToken(String token) {
        client.addHeader("Authorization", token);
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        syncHttpClient.addHeader("Authorization", token);
        syncHttpClient.addHeader("Accept", "application/json");
        syncHttpClient.addHeader("Content-Type", "application/json");
    }

    public static class ShowErrorResult {
        private boolean isSuccessful;
        private String errorMsg;

        public ShowErrorResult(boolean isSuccessful, String errorMsg) {
            this.isSuccessful = isSuccessful;
            this.errorMsg = errorMsg;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }
    }

}
