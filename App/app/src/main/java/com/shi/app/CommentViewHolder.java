package com.shi.app;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImage;
    TextView username,comment;
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImage = itemView.findViewById(R.id.profileImageCommnent);
        username = itemView.findViewById(R.id.usernameCmt);
        comment = itemView.findViewById(R.id.cmtTextView);

    }
}
