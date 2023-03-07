package com.shi.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 10 ;
    CircleImageView profi;
    TextInputLayout inputUser,inputCity, inputPro,inputHobby;
    Button btnSave;
    Uri imgUri;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    FirebaseDatabase database;
    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        profi = findViewById(R.id.profile_image);
        inputUser = findViewById(R.id.inputUsername);
        inputCity = findViewById(R.id.inputCity);
        inputPro = findViewById(R.id.inputPro);
        inputHobby = findViewById(R.id.inputHobby);
        btnSave = findViewById(R.id.btnSave);
        database= FirebaseDatabase.getInstance("https://appshi-56faa-default-rtdb.firebaseio.com");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = database.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImage");
        progressDialog= new ProgressDialog(this);
        toolbar= findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setup Profile");




        profi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();
            }
        });

    }

    private void SaveData() {

        String username = inputUser.getEditText().getText().toString();
        String city = inputCity.getEditText().getText().toString();
        String profes = inputPro.getEditText().getText().toString();
        String hobb = inputHobby.getEditText().getText().toString();


        if(username.isEmpty()|| username.length()<3){
            showError(inputUser,"User name is not Valid");
        }else if(city.isEmpty()|| city.length()<3){
            showError(inputCity,"City is not Valid");
        }else if(profes.isEmpty()|| profes.length()<2){
            showError(inputPro,"Profession is not Valid");
        }else if (imgUri == null){
            Toast.makeText(this, "Please select avatar", Toast.LENGTH_SHORT).show();
        }else {

            progressDialog.setTitle("adding Setup Profile");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            storageReference.child(mUser.getUid()).putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        storageReference.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashMap =new HashMap();
                                hashMap.put("username",username);
                                hashMap.put("city",city);
                                hashMap.put("profession",profes);
                                hashMap.put("hobby",hobb);
                                hashMap.put("profileImage",uri.toString());
                                hashMap.put("status","offline");


                                mRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Intent intent = new Intent(SetupActivity.this,MainActivity.class);
                                        startActivity(intent);

                                        progressDialog.dismiss();
                                        Toast.makeText(SetupActivity.this, "Setup profile  completed", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(SetupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }

    }

    private void showError(TextInputLayout seput, String tes) {
            seput.setError(tes);
            seput.requestFocus();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!=null){
        imgUri = data.getData();
        profi.setImageURI(imgUri);
         }
    }



}