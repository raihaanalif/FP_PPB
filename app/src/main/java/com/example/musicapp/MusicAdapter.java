package com.example.musicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicapp.model.ModelMusic;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private final List<ModelMusic> items;
    private final MusicAdapter.onSelectData onSelectData;
    private final Context mContext;

    public interface onSelectData {
        void onSelected(ModelMusic modelMusic);
    }

    public MusicAdapter(Context context, List<ModelMusic> items, MusicAdapter.onSelectData xSelectData) {
        this.mContext = context;
        this.items = items;
        this.onSelectData = xSelectData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ModelMusic data = items.get(position);

        //Get Image
        Glide.with(mContext)
                .load(data.cover)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.ic_no_image_foreground)
                .into(holder.imgCover);

        holder.tvBand.setText(data.strBand);
        holder.tvTitleMusic.setText(data.strTitle);
        holder.cvListMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectData.onSelected(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //Class Holder
    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvBand;
        public TextView tvTitleMusic;
        public CardView cvListMusic;
        public ImageView imgCover;

        public ViewHolder(View itemView) {
            super(itemView);
            cvListMusic = itemView.findViewById(R.id.cvListMusic);
            imgCover = itemView.findViewById(R.id.imgCover);
            tvBand = itemView.findViewById(R.id.tvBand);
            tvTitleMusic = itemView.findViewById(R.id.tvTitleMusic);
        }
    }

}
