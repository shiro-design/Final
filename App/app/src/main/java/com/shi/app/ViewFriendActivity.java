package com.shi.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFriendActivity extends AppCompatActivity {


    DatabaseReference mUserRef,requestRef,friendRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String profileImageUrl,username,city,hobby,profession;
    CircleImageView profileImage;
    TextView usernames, addreess;
    Button btnsend,btncan;
    String CurrentState = "nothing_happen";
    String userID ;
    String myProfileImageUrl,myUserName,myCity,myProfession;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);



        userID=getIntent().getStringExtra("userKey");

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mUser = mAuth.getCurrentUser();
        profileImage = findViewById(R.id.profileimage);
        usernames = findViewById(R.id.username);
        addreess = findViewById(R.id.address);
        btnsend = findViewById(R.id.btnsend);
        btncan= findViewById(R.id.btndec);





        loadUsers();

        loadMyProfile();


        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Action(userID);
            }
        });
        checkUser(userID);
        btncan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfriend(userID);
            }
        });

    }


    private void unfriend(String userID) {
        if(CurrentState.equals("friend")){
            friendRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()){
                       friendRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       Toast.makeText(ViewFriendActivity.this, "You are unfriend", Toast.LENGTH_SHORT).show();
                                       CurrentState="nothing_happen";
                                       btnsend.setText("Send Friend Request");
                                       btncan.setVisibility(View.GONE);
                                   }
                           }
                       });
                   }
                }
            });
        }
        if(CurrentState.equals("he_sent_pending")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","decline");
            requestRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this, "You have Decline Friend", Toast.LENGTH_SHORT).show();
                        CurrentState="he_sent_decline";
                        btnsend.setVisibility(View.GONE);
                        btncan.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void checkUser(String userID) {
        friendRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    CurrentState="friend";
                    btnsend.setText("Send Message");
                    btncan.setText("Unfriend");
                    btncan.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    CurrentState="friend";
                    btnsend.setText("Send Message");
                    btncan.setText("Unfriend");
                    btncan.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if(snapshot.child("status").getValue().toString().equals("pending")){
                        CurrentState="pending";
                        btnsend.setText("Cancel Request");
                        btncan.setVisibility(View.GONE);
                    }
                    if(snapshot.child("status").getValue().toString().equals("decline")){
                        CurrentState="decline";
                        btnsend.setText("Cancel Request");
                        btncan.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("status").getValue().toString().equals("pending")) {

                        CurrentState="he_sent_pending";
                        btnsend.setText("Accept Friend Request");
                        btncan.setText("Decline Friend");
                        btncan.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (CurrentState.equals("nothing_happen")){
            CurrentState="nothing_happen";
            btnsend.setText("Send Friend Request");
            btncan.setVisibility(View.GONE);
        }
    }

    private void Action(String userID) {
        if(CurrentState.equals("nothing_happen")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","pending");
            requestRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this, "You have sent Friend Request", Toast.LENGTH_SHORT).show();
                        btncan.setVisibility(View.GONE);
                        CurrentState="pending";
                        btnsend.setText("Cancel Friend Request");
                    }else {
                        Toast.makeText(ViewFriendActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(CurrentState.equals("pending")|| CurrentState.equals("decline")){
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this, "You have canceled Friend Request", Toast.LENGTH_SHORT).show();
                        CurrentState="nothing_happen";
                        btnsend.setText("Send request");
                        btncan.setVisibility(View.GONE);
                    }else {
                        Toast.makeText(ViewFriendActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(CurrentState.equals("he_sent_pending")){
            requestRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        HashMap hashMap = new HashMap();
                        hashMap.put("status","friend");
                        hashMap.put("username",username);
                        hashMap.put("profileImageUrl",profileImageUrl);
                        hashMap.put("profession",profession);

                        final HashMap hashMap1 = new HashMap();
                        hashMap1.put("status","friend");
                        hashMap1.put("username",myUserName);
                        hashMap1.put("profileImageUrl",myProfileImageUrl);
                        hashMap1.put("profession",myProfession);




                        friendRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {


                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    friendRef.child(userID).child(mUser.getUid()).updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            Toast.makeText(ViewFriendActivity.this, "You add friend", Toast.LENGTH_SHORT).show();
                                            CurrentState="friend";
                                            btnsend.setText("Send Message");
                                            btncan.setText("Unfriend");
                                            btncan.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }else{
            if(CurrentState.equals("friend")){
                Intent intent = new Intent(ViewFriendActivity.this,ChatActivity.class);
                intent.putExtra("OtherUserID",userID);
                startActivity(intent);
            }
        }
    }


    private void loadUsers() {
        mUserRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    profileImageUrl=snapshot.child("profileImage").getValue().toString();
                    username=snapshot.child("username").getValue().toString();
                    city=snapshot.child("city").getValue().toString();
                    hobby=snapshot.child("hobby").getValue().toString();
                    profession=snapshot.child("profession").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImage);
                    usernames.setText(username);
                    addreess.setText(city);

                }else {
                    Toast.makeText(ViewFriendActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    myProfileImageUrl = snapshot.child("profileImage").getValue().toString();
                    myUserName = snapshot.child("username").getValue().toString();
                    myCity = snapshot.child("city").getValue().toString();
                    myProfession = snapshot.child("profession").getValue().toString();


                }else {
                    Toast.makeText(ViewFriendActivity.this, "Khong tim thay du lieu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}