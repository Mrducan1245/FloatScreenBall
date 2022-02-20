package com.mrducan.floatscreenball;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

public class IpAdapter extends BaseAdapter {
    private LinkedList<IpItem> ipItems;
    private Context mContext;

    private String IP;

    private ViewHolder viewHolder;

    private EditText  edtIp;


    public IpAdapter(LinkedList<IpItem> ipItems, Context mContext,EditText edtIp,String IP) {
        this.ipItems = ipItems;
        this.mContext = mContext;
        this.edtIp = edtIp;
        this.IP = IP;
    }

    private class ViewHolder{
         TextView textView;
         ImageView img_icon;

    }


    @Override
    public int getCount() {
        return ipItems.size();
    }

    @Override
    public Object getItem(int position) {
        return ipItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IpItem ipItem = ipItems.get(position);
        if (convertView != null){
            viewHolder = (ViewHolder) convertView.getTag();
        }else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_spin_ip,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.img_icon = convertView.findViewById(R.id.iv_icon);
            viewHolder.textView= convertView.findViewById(R.id.tv_ip);
            viewHolder.img_icon.setBackgroundResource(ipItem.gethIcone());
            viewHolder.textView.setText(ipItem.getIP());
            convertView.setTag(viewHolder);
        }

        if (ipItem != null){
            viewHolder.textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    IP = ipItems.get(position).getIP();
                    edtIp.setText(IP);
                }
            });

            viewHolder.img_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ipItems.isEmpty()){
                        ipItems.remove(ipItems.get(position));
                    }
                }
            });
        }
        return convertView;
    }

}
