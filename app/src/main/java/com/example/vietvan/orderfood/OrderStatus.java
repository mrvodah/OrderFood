package com.example.vietvan.orderfood;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vietvan.orderfood.Common.Common;
import com.example.vietvan.orderfood.Model.Order;
import com.example.vietvan.orderfood.Model.Request;
import com.example.vietvan.orderfood.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.widget.FButton;

public class OrderStatus extends AppCompatActivity {

    @BindView(R.id.rv_order)
    RecyclerView rvOrder;
    @BindView(R.id.order_total)
    TextView orderTotal;
    @BindView(R.id.order_btnPlaceOrder)
    FButton orderBtnPlaceOrder;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;
    int sum;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        ButterKnife.bind(this);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        layoutManager = new LinearLayoutManager(this);
        rvOrder.setLayoutManager(layoutManager);
        rvOrder.setHasFixedSize(true);

        if(getIntent().getStringExtra("userPhone") == null)
            loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));
    }

    private void loadOrders(String phone) {
        sum = 0;
        Locale locale = new Locale("en", "US");
        final NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.getTvOrderId().setText("#" + adapter.getRef(position).getKey());
                viewHolder.getTvOrderStatus().setText(convertCodeToStatus(model.getStatus()));
                viewHolder.getTvOrderPhone().setText(model.getPhone());
                viewHolder.getTvOrderAddress().setText(model.getAddress());

                sum += Integer.valueOf(model.getTotal());
                orderTotal.setText(format.format(sum));
            }
        };

        adapter.notifyDataSetChanged();
        rvOrder.setAdapter(adapter);
    }


    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }
}
