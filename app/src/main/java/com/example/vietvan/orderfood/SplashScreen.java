package com.example.vietvan.orderfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vietvan.orderfood.Common.Common;
import com.example.vietvan.orderfood.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class SplashScreen extends AppCompatActivity {

    @BindView(R.id.tvSlogan)
    TextView tvSlogan;
    @BindView(R.id.btnSignUp)
    FButton btnSignUp;
    @BindView(R.id.btnSignIn)
    FButton btnSignIn;

    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        //Init paper
        Paper.init(this);

        //Check remember
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PW_KEY);
        if(user != null && pwd != null){
            if(!user.isEmpty() && !pwd.isEmpty())
                login(user, pwd);
        }

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        tvSlogan.setTypeface(typeface);

    }

    private void login(final String us, final String pwd) {
        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog progressDialog = new ProgressDialog(SplashScreen.this);
            progressDialog.setMessage("Please waiting ...");
            progressDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    progressDialog.dismiss();

                    //check if user not exist in database
                    if (dataSnapshot.child(us).exists()) {
                        // get user information
                        User user = dataSnapshot.child(us).getValue(User.class);
                        user.setPhone(us);

                        if (user.getPassword().equals(pwd)) {
                            Common.currentUser = user;
                            startActivity(new Intent(SplashScreen.this, Home.class));
                            finish();

                        } else {
                            Toast.makeText(SplashScreen.this, "Sign in failed!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SplashScreen.this, "User not exists", Toast.LENGTH_SHORT).show();
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

    @OnClick({R.id.btnSignUp, R.id.btnSignIn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                startActivity(new Intent(SplashScreen.this, SignUp.class));
                break;
            case R.id.btnSignIn:
                startActivity(new Intent(SplashScreen.this, SignIn.class));
                break;
        }
    }
}
