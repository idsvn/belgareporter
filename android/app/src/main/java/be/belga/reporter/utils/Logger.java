package be.belga.reporter.utils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Logger {
    public static final boolean DEBUG = false;
    private static final String TAG = Logger.class.getSimpleName();

    public static void i(String tag, String message) {
        if (DEBUG) Log.i(tag, message);
    }

    public static void i(String tag, String message, Throwable throwable) {
        if (DEBUG) Log.i(tag, message, throwable);
    }

    public static void d(String tag, String message) {
        if (DEBUG) Log.d(tag, message);
    }

    public static void d(String tag, String message, Throwable throwable) {
        if (DEBUG) Log.d(tag, message, throwable);
    }

    public static void e(String tag, String message) {
        if (DEBUG) Log.e(tag, message);
    }

    public static void e(String tag, String message, Throwable throwable) {
        if (DEBUG) Log.e(tag, message, throwable);
    }


    public static void v(String tag, String message) {
        if (DEBUG) Log.v(tag, message);
    }

    public static void v(String tag, String message, Throwable throwable) {
        if (DEBUG) Log.v(tag, message, throwable);
    }

    public static void writeToFile(String message) {
        writeToFile(message, "/sdcard/mysdfile.html");
    }

    public static void writeToFile(String message, String filePath) {
        File myFile = new File(filePath);
        FileOutputStream outputStream = null;
        OutputStreamWriter myOutWriter = null;
        try {
            outputStream = new FileOutputStream(myFile);
            myOutWriter = new OutputStreamWriter(outputStream);
            myOutWriter.append(message);
            myOutWriter.close();
            outputStream.close();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (myOutWriter != null) {
                try {
                    myOutWriter.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }
}
