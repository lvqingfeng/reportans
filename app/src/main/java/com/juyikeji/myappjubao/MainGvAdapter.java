package com.juyikeji.myappjubao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/1 0001.
 */
public class MainGvAdapter extends BaseAdapter{
    private Context context;
    private List<Map<String,Object>> list;

    private int[] color={0xffd63839,0xff33d197,0xff7167e2,0xffffab34};
    public MainGvAdapter(Context context){
        this.context=context;
        getGv();
    }


    /**
     * 为gridview的容器赋值
     * @return
     */
    private void getGv() {
        list = new ArrayList<Map<String, Object>>();
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("image", R.mipmap.index11_05);
        map.put("tv", "我要上传");
        list.add(map);
        Map<String,Object> map1=new HashMap<String, Object>();
        map1.put("image", R.mipmap.index11_07);
        map1.put("tv", "查看进度");
        list.add(map1);
        Map<String,Object> map2=new HashMap<String, Object>();
        map2.put("image", R.mipmap.index11_11);
        map2.put("tv", "公告须知");
        list.add(map2);
        Map<String,Object> map3=new HashMap<String, Object>();
        map3.put("image", R.mipmap.index11_13);
        map3.put("tv", "个人中心");
        list.add(map3);
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
            convertView= LayoutInflater.from(context).inflate(R.layout.activity_main_gvitem,null);
            holder.iv=(ImageView)convertView.findViewById(R.id.iv);
            holder.tv=(TextView)convertView.findViewById(R.id.tv);
            holder.linout=(LinearLayout)convertView.findViewById(R.id.linout);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        switch (position){
            case 0:
                holder.linout.setBackgroundColor(color[position]);
                break;
            case 1:
                holder.linout.setBackgroundColor(color[position]);
                break;
            case 2:
                holder.linout.setBackgroundColor(color[position]);
                break;
            case 3:
                holder.linout.setBackgroundColor(color[position]);
                break;

        }
        holder.iv.setBackgroundResource((Integer)list.get(position).get("image"));
        holder.tv.setText(list.get(position).get("tv").toString());
        return convertView;
    }
    class ViewHolder{
        LinearLayout linout;
        ImageView iv;
        TextView tv;
    }


}
