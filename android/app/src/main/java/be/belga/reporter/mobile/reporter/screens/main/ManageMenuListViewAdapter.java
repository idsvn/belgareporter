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

public class ManageMenuListViewAdapter extends BaseAdapter {

    private int[] text = new int[]{R.string.manage_default_metadata, R.string.manage_upload_settings, R.string.manage_profile};

    private LayoutInflater inflater;

    private Context context;

    public ManageMenuListViewAdapter(Context context) {
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
        viewHolder.ivIcon.setVisibility(View.GONE);
        viewHolder.tvText.setText(text[position]);

        return result;
    }

    private class ViewHolder {
        IconTextView ivIcon;
        TextView tvText;
    }
}
