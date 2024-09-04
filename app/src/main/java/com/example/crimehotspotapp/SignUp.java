package com.example.crimehotspotapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crimehotspotapp.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
 TextInputEditText Name,Surname,Email,Password,Repassword,Phone;
 CardView Register;
 TextView SignIn;
    ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(Name.getText().toString())) {
                    Name.setError("Please Enter Your First Name");

                    return;
                }
                if (TextUtils.isEmpty(Surname.getText().toString())) {
                    Surname.setError("Please Enter Your Surname");
                    return;
                }
                if (TextUtils.isEmpty(Phone.getText().toString())) {
                    Phone.setError("Please Enter Your Contact Number");
                    return;
                }
                if (Phone.getText().toString().length() < 10) {
                    Phone.setError("Contact Must Be 10 Digits");
                    return;
                }
                if (TextUtils.isEmpty(Email.getText().toString())) {
                    Email.setError("Please Enter Your  Email Address");
                    return;
                }
                if (TextUtils.isEmpty(Password.getText().toString())) {
                    Password.setError("Please Enter Your Password");
                    return;
                }
                if (Password.getText().toString().length() < 4) {
                    Password.setError("Password Must Be More Than Four Values");
                    return;
                }
                if (!Password.getText().toString().equals(Repassword.getText().toString())) {
                    Repassword.setError("Passwords Do Not Match !!");
                    return;
                }
                mDialog  = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Registering Your Account...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                FirebaseAuth auth =  FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(Email.getText().toString(),Password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
                        User user = new User();
                        user.setName(Name.getText().toString());
                        user.setSurname(Surname.getText().toString());
                        user.setPhone(Phone.getText().toString());
                        databaseReference.child(authResult.getUser().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(SignUp.this, "Account Created", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                                startActivity(new Intent(SignUp.this, com.example.crimehotspotapp.SignIn.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, com.example.crimehotspotapp.SignIn.class));
            }
        });
    }
    private void init(){
        Name = findViewById(R.id.Name);
        Surname = findViewById(R.id.Surname);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        Repassword = findViewById(R.id.Repassword);
        Phone = findViewById(R.id.Phone);
        Register = findViewById(R.id.Register);
        SignIn = findViewById(R.id.SignIn);


    }
}