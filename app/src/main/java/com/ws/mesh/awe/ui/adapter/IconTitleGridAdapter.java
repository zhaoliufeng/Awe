package com.ws.mesh.awe.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ws.mesh.awe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IconTitleGridAdapter extends RecyclerView.Adapter {
    private int[] title;
    private int[] icon;
    public IconTitleGridAdapter(int[] title, int[] icon){
        this.title = title;
        this.icon = icon;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_icon_title_grid, parent, false);
        return new SceneGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        SceneGridViewHolder viewHolder = (SceneGridViewHolder) holder;
        viewHolder.tvName.setText(title[position]);
        viewHolder.imgIcon.setImageResource(icon[position]);
        viewHolder.llFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemSelectedListener != null){
                    onItemSelectedListener.OnItemSelected(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return title.length;
    }

    private OnItemSelectedListener onItemSelectedListener;

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void OnItemSelected(int position);
    }

    class SceneGridViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_scene_icon)
        ImageView imgIcon;
        @BindView(R.id.tv_scene_name)
        TextView tvName;
        @BindView(R.id.ll_frame)
        LinearLayout llFrame;

        SceneGridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
