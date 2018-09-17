package com.example.vietvan.orderfood.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vietvan.orderfood.Interface.ItemClickListener;
import com.example.vietvan.orderfood.R;

/**
 * Created by VietVan on 06/06/2018.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static ImageView food_image, fav_image, cart_image;
    public static TextView food_name, food_price;

    ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);

        food_image = itemView.findViewById(R.id.food_image);
        food_name = itemView.findViewById(R.id.food_name);
        food_price = itemView.findViewById(R.id.food_price);
        fav_image = itemView.findViewById(R.id.fav);
        cart_image = itemView.findViewById(R.id.iv_cart);

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
