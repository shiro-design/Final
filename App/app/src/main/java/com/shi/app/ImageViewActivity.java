package com.shi.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ImageViewActivity extends AppCompatActivity {


  
    ImageView imageView;
    Button btnDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView= findViewById(R.id.imageView);
        btnDown=findViewById(R.id.btnDown);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN );



        String url = getIntent().getStringExtra("url");
        Picasso.get().load(url).into(imageView);

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImg(url);
            }
        });
    }

    private void DownloadImg(String url) {
        Uri uri = Uri.parse(url);
        DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(this, DIRECTORY_DOWNLOADS,".jpg") ;
        downloadManager.enqueue(request);
    }

}