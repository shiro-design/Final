package com.shi.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shi.app.Utills.Chat;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText inputmess;
    ImageView btnsend;
    CircleImageView userProfileImageAppbar;
    TextView usernameAppbar,status;
    String OtherUserID;
    DatabaseReference mUserRef,smsRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String OtherUsername, OtherUserProfileImageLink,OtherUserStatus;
    FirebaseRecyclerOptions<Chat>options;
    FirebaseRecyclerAdapter<Chat,ChatMyViewholder>adapter;
    String myProfileImageLink;
    String URL ="https://fcm.googleapis.com/fcm/send";
    RequestQueue requestqueue;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar= findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        requestqueue= Volley.newRequestQueue(this);

        OtherUserID=getIntent().getStringExtra("OtherUserID");
        recyclerView =findViewById(R.id.recycleview);
        inputmess= findViewById(R.id.inputmess);
        usernameAppbar=findViewById(R.id.usernameAppbar);
        status=findViewById(R.id.status);
        userProfileImageAppbar=findViewById(R.id.userProfileImageAppbar);
        btnsend = findViewById(R.id.btnsendmess);
        smsRef=FirebaseDatabase.getInstance().getReference().child("Message");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();




        loadOtherUser();
        LoadMyProfile();
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendSMS();
            }
        });
        loadSMS();
    }

    private void LoadMyProfile() {

        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    myProfileImageLink=snapshot.child("profileImage").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadSMS() {
            options= new FirebaseRecyclerOptions.Builder<Chat>().setQuery(smsRef.child(mUser.getUid()).child(OtherUserID),Chat.class).build();
            adapter = new FirebaseRecyclerAdapter<Chat, ChatMyViewholder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ChatMyViewholder holder, int position, @NonNull Chat model) {
                    if(model.getUserID().equals(mUser.getUid())){
                        holder.firstUserText.setVisibility(View.GONE);
                        holder.firstUserProfile.setVisibility(View.GONE);
                        holder.secondUserText.setVisibility(View.VISIBLE);
                        holder.secondUserProfile.setVisibility(View.VISIBLE);

                        Picasso.get().load(myProfileImageLink).into(holder.secondUserProfile);


                        holder.secondUserText.setText(model.getSms());
                    }else {
                        holder.firstUserText.setVisibility(View.VISIBLE);
                        holder.firstUserProfile.setVisibility(View.VISIBLE);
                        holder.secondUserText.setVisibility(View.GONE);
                        holder.secondUserProfile.setVisibility(View.GONE);

                        holder.firstUserText.setText(model.getSms());
                        Picasso.get().load(OtherUserProfileImageLink).into(holder.firstUserProfile);
                    }
                }

                @NonNull
                @Override
                public ChatMyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleview_sms,parent,false);

                    return new ChatMyViewholder(view);
                }
            };
            adapter.startListening();
            recyclerView.setAdapter(adapter);
    }


    private void SendSMS() {
        String sms= inputmess.getText().toString();
        if (sms.isEmpty()){
            Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show();
        }else {
            HashMap hashMap = new HashMap();
            hashMap.put("sms",sms);
            hashMap.put("status","unseen");
            hashMap.put("userID",mUser.getUid());
            smsRef.child(OtherUserID).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        smsRef.child(mUser.getUid()).child(OtherUserID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()){
                                    sendNoti(sms);
                                    inputmess.setText(null);
                                    Toast.makeText(ChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void sendNoti(String sms) {
        JSONObject json = new JSONObject();
        try {
            json.put("to","/topics/"+OtherUserID);
            JSONObject json1 = new JSONObject();
            json1.put("title","You have a new message ! Check it out");
            json1.put("body",sms);



            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userID", mUser.getUid());
            jsonObject.put("type","sms");



            json.put("notification",json1);
            json.put("data",jsonObject);



            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String ,String> map = new HashMap<>();
                    map.put("content-type","application/json");
                    map.put("authorization","key=####################################");
                    return map;
                }
            };
            requestqueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void loadOtherUser() {

        mUserRef.child(OtherUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    OtherUsername = snapshot.child("username").getValue().toString();
                    OtherUserProfileImageLink = snapshot.child("profileImage").getValue().toString();
                    OtherUserStatus = snapshot.child("status").getValue().toString();
                    Picasso.get().load(OtherUserProfileImageLink).into(userProfileImageAppbar);
                    usernameAppbar.setText(OtherUsername);
                    status.setText(OtherUserStatus);
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
