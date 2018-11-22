package be.belga.reporter.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.text.DecimalFormat;

public class StringUtils {


    public static String stringArrayToString(String[] input, String seperator) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < input.length; i++) {
            if (i > 0) strBuilder.append(seperator);
            strBuilder.append(input[i]);
        }
        return strBuilder.toString();
    }

    public static boolean isNullOrEmpty(String source) {
        return (source == null || source.isEmpty());
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) return null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }
    }


    public static String formatMoneyValue(double value) {

        if (value == Math.floor(value)) {
            return "" + ((int) value);
        }

        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getMimeTypeFromFileName(String filename) {
        String type = null;
        String extension = getFileExtension(filename);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    public static String getFileExtension(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            int dotPos = filename.lastIndexOf('.');
            if (0 <= dotPos) {
                return filename.substring(dotPos + 1);
            }
        }
        return "";
    }

    public static int versionCompare(String str1, String str2) {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version
        // string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        else {
            return Integer.signum(vals1.length - vals2.length);
        }
    }


    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }


}
