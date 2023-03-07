package com.shi.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shi.app.Utills.Users;
import com.squareup.picasso.Picasso;

import static com.shi.app.R.menu.search_menu;

public class FriendAdActivity extends AppCompatActivity {

    FirebaseRecyclerOptions<Users> options;
    FirebaseRecyclerAdapter<Users, FindFriendViewHolder> adapter;
    Toolbar toolbar;

    DatabaseReference mRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_ad);
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Friend");
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        recyclerView = findViewById(R.id.recycleViewm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        LoadUsers("");

    }

    private void LoadUsers(String s) {
        Query query = mRef.orderByChild("username").startAt(s).endAt(s + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query, Users.class).build();
        adapter = new FirebaseRecyclerAdapter<Users, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull Users model) {
                if (mUser.getUid().equals(getRef(position).getKey().toString())) {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                } else {

                    Picasso.get().load(model.getProfileImage()).into(holder.profileImage);
                    holder.username.setText(model.getUsername());
                    holder.profession.setText(model.getProfession());

                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(FriendAdActivity.this,ViewFriendActivity.class);
                        intent.putExtra("userKey",getRef(position).getKey().toString());
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_add_friend, parent, false);
                return new FindFriendViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LoadUsers(newText);
                return true;
            }
        });

        return true;
    }
}

