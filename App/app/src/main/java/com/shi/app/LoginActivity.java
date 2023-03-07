package com.shi.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emaillog, passlog;
    Button btnlog;
    TextView forgotpass,createAcc;
    ProgressDialog mload;
    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emaillog = findViewById(R.id.inputEmailLog);
        passlog = findViewById(R.id.inputPassLog);
        btnlog = findViewById(R.id.btnLog);
        forgotpass = findViewById(R.id.txtForgot);
        createAcc = findViewById(R.id.txtCreate);
        mload= new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AtempLogin();
            }
        });
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPassWordActivity.class));

            }
        });

    }

    private void AtempLogin() {
        String emails = emaillog.getEditText().getText().toString();
        String paass = passlog.getEditText().getText().toString();

        if(emails.isEmpty() || !emails.contains("@gmail")){
            showErroe(emaillog,"Email is not Valid");
        }else if(paass.isEmpty() || paass.length() < 6)  {
            showErroe(passlog,"Password must be greated than 6 latter");
        }else{
            mload.setTitle("Login");
            mload.setMessage("Logining ...");
            mload.setCanceledOnTouchOutside(false);
            mload.show();
            mAuth.signInWithEmailAndPassword(emails,paass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                  if(task.isSuccessful()){
                          mload.dismiss();
                          Toast.makeText(LoginActivity.this, "Login is Succesfull", Toast.LENGTH_SHORT).show();
                          Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                          startActivity(intent);
                          finish();
                  }
                  else {
                      mload.dismiss();
                      Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                  }
                }
            });

        }

    }

    private void showErroe(TextInputLayout semaillog, String tes) {
        semaillog.setError(tes);
        semaillog.requestFocus();
    }
}