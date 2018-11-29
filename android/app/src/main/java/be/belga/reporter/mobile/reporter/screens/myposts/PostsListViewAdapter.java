package be.belga.reporter.mobile.reporter.screens.myposts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.model.FileUpload;
import be.belga.reporter.mobile.reporter.model.Post;
import be.belga.reporter.mobile.views.IconTextView;
import be.belga.reporter.utils.FileUtil;
import belga.be.belgareporter.R;

/**
 * Created by vinh.bui on 6/18/2018.
 */

public class PostsListViewAdapter extends BaseAdapter {
    private static final int PARAM_DEFAULT = 0;
    private static final int PARAM_PUBLISHED = 1;
    private static final int PARAM_UPLOADING = 2;

    private List<Post> posts;

    private LayoutInflater inflater;

    private String percent = "";
    private int mPosition = -1;

    private Context context;

    public PostsListViewAdapter(Context context, List<Post> posts) {
        this.posts = posts;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (posts == null) return 0;
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (posts.get(position).getWorkflowStatus().equals(Post.PostWorkflowStatus.PUBLISHED)) {
            return PARAM_PUBLISHED;
        } else if (posts.get(position).getWorkflowStatus().equals(Post.PostWorkflowStatus.IN_PROGRESS)) {
            return PARAM_UPLOADING;
        }

        return PARAM_DEFAULT;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            result = inflater.inflate(R.layout.list_item_my_posts, null);
            ViewHolder v = new ViewHolder();

            v.ivStatusIcon = (ImageView) result.findViewById(R.id.imageview_status_icon);

            v.imageContainer = (RelativeLayout) result.findViewById(R.id.image_container);
            v.ivImg = (ImageView) result.findViewById(R.id.imageview_image);
            v.videoPlayIcon = (IconTextView) result.findViewById(R.id.video_play_icon);

            v.fontAwesomeContainer = (RelativeLayout) result.findViewById(R.id.font_awesome_container);
            v.fontAwesomeIcon = (IconTextView) result.findViewById(R.id.font_awesome_icon);

            v.txtStatus = (TextView) result.findViewById(R.id.txtview_status);
            v.txtTitle = (TextView) result.findViewById(R.id.txtview_title);
            v.txtCation = (TextView) result.findViewById(R.id.txtview_cation);
            v.txtType = (TextView) result.findViewById(R.id.txtview_type);
            v.txtDimensions = (TextView) result.findViewById(R.id.txtview_dimensions);

            result.setTag(v);
        }

        ViewHolder v = (ViewHolder) result.getTag();

        Post post = posts.get(position);

        v.ivStatusIcon.setImageResource(post.getWorkflowStatus().getIconResource());
        v.txtStatus.setText(post.getWorkflowStatus().getFullStatus());
        v.txtStatus.setTextColor(context.getResources().getColor(post.getWorkflowStatus().getColorResource()));

        v.txtTitle.setText(post.getTitle());
        v.txtCation.setText(post.getCaption());
        v.txtType.setAllCaps(true);

        FileUpload fileUpload = post.getFileUpload();

        switch (post.getType()) {
            case PICTURE:
                v.imageContainer.setVisibility(View.VISIBLE);
                v.fontAwesomeContainer.setVisibility(View.GONE);
                v.videoPlayIcon.setVisibility(View.GONE);

                Bitmap bitmapImage = BitmapFactory.decodeFile(fileUpload.getGeneratedUrl());
//                v.ivImg.setImageBitmap(bitmapImage);
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmapImage,128,128);
                v.ivImg.setImageDrawable(new BitmapDrawable(context.getResources(), thumbImage));
                break;
            case VIDEO:
                v.imageContainer.setVisibility(View.VISIBLE);
                v.fontAwesomeContainer.setVisibility(View.GONE);
                v.videoPlayIcon.setVisibility(View.VISIBLE);

                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(fileUpload.getGeneratedUrl(), MediaStore.Images.Thumbnails.MINI_KIND);
                v.ivImg.setImageDrawable(new BitmapDrawable(context.getResources(), thumb));
                break;
            case AUDIO:
                v.imageContainer.setVisibility(View.GONE);
                v.fontAwesomeContainer.setVisibility(View.VISIBLE);
                v.fontAwesomeIcon.setText(R.string.audio_circle_icon);
                v.fontAwesomeIcon.setTextColor(context.getResources().getColor(R.color.nobel));
                break;
            case SHORT:
                v.imageContainer.setVisibility(View.GONE);
                v.fontAwesomeContainer.setVisibility(View.VISIBLE);
                v.fontAwesomeIcon.setText(R.string.short_icon);
                v.fontAwesomeIcon.setTextColor(context.getResources().getColor(R.color.viking));
                break;
            case ALERT:
                v.imageContainer.setVisibility(View.GONE);
                v.fontAwesomeContainer.setVisibility(View.VISIBLE);
                v.fontAwesomeIcon.setText(R.string.alert_icon);
                v.fontAwesomeIcon.setTextColor(context.getResources().getColor(R.color.flamingo));
                break;
        }

        if (fileUpload != null) {
            v.txtType.setText(fileUpload.getMimetype());
            v.txtDimensions.setText(FileUtil.getStringSizeLengthFile(fileUpload.getSize()));
        } else {
            v.txtType.setText(context.getString(R.string.text_mime_type));
            v.txtDimensions.setText(FileUtil.getStringSizeLengthFile(FileUtil.sizeOf(post)));
        }

        return result;
    }

    private class ViewHolder {
        RelativeLayout imageContainer;
        ImageView ivImg;
        IconTextView videoPlayIcon;
        RelativeLayout fontAwesomeContainer;
        IconTextView fontAwesomeIcon;
        ImageView ivStatusIcon;
        TextView txtStatus;
        TextView txtTitle;
        TextView txtCation;
        TextView txtType;
        TextView txtDimensions;
    }
}
