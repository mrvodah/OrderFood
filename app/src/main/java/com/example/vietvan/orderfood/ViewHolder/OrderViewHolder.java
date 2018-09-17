package com.example.vietvan.orderfood.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.vietvan.orderfood.Interface.ItemClickListener;
import com.example.vietvan.orderfood.R;

/**
 * Created by VietVan on 13/06/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tvOrderId, tvOrderStatus, tvOrderPhone, tvOrderAddress;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        tvOrderId = itemView.findViewById(R.id.order_id);
        tvOrderStatus = itemView.findViewById(R.id.order_status);
        tvOrderPhone = itemView.findViewById(R.id.order_phone);
        tvOrderAddress = itemView.findViewById(R.id.order_address);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }


}
