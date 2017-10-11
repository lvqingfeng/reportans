package com.juyikeji.myappjubao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.app.MyApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/5 0005.
 */
public class CheckPlandetailsAdapter extends BaseAdapter{

    private Context context;
    private List<String> list;
    ImageLoader loader=ImageLoader.getInstance();

    public CheckPlandetailsAdapter( Context context, List<String> list){
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.activity_checkplan_details_item,null);
            holder.iv=(ImageView)convertView.findViewById(R.id.iv);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        loader.displayImage(list.get(position), holder.iv, MyApplication.getDefaultOptions());
        return convertView;
    }

    class ViewHolder{

        ImageView iv;
    }
}
