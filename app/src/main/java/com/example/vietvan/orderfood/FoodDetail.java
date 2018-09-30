package com.example.vietvan.orderfood;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.vietvan.orderfood.Common.Common;
import com.example.vietvan.orderfood.Database.Database;
import com.example.vietvan.orderfood.Model.Food;
import com.example.vietvan.orderfood.Model.Order;
import com.example.vietvan.orderfood.Model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    private static final String TAG = "TAG";
    @BindView(R.id.img_food)
    ImageView imgFood;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing)
    CollapsingToolbarLayout collapsing;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.btnCart)
    CounterFab btnCart;
    @BindView(R.id.food_name)
    TextView foodName;
    @BindView(R.id.food_price)
    TextView foodPrice;
    @BindView(R.id.layout_price)
    LinearLayout layoutPrice;
    @BindView(R.id.number_button)
    ElegantNumberButton numberButton;
    @BindView(R.id.food_description)
    TextView foodDescription;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTbl;

    String foodId;
    Food currentFood;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

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

        setContentView(R.layout.activity_food_detail);
        ButterKnife.bind(this);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        ratingTbl = database.getReference("Rating");

        // Init View
        collapsing.setExpandedTitleTextAppearance(R.style.ExpandedAppar);
        collapsing.setCollapsedTitleTextAppearance(R.style.CollapsedAppar);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()

                ));
                Toast.makeText(FoodDetail.this, "Added to Cart!", Toast.LENGTH_SHORT).show();
            }
        });
        btnCart.setCount(new Database(this).getCarts().size());

        if (getIntent() != null) {
            foodId = getIntent().getStringExtra("FoodId");
            getDetailFood(foodId);
            getRatingFood(foodId);
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void getRatingFood(final String foodId) {
        ratingTbl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(foodId).exists()){
                    String rate = (String) dataSnapshot.child(foodId).child("rateValue").getValue();
                    ratingBar.setRating(Float.parseFloat(rate));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDetailFood(String foodId) {

        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentFood = dataSnapshot.getValue(Food.class);

                Picasso.get().load(currentFood.getImage()).into(imgFood);

                collapsing.setTitle(currentFood.getName());

                foodName.setText(currentFood.getName());
                foodPrice.setText(currentFood.getPrice());
                foodDescription.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @OnClick(R.id.btnRate)
    public void onViewClicked() {
        showRatingDialog();
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite Ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rating this food")
                .setDescription("Please select soem starts and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    @Override
    public void onPositiveButtonClicked(final int value, String comments) {
        //Get Rating and upload to firebase
        final Rating rating = new Rating(Common.currentUser.getPhone(),
                foodId,
                String.valueOf(value),
                comments);

        ratingTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(foodId).exists()){
                    String rate = (String) dataSnapshot.child(foodId).child("rateValue").getValue();
                    double ave = (value + Double.parseDouble(rate)) / 2;
                    ratingTbl.child(foodId).child("rateValue").setValue((String.valueOf(ave)));
                }
                else
                    ratingTbl.child(foodId).setValue(rating);

                Toast.makeText(FoodDetail.this, "Thank yu for submit rating!", Toast.LENGTH_SHORT).show();
                ratingTbl.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
