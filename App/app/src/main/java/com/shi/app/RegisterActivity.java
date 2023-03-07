package com.shi.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout email,pass,cfpass;
    Button btnreg;
    TextView alacc ;
    FirebaseAuth mAuth;
    ProgressDialog mload;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.inputEmailLog);
        pass = findViewById(R.id.inputPassReg);
        cfpass = findViewById(R.id.inputRPassReg);
        btnreg = findViewById(R.id.btnReg);
        alacc = findViewById(R.id.txthaveAcc);
        mAuth = FirebaseAuth.getInstance();

        mload = new ProgressDialog(this);

        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AteempReg();
            }
        });
        alacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void AteempReg() {
        String emails = email.getEditText().getText().toString();
        String paass = pass.getEditText().getText().toString();
        String cfpas = cfpass.getEditText().getText().toString();
        
        if(emails.isEmpty() || !emails.contains("@gmail")){
            showErroe(email,"Email is not Valid");
        }else if(paass.isEmpty() || paass.length() < 6)  {
            showErroe(pass,"Password must be greated than 6 latter");
        }else if(!cfpas.equals(paass)){
            showErroe(cfpass,"Password did not match");
        }else{
            mload.setTitle("Registion");
            mload.setMessage("Registing");
            mload.setCanceledOnTouchOutside(false);
            mload.show();
            mAuth.createUserWithEmailAndPassword(emails,paass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mload.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration is Succesfull", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this,SetupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }else {
                        mload.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration is Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void showErroe(TextInputLayout semails, String tes) {
        semails.setError(tes);
        semails.requestFocus();

    }
}