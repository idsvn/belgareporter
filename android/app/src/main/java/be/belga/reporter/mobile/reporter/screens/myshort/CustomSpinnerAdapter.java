package be.belga.reporter.mobile.reporter.screens.myshort;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import belga.be.belgareporter.R;

public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context context;
    private String[] asr;

    public CustomSpinnerAdapter(Context context, String[] asr) {
        this.asr = asr;
        this.context = context;
    }

    public int getCount() {
        return asr.length;
    }

    public Object getItem(int i) {
        return asr[i];
    }

    public long getItemId(int i) {
        return (long) i;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        txt.setPadding(30, 8, 30, 8);
        txt.setBackgroundColor(Color.TRANSPARENT);
        txt.setTextSize(13);
        txt.setText(asr[position]);
        txt.setTextColor(context.getResources().getColor(R.color.dim_gray));
        return txt;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        TextView txt = new TextView(context);
        txt.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        txt.setPadding(30, 0, 15, 0);
        txt.setTextSize(13);
        txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.intercom_expand_arrow, 0);
        txt.setText(asr[i]);
        txt.setTextColor(context.getResources().getColor(R.color.dim_gray));
        return txt;
    }

}
