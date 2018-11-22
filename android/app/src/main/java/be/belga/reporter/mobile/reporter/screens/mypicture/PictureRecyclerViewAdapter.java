package be.belga.reporter.mobile.reporter.screens.mypicture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import be.belga.reporter.mobile.reporter.model.Post;
import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import belga.be.belgareporter.R;

public class PictureRecyclerViewAdapter extends RecyclerView.Adapter<PictureRecyclerViewAdapter.ViewHolder> {

    private List<Post> posts;
    private Activity activity;

    public PictureRecyclerViewAdapter(Activity activity, List<Post> posts) {
        this.activity = activity;
        this.posts = posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (posts == null || posts.size() == 0) return 1;
        return posts.size() + 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_picture, parent, false);

        ViewHolder dataObjectHolder = new ViewHolder(view);

        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.itemView.setTag(position);

        holder.imgPicture.requestLayout();
        holder.imgPicture.getLayoutParams().height = ((MainActivity) activity).getSizeWindowDisplay().x / 2;

        holder.layoutTxtPicture.setVisibility(View.GONE);

        if (position == getItemCount() - 1) {
            holder.imgPicture.setImageDrawable(((MainActivity) activity).getResources().getDrawable(R.mipmap.add_icon));
            holder.imgPicture.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            File imgFile = new File(posts.get(position).getFileUpload().getGeneratedUrl());

            BitmapFactory.Options dimensions = new BitmapFactory.Options();
            dimensions.inJustDecodeBounds = true;

            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), dimensions);
            Bitmap bitmapImage = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            holder.imgPicture.setImageBitmap(bitmapImage);
            holder.imgPicture.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPicture;
        TextView txtPicture;
        LinearLayout layoutTxtPicture;

        public ViewHolder(final View itemView) {
            super(itemView);
            imgPicture = itemView.findViewById(R.id.imageview_picture);
            txtPicture = itemView.findViewById(R.id.txtview_picture);
            layoutTxtPicture = itemView.findViewById(R.id.layout_txtview_picture);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() == getItemCount() - 1) {
                        if (PictureFragment.getInstance().requestPermission()) {
                            PictureFragment.getInstance().showDialog();
                        }
                    } else {
                        ((MainActivity) activity).openPictureDetailFragment(posts.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
