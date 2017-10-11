package com.juyikeji.myappjubao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juyikeji.myappjubao.R;

import java.util.List;

/**
 * Created by Administrator on 2016/4/22 0022.
 */
public class DiscloseGvluxiangAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private List<Integer> list;

    private Callback mCallback;

    /**
     * 25      * 自定义接口，用于回调按钮点击事件到Activity
     * 26      * @author Ivan Xu
     * 27      * 2014-11-26
     * 28
     */
    public interface Callback {
        public void click(View v);
    }

    public DiscloseGvluxiangAdapter(Context context, List<Integer> list, Callback callback) {
        this.context = context;
        this.list = list;
        mCallback = callback;
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_disclose_gvluxiangitem, null);
            holder.iv_ = (ImageView) convertView.findViewById(R.id.iv_);
            holder.iv_play = (ImageView) convertView.findViewById(R.id.iv_play);
            holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.iv_delete.setOnClickListener(this);//给控件添加点击事件并设置位置
        holder.iv_delete.setTag(position);
        return convertView;
    }

    //响应按钮点击事件,调用子定义接口，并传入View

    @Override
    public void onClick(View v) {
        mCallback.click(v);
    }

    class ViewHolder {
        ImageView iv_,iv_play,iv_delete;
    }
}
