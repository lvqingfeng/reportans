package com.juyikeji.myappjubao.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.activity.DiscloseActivity;

import java.util.List;

/**
 * Created by Administrator on 2016/4/2 0002.
 */
public class DiscloseGridviewAdapter extends BaseAdapter {
    private DiscloseActivity context;
    private List<Bitmap> list;
    private int po = -1;

    public DiscloseGridviewAdapter(DiscloseActivity context, List<Bitmap> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size() + 1;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.disclose_gridview_item, null);
            holder.iv_ = (ImageView) convertView.findViewById(R.id.iv_);
            holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (position == getCount() - 1) {
            if (position>=4){
                holder.iv_.setVisibility(View.GONE);
                holder.iv_delete.setVisibility(View.GONE);
            }else {
                holder.iv_.setVisibility(View.VISIBLE);
                holder.iv_delete.setVisibility(View.GONE);
                holder.iv_.setImageResource(R.mipmap.add);
            }
        } else {
            holder.iv_.setVisibility(View.VISIBLE);
            holder.iv_delete.setVisibility(View.VISIBLE);
            holder.iv_.setImageBitmap(list.get(position));

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.clear(position);
//                    list.remove(position);
//                    notifyDataSetChanged();
                }
            });
        }

        return convertView;
    }

    class Holder {
        ImageView iv_, iv_delete;
    }
}
