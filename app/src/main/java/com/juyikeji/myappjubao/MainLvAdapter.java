package com.juyikeji.myappjubao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/1 0001.
 */
public class MainLvAdapter extends BaseAdapter{
    private Context context;
    public List<Map<String,Object>> list;
    private int listsize=0;

    private MainLvitemAdapter adapter;

    public MainLvAdapter(Context context,List<Map<String,Object>> list){
        this.context=context;
        this.list=list;
        listsize=list.size()-1;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.activity_main_lvitem,null);
            holder.gv=(GridView)convertView.findViewById(R.id.gv);
            holder.tv=(TextView)convertView.findViewById(R.id.tv);
            holder.tv_text=(TextView)convertView.findViewById(R.id.tv_text);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.tv.setText(list.get(position).get("title").toString());
        holder.tv_text.setText("\t\t"+list.get(position).get("content").toString());
        final List<String> listgv=(List<String>)list.get(position).get("imlist");
        adapter=new MainLvitemAdapter(context,listgv);
        holder.gv.setAdapter(adapter);
//        holder.gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
//                Intent intent=new Intent(context, ImageViewActivity.class);
//                intent.putExtra("position",position1);
//                intent.putExtra("imgsize",listgv.size());
//                for (int i=0;i<((List<String>)list.get(position).get("imlist")).size();i++){
//                    intent.putExtra("img"+i,listgv.get(i));
//                }
//                context.startActivity(intent);
//            }
//        });
        //设置gridview不能获取焦点
        holder.gv.setClickable(false);
        holder.gv.setPressed(false);
        holder.gv.setEnabled(false);
        return convertView;
    }
    class ViewHolder{
        GridView gv;
        TextView tv,tv_text;
    }
}
