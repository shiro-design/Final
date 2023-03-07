package com.shi.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shi.app.Utills.Comment;
import com.shi.app.Utills.Posts;
import com.shi.app.Utills.TimeAgo2;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef,postRef,LikeRef,CommentRef;
    FirebaseDatabase database;
    String profileImageURl,usernameView;
    CircleImageView profileCimage;
    TextView txtUser;
    ImageView addPost, sendPost;
    EditText postDesc;
    private static final int REQUEST_CODE = 10;
    Uri imgUri;
    ProgressDialog progressDialog;
    StorageReference postImage;
    FirebaseStorage firebaseStorage;
    FirebaseRecyclerAdapter<Posts,MyViewHolder>adapter;
    FirebaseRecyclerOptions<Posts>options;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<Comment>CommentOption;
    FirebaseRecyclerAdapter<Comment,CommentViewHolder>CommentAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance("https://appshi-56faa-default-rtdb.firebaseio.com");
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ShiConnect");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = database.getReference("Users");
        postRef=database.getReference("Post");
        addPost= findViewById(R.id.imageaddPost);
        sendPost=findViewById(R.id.sendPost);
        LikeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        CommentRef=FirebaseDatabase.getInstance().getReference().child("Comments");
       // firebaseStorage= FirebaseStorage.getInstance("https://appshi-56faa-default-rtdb.firebaseio.com");
        postDesc= findViewById(R.id.inputAddPost);
        progressDialog = new ProgressDialog(this);
        postImage = FirebaseStorage.getInstance().getReference("PostImage");
        recyclerView= findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseMessaging.getInstance().subscribeToTopic(mUser.getUid());



        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        View view = navigationView.inflateHeaderView(R.layout.drawer_header);
        profileCimage=view.findViewById(R.id.profileImage_header);
        txtUser = view.findViewById(R.id.userNameHeader);


        navigationView.setNavigationItemSelectedListener(this);
        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPost();
            }
        });
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        LoadPost();


    }

    private void LoadPost() {

        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(postRef,Posts.class).build();
        adapter= new FirebaseRecyclerAdapter<Posts, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Posts model) {

                final String postKey = getRef(position).getKey();
                holder.postDesc.setText(model.getPostDesc());
                String timeAgo = calculateTimeAgo(model.getDatePost());
                holder.timeAgo.setText(timeAgo);
                holder.username.setText(model.getUsername());
                Picasso.get().load(model.getPostImageUrl()).into(holder.postImage);
                Picasso.get().load(model.getUserProfileImageUrl()).into(holder.profileImage);
                holder.countLikes(postKey,mUser.getUid(),LikeRef);
                holder.countComment(postKey,mUser.getUid(),CommentRef);
                holder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LikeRef.child(postKey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    LikeRef.child(postKey).child(mUser.getUid()).removeValue();
                                    holder.likeImage.setColorFilter(Color.GRAY);
                                    notifyDataSetChanged();
                                }else{
                                    LikeRef.child(postKey).child(mUser.getUid()).setValue("like");
                                    holder.likeImage.setColorFilter(Color.GREEN);
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                holder.commnetSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment = holder.inputComment.getText().toString();
                        if(comment.isEmpty()){
                            Toast.makeText(MainActivity.this, "Please write something", Toast.LENGTH_SHORT).show();
                        }else {
                            AddComment(holder,postKey,CommentRef,mUser.getUid(),comment);
                        }
                    }
                });

                LoadComments(postKey);
                holder.postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,ImageViewActivity.class);
                        intent.putExtra("url",model.getPostImageUrl());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_post,parent,false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    private void LoadComments(String postKey) {
             MyViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            CommentOption= new FirebaseRecyclerOptions.Builder<Comment>().setQuery(CommentRef.child(postKey),Comment.class).build();
            CommentAdapter= new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(CommentOption) {
                @Override
                protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
                    Picasso.get().load(model.getProfileImageUrl()).into(holder.profileImage);
                    holder.username.setText(model.getUsername());
                    holder.comment.setText(model.getComment());
                }

                @NonNull
                @Override
                public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_comment,parent,false);
                    return new CommentViewHolder(view);

                }
            };

            CommentAdapter.startListening();
            MyViewHolder.recyclerView.setAdapter(CommentAdapter);

    }

    private void AddComment(MyViewHolder holder, String postKey, DatabaseReference commentRef, String uid, String comment) {
        HashMap hashMap= new HashMap();
        hashMap.put("username",usernameView);
        hashMap.put("profileImageUrl",profileImageURl);
        hashMap.put("comment",comment);

        commentRef.child(postKey).child(uid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Comments Added", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    holder.inputComment.setText(null);
                }else{

                    Toast.makeText(MainActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }



    private String calculateTimeAgo(String datePost) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        try {
            long time = sdf.parse(datePost).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!=null){
            imgUri = data.getData();
            addPost.setImageURI(imgUri);
        }
    }

    private void AddPost() {
        String postinput = postDesc.getText().toString();
        if(postinput.isEmpty() || postinput.length()<3){
            postDesc.setError("Please write something in post");
        }else if(imgUri == null){
            Toast.makeText(this, "Please select and image", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.setTitle("Add Post");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            postImage.child(mUser.getUid()).putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        postImage.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Date date = new Date();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                                String strDate = formatter.format(date);

                                HashMap hashMap = new HashMap();
                                hashMap.put("datePost",strDate);
                                hashMap.put("postImageUrl",uri.toString());
                                hashMap.put("postDesc",postinput);
                                hashMap.put("username",usernameView);
                                hashMap.put("userProfileImageUrl",profileImageURl);
                                postRef.child(mUser.getUid()+strDate).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Post Added", Toast.LENGTH_SHORT).show();
                                            addPost.setImageResource(R.drawable.image_gallery_48px);
                                            postDesc.setText("");

                                        }else{
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mUser==null){
            SendUserToLogin();
        }
        else {
            mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        profileImageURl=snapshot.child("profileImage").getValue().toString();
                        usernameView = snapshot.child("username").getValue().toString();
                        Picasso.get().load(profileImageURl).into(profileCimage);
                        txtUser.setText(usernameView);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Sorry ! Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void SendUserToLogin() {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.profile:
               startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                break;
            case R.id.friend:
               startActivity(new Intent(MainActivity.this,FriendActivity.class));
                break;
            case R.id.addFr:
                startActivity(new Intent(MainActivity.this,FriendAdActivity.class));
                break;
            case R.id.chat:
                startActivity(new Intent(MainActivity.this,ChatUserActivity.class));
                break;
            case R.id.share:
                Intent intent0 =  new Intent(Intent.ACTION_SEND);
                intent0.setType("text/plain");
                String b = "Download this App";
                String s = "http://play.google.com";
                intent0.putExtra(Intent.EXTRA_TEXT,b);
                intent0.putExtra(Intent.EXTRA_TEXT,s);
                startActivity(Intent.createChooser(intent0,"Share using with"));
                break;
            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }
}