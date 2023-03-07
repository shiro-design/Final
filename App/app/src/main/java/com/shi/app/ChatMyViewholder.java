package com.shi.app;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMyViewholder extends RecyclerView.ViewHolder {
    CircleImageView firstUserProfile,secondUserProfile;
    TextView firstUserText, secondUserText;

    public ChatMyViewholder(@NonNull View itemView) {
        super(itemView);

        firstUserProfile = itemView.findViewById(R.id.firstUserProfile);
        secondUserProfile = itemView.findViewById(R.id.secondUserProfile);
        firstUserText = itemView.findViewById(R.id.firstUserText);
        secondUserText = itemView.findViewById(R.id.secondUserText);

    }
}
