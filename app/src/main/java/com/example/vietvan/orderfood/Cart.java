package com.example.vietvan.orderfood;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vietvan.orderfood.Common.Common;
import com.example.vietvan.orderfood.Database.Database;
import com.example.vietvan.orderfood.Model.Order;
import com.example.vietvan.orderfood.Model.Request;
import com.example.vietvan.orderfood.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity {

    private static final String TAG = "TAG";
    @BindView(R.id.listCart)
    RecyclerView listCart;
    public static TextView total;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.btnPlaceOrder)
    FButton btnPlaceOrder;

    FirebaseDatabase database;
    DatabaseReference requests;
    List<Order> cart;
    MaterialEditText address, comment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add this code before setContentView
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/restaurant_font.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        total = findViewById(R.id.total);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        // Init
        cart = new ArrayList<>();
        listCart.setLayoutManager(new LinearLayoutManager(this));
        listCart.setHasFixedSize(true);

        // init swipe
        swipeLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadListFood();
            }
        });

        // load for first times
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                loadListFood();
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {

        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.order_address_comment, null);

        address = v.findViewById(R.id.edtAddress);
        comment = v.findViewById(R.id.edtComment);

        cart.clear();
        cart.addAll(new Database(this).getCarts());
        new AlertDialog.Builder(Cart.this)
                .setTitle("One more step")
                .setMessage("Enter your address")
                .setView(v)
                .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // calculate total price
                        int ntotal = 0;
                        for (Order order : cart)
                            ntotal += Integer.parseInt(order.getPrice()) * Integer.parseInt(order.getQuantity());

                        // Create request
                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address.getText().toString(),
                                String.valueOf(ntotal),
                                "0",
                                comment.getText().toString(),
                                cart
                        );

                        // Submit to Firebase
                        // We will using System.CurrentMil to key
                        requests.child(String.valueOf(System.currentTimeMillis()))
                                .setValue(request);

                        // Clean Cart
                        new Database(Cart.this).cleanCart();
                        Toast.makeText(Cart.this, "Thank you, Order Place", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void loadListFood() {

        cart = new Database(this).getCarts();
        listCart.setAdapter(new CartAdapter(cart, this));

        // calculate total price
        int ntotal = 0;
        for (Order order : cart)
            ntotal += Integer.parseInt(order.getPrice()) * Integer.parseInt(order.getQuantity());

        Locale locale = new Locale("en", "US");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        total.setText(format.format(ntotal));
        swipeLayout.setRefreshing(false);

    }

    @OnClick(R.id.btnPlaceOrder)
    public void onViewClicked() {
        if (cart.size() > 0)
            showAlertDialog();
        else
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)) {
            deleteCart(item.getOrder());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteCart(int order) {
        //We will remove item at List<Order> by position
        cart.remove(order);
        //After that, we will delete all old data from SQlite
        new Database(this).cleanCart();
        //And final, we will update new data from list<Order> to SQLite
        for (Order item : cart)
            new Database(this).addToCart(item);

        loadListFood();
    }
}
