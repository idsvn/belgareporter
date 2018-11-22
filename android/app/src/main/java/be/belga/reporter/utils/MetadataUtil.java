package be.belga.reporter.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.model.Metadata;
import be.belga.reporter.mobile.reporter.model.Post;
import belga.be.belgareporter.R;

public class MetadataUtil {
    public static void setDataMetadata(Post post,
                                       String package_,
                                       String urgency,
                                       String distribition,
                                       String language,
                                       String keywords,
                                       String iptc,
                                       String author,
                                       String label,
                                       String status,
                                       String city,
                                       String country,
                                       String editorial,
                                       String info,
                                       String credit,
                                       String source,
                                       String comment) {
        post.getMetadata().setPackage_(package_);
        post.getMetadata().setUrgency(urgency);
        post.getMetadata().setDistribition(distribition);
        post.getMetadata().setLanguage(language);
        post.getMetadata().setKeywords(keywords);
        post.getMetadata().setIptc(iptc);
        post.getMetadata().setAuthor(author);
        post.getMetadata().setLabel(label);
        post.getMetadata().setStatus(status);
        post.getMetadata().setCity(city);
        post.getMetadata().setCountry(country);
        post.getMetadata().setEditorial(editorial);
        post.getMetadata().setInfo(info);
        post.getMetadata().setCredit(credit);
        post.getMetadata().setSource(source);
        post.getMetadata().setComment(comment);
    }

    public static void setDataMetadata(Metadata metadata,
                                       String package_,
                                       String urgency,
                                       String distribition,
                                       String language,
                                       String keywords,
                                       String iptc,
                                       String author,
                                       String label,
                                       String status,
                                       String city,
                                       String country,
                                       String editorial,
                                       String info,
                                       String credit,
                                       String source,
                                       String comment) {
        metadata.setPackage_(package_);
        metadata.setUrgency(urgency);
        metadata.setDistribition(distribition);
        metadata.setLanguage(language);
        metadata.setKeywords(keywords);
        metadata.setIptc(iptc);
        metadata.setAuthor(author);
        metadata.setLabel(label);
        metadata.setStatus(status);
        metadata.setCity(city);
        metadata.setCountry(country);
        metadata.setEditorial(editorial);
        metadata.setInfo(info);
        metadata.setCredit(credit);
        metadata.setSource(source);
        metadata.setComment(comment);
    }

    public static void addMetadata(Activity activity, final ViewGroup containerView, String[] strings, final List<String> stringList) {
        LayoutInflater inflater = activity.getLayoutInflater();
        containerView.clearFocus();

        for (String str : strings) {
            final View selectionItem = inflater.inflate(R.layout.list_item_package, containerView, false);
            ((TextView) selectionItem.findViewById(R.id.txtview_metadata)).setText(str);
            ((ImageButton) selectionItem.findViewById(R.id.btn_delete)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    containerView.removeView(selectionItem);
                    stringList.remove(((TextView) selectionItem.findViewById(R.id.txtview_metadata)).getText().toString());
                }
            });

            if (!stringList.contains(str)) {
                stringList.add(str);
                containerView.addView(selectionItem);
            }
        }
    }

    public static void addMetadata(Activity activity, final ViewGroup containerView, String tvText, String spnText, final List<String> stringList) {
        LayoutInflater inflater = activity.getLayoutInflater();

        String str = tvText + "(" + spnText + ")";

        final View selectionItem = inflater.inflate(R.layout.list_item_package, containerView, false);
        ((TextView) selectionItem.findViewById(R.id.txtview_metadata)).setText(str);
        ((ImageButton) selectionItem.findViewById(R.id.btn_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                containerView.removeView(selectionItem);
                stringList.remove(((TextView) selectionItem.findViewById(R.id.txtview_metadata)).getText().toString());
            }
        });

        if (!stringList.contains(str)) {
            stringList.add(str);
            containerView.addView(selectionItem);
        }

    }

    public static String getValueMetadata(ViewGroup containerView) {
        String str = "";
        for (int i = 0; i < containerView.getChildCount(); i++) {
            TextView textView = (TextView) ((ViewGroup) containerView.getChildAt(i)).getChildAt(0);
            str += textView.getText().toString();
            if (i != containerView.getChildCount() - 1) {
                str += ReporterApplication.SEMICOLON_CHARACTER;
            }
        }

        return str;
    }

    public static String getValueMetadata(String s) {
        if (s == null) return null;
        String string = "";
        for (int i = 0; i < s.split(ReporterApplication.SEMICOLON_CHARACTER).length; i++) {
            string += s.split(ReporterApplication.SEMICOLON_CHARACTER)[i];
            if (i != s.split(ReporterApplication.SEMICOLON_CHARACTER).length - 1) {
                string += "\n";
            }
        }

        return string;
    }

    public static void setValueSpinner(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
            }
        }
    }

}
