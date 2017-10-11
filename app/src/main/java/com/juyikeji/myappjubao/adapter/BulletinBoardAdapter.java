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
public class BulletinBoardAdapter extends BaseAdapter{

    private Context context;
    private List<Map<String,String>> list;
    ImageLoader loader=ImageLoader.getInstance();

    public BulletinBoardAdapter( Context context, List<Map<String,String>> list){
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
            convertView= LayoutInflater.from(context).inflate(R.layout.activity_bulletinboard_item,null);
            holder.iv_heard=(ImageView)convertView.findViewById(R.id.iv_heard);
            holder.tv_title=(TextView)convertView.findViewById(R.id.tv_title);
            holder.tv_text=(TextView)convertView.findViewById(R.id.tv_text);
            holder.tv_time=(TextView)convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        loader.displayImage(list.get(position).get("picurl"), holder.iv_heard, MyApplication.getDefaultOptions());
        holder.tv_title.setText(list.get(position).get("title"));
        holder.tv_text.setText("\t\t"+list.get(position).get("content"));
        holder.tv_time.setText(list.get(position).get("crttime"));
        return convertView;
    }

    class ViewHolder{

        ImageView iv_heard;
        TextView tv_title,tv_text,tv_time;
    }
}
