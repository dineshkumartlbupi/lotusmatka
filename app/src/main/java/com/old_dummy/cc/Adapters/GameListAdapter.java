package com.old_dummy.cc.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.MainGameChart.MainGameChartActivity;
import com.old_dummy.cc.Models.GameListModel;
import com.old_dummy.cc.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Objects;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder>{

    public interface OnItemClickListener{
        void onItemClick(GameListModel.Data data, View itemView);
    }
    Context context;
    ArrayList<GameListModel.Data> datalArrayList;

    OnItemClickListener listener;

    public GameListAdapter(Context context, ArrayList<GameListModel.Data> datalArrayList, OnItemClickListener listener) {
        this.context = context;
        this.datalArrayList = datalArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameListAdapter.ViewHolder holder, int position) {
        holder.bind(datalArrayList.get(position), listener, context, position);
    }

    @Override
    public int getItemCount() {
        return datalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView eventType,eventResult,openTime,closeTime;
        ShapeableImageView chartImage, playIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventType = itemView.findViewById(R.id.eventType);
            eventResult = itemView.findViewById(R.id.eventResult);
            openTime = itemView.findViewById(R.id.openTime);
            closeTime = itemView.findViewById(R.id.closeTime);
            chartImage = itemView.findViewById(R.id.chartIcon);
            playIcon = itemView.findViewById(R.id.playIcon);

        }

        public void bind(GameListModel.Data data, OnItemClickListener listener,
                         Context context, int position) {
            eventType.setText(data.getName());
            eventResult.setText(data.getResult());
            openTime.setText("Open: "+data.getOpen_time());
            closeTime.setText("Close: "+data.getClose_time());
            if(data.isPlay()){
                playIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play));
            }else {
                playIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.close));
            }
            chartImage.setOnClickListener(v -> {
                Intent intent = new Intent(context, MainGameChartActivity.class);
                intent.putExtra("gameId", data.getId());
                intent.putExtra("gameName", data.getName());
                context.startActivity(intent);
            });
            itemView.setOnClickListener(v ->{
                if(!Objects.equals(SharPrefHelper.getActiveUser(context), "2")){
                    listener.onItemClick(data, v);
                }
            });



        }

    }

}
