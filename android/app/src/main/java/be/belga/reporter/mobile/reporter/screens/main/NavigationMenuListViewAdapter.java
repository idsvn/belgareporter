package be.belga.reporter.mobile.reporter.screens.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import be.belga.reporter.mobile.views.IconTextView;
import belga.be.belgareporter.R;

/**
 * Created by vinh.bui on 6/5/2018.
 */

public class NavigationMenuListViewAdapter extends BaseAdapter {

    private int[] icon = new int[]{
            R.string.overview_posts_icon, R.string.alert_icon,
            R.string.short_icon, R.string.picture_icon,
            R.string.video_icon, R.string.audio_circle_icon
    };

    private int[] text = new int[]{
            R.string.overview_posts, R.string.add_alert, R.string.add_short,
            R.string.add_picture, R.string.add_video, R.string.add_audio
    };

    private LayoutInflater inflater;

    private Context context;

    public NavigationMenuListViewAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        return text.length;
    }

    @Override
    public Object getItem(int position) {
        return text[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            result = inflater.inflate(R.layout.list_item_nav_menu, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.ivIcon = (IconTextView) result.findViewById(R.id.icon_menu_nav);
            viewHolder.tvText = (TextView) result.findViewById(R.id.text_menu_nav);

            result.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) result.getTag();

        viewHolder.ivIcon.setText(context.getString(icon[position]));
        viewHolder.tvText.setText(text[position]);

        if (position == 0) {
            viewHolder.ivIcon.setTextColor(context.getResources().getColor(R.color.viking));
            viewHolder.tvText.setTextColor(context.getResources().getColor(R.color.viking));
        }

        if (position == 1) {
            viewHolder.ivIcon.setTextColor(context.getResources().getColor(R.color.flamingo));
            viewHolder.tvText.setTextColor(context.getResources().getColor(R.color.flamingo));
        }

        return result;
    }

    private class ViewHolder {
        IconTextView ivIcon;
        TextView tvText;
    }
}
