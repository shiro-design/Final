package com.shi.app;

import android.view.View;
import android.widget.TextView;
import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profileImage;
    TextView username,profession;
    public FindFriendViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.profileImage);
        username = itemView.findViewById(R.id.username);
        profession = itemView.findViewById(R.id.profession);

    }

}
