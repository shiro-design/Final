package com.shi.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SpalshActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mRef;
    FirebaseUser mUser;
    FirebaseDatabase database;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth= FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://appshi-56faa-default-rtdb.firebaseio.com");

        mUser=mAuth.getCurrentUser();
        mRef = database.getReference("Users");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(mUser != null)
                {
                    mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Intent intent = new Intent(SpalshActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Intent intent = new Intent(SpalshActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else {
                    Intent intent = new Intent(SpalshActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable,5000);
    }
}