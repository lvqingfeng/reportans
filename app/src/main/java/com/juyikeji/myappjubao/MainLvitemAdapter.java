package com.juyikeji.myappjubao;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juyikeji.myappjubao.app.MyApplication;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/1 0001.
 */
public class MainLvitemAdapter extends BaseAdapter{
    private Context context;
    private List<String> list;
    ImageLoader loader=ImageLoader.getInstance();

    public MainLvitemAdapter(Context context, List<String> list){
        this.context=context;
        this.list=list;
    }
    /**
     * 让adapter中的所有item不可以点击
     */
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    /**
     * 下标为position 的item不可选中，不可点击
     */
    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.activity_main_lvitem_item,null);
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
