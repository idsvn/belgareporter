package be.belga.reporter.mobile.reporter.screens.mypicture;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.model.FileUpload;
import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import be.belga.reporter.mobile.views.IconTextView;
import be.belga.reporter.utils.FileUtil;
import belga.be.belgareporter.R;

import static be.belga.reporter.utils.FileUtil.calculateInSampleSize;
import static be.belga.reporter.utils.FileUtil.setRotate;

public class PictureGridViewAdapter extends BaseAdapter {
    private static final int TYPE_COUNT = 3;
    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_VIDEO = 1;
    private static final int TYPE_AUDIO = 2;

    private List<FileUpload> files;

    private Activity activity;
    private LayoutInflater inflater;

    private boolean delete = false;
    private int positionDelete;

    public PictureGridViewAdapter(Activity activity, List<FileUpload> files) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.files = files;
        this.activity = activity;
    }

    public void setPicture(List<FileUpload> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (files == null || files.size() == 0) return 1;
        return files.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (files == null || files.size() == 0) return null;
        return files.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (files != null && files.size() != 0 && position != getCount() - 1) {
            switch (files.get(position).getMimetype().split(ReporterApplication.SLASH_CHARACTER)[0]) {
                case "image":
                    return TYPE_IMAGE;
                case "video":
                    return TYPE_VIDEO;
                case "audio":
                    return TYPE_AUDIO;
            }
        }

        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            result = inflater.inflate(R.layout.list_item_picture, null);
            ViewHolder v = new ViewHolder();
            v.imgPicture = (ImageView) result.findViewById(R.id.imageview_picture);
            v.txtPicture = (TextView) result.findViewById(R.id.txtview_picture);
            v.layoutTxtPicture = (LinearLayout) result.findViewById(R.id.layout_txtview_picture);
            v.layoutDeleteItem = (RelativeLayout) result.findViewById(R.id.layout_delete_item);
            result.setTag(v);
        }

        ViewHolder v = (ViewHolder) result.getTag();
        v.imgPicture.requestLayout();
        v.imgPicture.getLayoutParams().height = ((MainActivity) activity).getSizeWindowDisplay().x / 2;
        v.imgPicture.getLayoutParams().width = ((MainActivity) activity).getSizeWindowDisplay().x / 2;

        if (position == files.size()) {
            v.imgPicture.setImageResource(R.mipmap.add_icon);
            v.imgPicture.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            v.layoutTxtPicture.setVisibility(View.GONE);
        } else {
            FileUpload file = files.get(position);
            File imgFile = new File(file.getGeneratedUrl());
            switch (getItemViewType(position)) {
                case TYPE_IMAGE:
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(imgFile.getAbsolutePath(), opts);
                    opts.inJustDecodeBounds = false; // This time it's for real!
                    v.txtPicture.setText(file.getMimetype() + " - " + opts.outWidth+ "x" + opts.outHeight);
                    int sampleSize = calculateInSampleSize(opts, 100, 100); // Calculate your sampleSize here
                    opts.inSampleSize = sampleSize;
                    Bitmap bitmapImage = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),opts);

                    v.layoutTxtPicture.setVisibility(View.VISIBLE);
                    v.imgPicture.setScaleType(ImageView.ScaleType.FIT_XY);
                    v.imgPicture.setImageBitmap(setRotate(bitmapImage,imgFile.getAbsolutePath()));//Added by Tai 30/11/2018
                    break;
                case TYPE_VIDEO:
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(file.getGeneratedUrl(), MediaStore.Images.Thumbnails.MINI_KIND);
                    v.imgPicture.setImageDrawable(new BitmapDrawable(activity.getResources(), thumb));
                    v.txtPicture.setText(file.getMimetype() + " - " + FileUtil.getStringSizeLengthFile(file.getSize()));
                    break;
                case TYPE_AUDIO:
                    IconTextView tv = new IconTextView(activity);
                    tv.setText(activity.getString(R.string.audio_circle_icon));
                    tv.setTextColor(activity.getResources().getColor(R.color.nobel));
                    tv.setTextSize(80);
                    tv.setDrawingCacheEnabled(true);
                    tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    tv.layout(0, 0, v.imgPicture.getLayoutParams().width / 2, v.imgPicture.getLayoutParams().height / 2);
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    tv.buildDrawingCache(true);
                    Bitmap b = Bitmap.createBitmap(tv.getDrawingCache());
                    tv.setDrawingCacheEnabled(false);

                    v.imgPicture.setImageDrawable(new BitmapDrawable(activity.getResources(), b));
                    v.imgPicture.setScaleType(ImageView.ScaleType.CENTER);
                    v.txtPicture.setText(file.getMimetype() + " - " + FileUtil.getStringSizeLengthFile(file.getSize()));
                    break;
            }

        }

        return result;
    }

    private class ViewHolder {
        ImageView imgPicture;
        TextView txtPicture;
        LinearLayout layoutTxtPicture;
        RelativeLayout layoutDeleteItem;
    }
}