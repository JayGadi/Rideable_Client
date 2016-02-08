package com.rideable.resources;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rideable.R;


import java.util.Collections;
import java.util.List;

public class OptionsItemCustomAdapter extends RecyclerView.Adapter<OptionsItemCustomAdapter.MyViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<OptionsDrawerItem> data = Collections.emptyList();

    public OptionsItemCustomAdapter(Context mContext,List<OptionsDrawerItem> data) {
        this.mContext = mContext;
        this.data = data;
        inflater = LayoutInflater.from(mContext);

    }

    public void delete(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.options_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        OptionsDrawerItem current = data.get(position);
        holder.icon.setBackgroundResource(current.getIcon());
        holder.title.setText(current.getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.option_texts);
            icon = (ImageView) itemView.findViewById(R.id.option_icons);
        }
    }
}