package com.example.vietvan.orderfood;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.vietvan.orderfood.Common.Common;
import com.example.vietvan.orderfood.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUp extends AppCompatActivity {

    @BindView(R.id.edtPhone)
    MaterialEditText edtPhone;
    @BindView(R.id.edtNane)
    MaterialEditText edtNane;
    @BindView(R.id.edtPassword)
    MaterialEditText edtPassword;
    @BindView(R.id.edtSecureCode)
    MaterialEditText edtSecureCode;

    FirebaseDatabase database;
    DatabaseReference table_user;

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

        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

    }

    @OnClick(R.id.su_btnSignUp)
    public void onViewClicked() {

        if (Common.isConnectedToInternet(getBaseContext())) {
            final ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
            progressDialog.setMessage("Please waiting ...");
            progressDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    progressDialog.dismiss();

                    if (dataSnapshot.child(edtPhone.getText().toString()).exists())
                        Toast.makeText(SignUp.this, "Phone Number already registed!", Toast.LENGTH_SHORT).show();
                    else {
                        User user = new User(
                                edtNane.getText().toString(),
                                edtPassword.getText().toString(),
                                edtSecureCode.getText().toString()
                        );

                        table_user.child(edtPhone.getText().toString()).setValue(user);
                        finish();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Please check your connection!", Toast.LENGTH_SHORT).show();
            return;
        }

    }
}
