package com.juyikeji.myappjubao.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.juyikeji.myappjubao.R;

/**
 * 城市定位adapter
 * @author CityDingWeiAdapter
 *
 */
public class CityDingWeiAdapter extends BaseAdapter{
    private Context context;
    private List<String> list;
    public CityDingWeiAdapter(Context context, List<String> list
    ) {
        this.list = list;
        this.context = context;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHoder viewhoder = null;
        if (arg1 == null) {
            arg1 = LayoutInflater.from(context).inflate(
                    R.layout.activity_citydingwei_item, null);
            viewhoder = new ViewHoder();
            viewhoder.tv = (TextView) arg1.findViewById(R.id.tv);
            arg1.setTag(viewhoder);

        } else {
            viewhoder = (ViewHoder) arg1.getTag();
        }

        viewhoder.tv.setText(list.get(arg0).toString());
        viewhoder.tv.setBackgroundColor(Color.WHITE);
        return arg1;
    }
    class ViewHoder {
        TextView tv;
    }
}
