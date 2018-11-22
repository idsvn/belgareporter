package be.belga.reporter.mobile.reporter.network;

import android.util.Log;

public class APIUrls {
    private static final String TAG = APIUrls.class.getSimpleName();

    private static String HTTP = "http://";

    private static String BASE_URL = "118.69.134.86:8106/reporter/";

    private static String getFullBaseUrlUser() {
        return HTTP + BASE_URL + "user/";
    }

    public static String getLoginUrl() {
        Log.i(TAG, getFullBaseUrlUser() + "login");
        return getFullBaseUrlUser() + "login";
    }

    public static String getPostUrl() {
        Log.i(TAG, HTTP + BASE_URL + "post/create");
        return HTTP + BASE_URL + "post/create";
    }

    public static String getUploadFileUrl() {
        Log.i(TAG, HTTP + BASE_URL + "file");
        return HTTP + BASE_URL + "file";
    }

    public static String getUploadFileUrl(int fileID) {
        Log.i(TAG, HTTP + BASE_URL + "file/" + fileID);
        return HTTP + BASE_URL + "file/" + fileID;
    }

    // Created by Tai 21/11/2018
    public static String getUploadUserUrl() {
        Log.i(TAG, getFullBaseUrlUser() + "update-profile");
        return getFullBaseUrlUser() + "update-profile";
    }
}
