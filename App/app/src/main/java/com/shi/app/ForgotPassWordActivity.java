package com.shi.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassWordActivity extends AppCompatActivity {

    EditText inputemail;
    Button btnSend;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_word);

        inputemail=findViewById(R.id.inputPassReset);
        btnSend= findViewById(R.id.btnReset);
        mAuth = FirebaseAuth.getInstance();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputemail.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(ForgotPassWordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPassWordActivity.this, "Please check your Email", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ForgotPassWordActivity.this, "Email not valid. Please enter your Email ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}