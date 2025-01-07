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
import com.old_dummy.cc.Models.StarlineGameListModel;
import com.old_dummy.cc.R;

import java.util.List;

public class StarlineGameListAdapter extends RecyclerView.Adapter
        <StarlineGameListAdapter.ViewHolder>{

    public interface OnItemClickListener{
        void onItemClick(StarlineGameListModel.Data.StarlineGame starlineGame, View itemView);
    }
    Context context;
    List<StarlineGameListModel.Data.StarlineGame> starlineGameList;

    OnItemClickListener listener;

    public StarlineGameListAdapter(Context context, List<StarlineGameListModel.Data.StarlineGame>
            starlineGameList, OnItemClickListener listener) {
        this.context = context;
        this.starlineGameList = starlineGameList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_starline_game, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(starlineGameList.get(position), listener, context, position);
    }

    @Override
    public int getItemCount() {
        return starlineGameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView gameName,gameResult;
        MaterialTextView eventStatusText;
        MaterialTextView eventStatus;
        ShapeableImageView playIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gameName = itemView.findViewById(R.id.gameName);
            gameResult = itemView.findViewById(R.id.gameResult);
            eventStatusText = itemView.findViewById(R.id.eventStatusText);
            eventStatus = itemView.findViewById(R.id.eventStatus);
            playIcon = itemView.findViewById(R.id.playIcon);

        }

        public void bind(StarlineGameListModel.Data.StarlineGame starlineGame,
                         OnItemClickListener listener, Context context, int position) {
             try{
                 gameName.setText(starlineGame.getName());
                 gameResult.setText(starlineGame.getResult());

                 if(starlineGame.isPlay()){
                     eventStatusText.setText("Market Open");
                     eventStatus.setText("Running");
                     playIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play));
                     eventStatusText.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main_color)));
                     eventStatus.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main_color)));
                 }
                 else {
                     playIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.close));
                     eventStatusText.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.main_color)));
                     eventStatus.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.warningRed)));
                     eventStatusText.setText("Market Closed");
                     eventStatus.setText("Closed");
                 }
                 itemView.setOnClickListener(v ->{
                     listener.onItemClick(starlineGame, v);
                 });

             }catch (Exception e){

             }


        }
    }
}
