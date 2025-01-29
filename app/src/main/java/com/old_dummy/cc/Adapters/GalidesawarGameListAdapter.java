package com.old_dummy.cc.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.old_dummy.cc.Models.GalidesawarGameListModel;
import com.old_dummy.cc.R;

import java.util.List;

public class GalidesawarGameListAdapter extends RecyclerView.Adapter<GalidesawarGameListAdapter.ViewHolder>{

    public interface OnItemClickListener{
        void onItemClick(GalidesawarGameListModel.Data.GalidesawarGame starlineGame, View itemView);
    }
    Context context;
    List<GalidesawarGameListModel.Data.GalidesawarGame> galidesawarGameList;

    OnItemClickListener listener;

    public GalidesawarGameListAdapter(Context context, List<GalidesawarGameListModel.Data.GalidesawarGame> galidesawarGameList, OnItemClickListener listener) {
        this.context = context;
        this.galidesawarGameList = galidesawarGameList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_gali_game, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(galidesawarGameList.get(position), listener, context, position);
    }

    @Override
    public int getItemCount() {
        return galidesawarGameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView gameName,gameResult;
        ShapeableImageView playIcon;
        MaterialTextView gameTime;
        MaterialTextView eventStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gameName = itemView.findViewById(R.id.gameName);
            gameResult = itemView.findViewById(R.id.gameResult);
            gameTime = itemView.findViewById(R.id.gameTime);
            playIcon = itemView.findViewById(R.id.playIcon);
            eventStatus = itemView.findViewById(R.id.eventStatus);

        }

        public void bind(GalidesawarGameListModel.Data.GalidesawarGame galidesawarGame, OnItemClickListener listener, Context context, int position) {

            gameName.setText(galidesawarGame.getName());
            gameResult.setText(galidesawarGame.getResult());
            gameTime.setText(galidesawarGame.getTime());

            if(galidesawarGame.isPlay()){
                playIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play));
                playIcon.setColorFilter(ContextCompat.getColor(context, R.color.main_color));
                eventStatus.setText("Running");
                eventStatus.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main_color)));
            } else {
                playIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play));
                playIcon.setColorFilter(ContextCompat.getColor(context, R.color.warningRed));
                eventStatus.setText("Closed");
                eventStatus.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.warningRed)));
            }

            itemView.setOnClickListener(v ->{
                listener.onItemClick(galidesawarGame, v);
            });
        }
    }
}
