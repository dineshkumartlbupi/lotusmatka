package com.old_dummy.cc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.old_dummy.cc.Models.WalletStatementModel;
import com.old_dummy.cc.R;

import java.util.List;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.ViewHolder> {

    Context context;
    List<WalletStatementModel.Data.Statement> modelWalletArrayList;

    public WalletAdapter(Context context, List<WalletStatementModel.Data.Statement> modelWalletArrayList) {
        this.context = context;
        this.modelWalletArrayList = modelWalletArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_view_wallet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WalletStatementModel.Data.Statement statement = modelWalletArrayList.get(position);
        holder.bind(statement);
    }

    @Override
    public int getItemCount() {
        return modelWalletArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView bonusName, amount, dateTime, tranStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tranStatus = itemView.findViewById(R.id.status);
            bonusName = itemView.findViewById(R.id.bonusName);
            amount = itemView.findViewById(R.id.amount);
            dateTime = itemView.findViewById(R.id.dateTime);
        }

        public void bind(WalletStatementModel.Data.Statement recyclerModelWallet) {
            bonusName.setText(recyclerModelWallet.getTransMsg());
            tranStatus.setText(recyclerModelWallet.getTransStatus());
            if (recyclerModelWallet.getTransStatus().equalsIgnoreCase("pending")){
                tranStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow));
            }else if(recyclerModelWallet.getTransStatus().equalsIgnoreCase("Successfull")) {
                tranStatus.setTextColor(ContextCompat.getColor(context, R.color.main_color));
            }else if(recyclerModelWallet.getTransStatus().equalsIgnoreCase("Successful")) {
                tranStatus.setTextColor(ContextCompat.getColor(context, R.color.main_color));
            }else if(recyclerModelWallet.getTransStatus().equalsIgnoreCase("Approved")) {
                tranStatus.setTextColor(ContextCompat.getColor(context, R.color.main_color));
            }else if(recyclerModelWallet.getTransStatus().equalsIgnoreCase("Failed")) {
                tranStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
            }else if(recyclerModelWallet.getTransStatus().equalsIgnoreCase("Rejected")) {
                tranStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
            }else {
                tranStatus.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
            if (recyclerModelWallet.getTransType().equalsIgnoreCase("debit")){
                amount.setTextColor(ContextCompat.getColor(context, R.color.red));
            }else if (recyclerModelWallet.getTransType().equalsIgnoreCase("credit")){
                amount.setTextColor(ContextCompat.getColor(context, R.color.main_color));
            }
            amount.setText(recyclerModelWallet.getPoints());
            dateTime.setText(recyclerModelWallet.getCreatedAt());
        }
    }
}
