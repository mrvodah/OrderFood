package com.example.vietvan.orderfood;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

        if(getIntent() == null)
            loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));

    }

    private void loadOrders(String phone) {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.tvOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.tvOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                viewHolder.tvOrderPhone.setText(model.getPhone());
                viewHolder.tvOrderAddress.setText(model.getAddress());
            }
        };

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

    @OnClick(R.id.order_btnPlaceOrder)
    public void onViewClicked() {
    }
}
