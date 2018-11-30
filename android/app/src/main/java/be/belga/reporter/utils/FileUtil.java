package be.belga.reporter.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import be.belga.reporter.mobile.reporter.network.APIUrls;
import io.tus.android.client.TusPreferencesURLStore;
import io.tus.java.client.TusClient;
import io.tus.java.client.TusURLStore;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url.replaceAll(" ", ""));
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if (size < sizeMb)
            return df.format(size / sizeKb) + " Kb";
        else if (size < sizeGb)
            return df.format(size / sizeMb) + " Mb";
        else if (size < sizeTerra)
            return df.format(size / sizeGb) + " Gb";

        return "";
    }

    public static int sizeOf(Object object) {
        if (object == null)
            return -1;

        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return byteArray == null ? 0 : byteArray.length;
    }

    public static TusClient getTusClient(Activity activity) {
        TusClient client = null;

        try {
            SharedPreferences pref = activity.getSharedPreferences("reporter", 0);
            pref.edit().clear();
            client = new TusClient();
            client.setUploadCreationURL(new URL(APIUrls.getUploadFileUrl()));
            TusURLStore urlStore = new TusPreferencesURLStore(pref);
            client.enableResuming(urlStore);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return client;
    }

    public static TusURLStore getTusURLStore(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("reporter", 0);
        pref.edit().clear();
        return new TusPreferencesURLStore(pref);
    }

    public static Uri getImageContentUri(Activity activity, File imageFile, String media) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = null;
        switch (media) {
            case "PICTURE":
                cursor = activity.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media._ID},
                        MediaStore.Images.Media.DATA + "=? ",
                        new String[]{filePath}, null);
                break;
            case "VIDEO":
                cursor = activity.getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Video.Media._ID},
                        MediaStore.Video.Media.DATA + "=? ",
                        new String[]{filePath}, null);
                break;
            case "AUDIO":
                cursor = activity.getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Media._ID},
                        MediaStore.Audio.Media.DATA + "=? ",
                        new String[]{filePath}, null);
        }

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            switch (media) {
                case "PICTURE":
                    return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
                case "VIDEO":
                    return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "" + id);
                case "AUDIO":
                    return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + id);
            }

            return null;
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                switch (media) {
                    case "PICTURE":
                        values.put(MediaStore.Images.Media.DATA, filePath);
                        return activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    case "VIDEO":
                        values.put(MediaStore.Video.Media.DATA, filePath);
                        return activity.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                    case "AUDIO":
                        values.put(MediaStore.Audio.Media.DATA, filePath);
                        return activity.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                }

                return null;
            } else {
                return null;
            }
        }
    }

    //Created by Tai 30/11/2018
    public static long getSizePicture(String url,String typeFile) {
        File imgFile = new File(url);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgFile.getAbsolutePath(), opts);
        opts.inJustDecodeBounds = false; // This time it's for real!
        int sampleSize = 1;
        opts.inSampleSize = sampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), opts);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.valueOf(typeFile.substring(6).toUpperCase()),93, stream);
        byte[] imageInByte = stream.toByteArray();
        return imgFile.length();
    }

    public static Bitmap setRotate(Bitmap bitmap, String photoPath){
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                bitmap = bitmap;
        }
        return bitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
