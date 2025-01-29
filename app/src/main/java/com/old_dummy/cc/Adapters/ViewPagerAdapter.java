package com.old_dummy.cc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.old_dummy.cc.Models.AppDetailsModel;
import com.old_dummy.cc.R;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.SliderViewHolder> {

    private final List<AppDetailsModel.Data.Banner> bannerList;
    private final Context context;

    public ViewPagerAdapter(Context context, List<AppDetailsModel.Data.Banner> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_pager_layout, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        // Load the image into the ImageView using Glide
        Glide.with(holder.itemView)
                .load(bannerList.get(position).getImage()) // URL or image path from your model
                .fitCenter()
                .into(holder.imageViewBackground);
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageViewBackground;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.viewPagerImage);
        }
    }
}
