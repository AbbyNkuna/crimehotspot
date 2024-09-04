package com.example.crimehotspotapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crimehotspotapp.Common.common;
import com.example.crimehotspotapp.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    TextView register,Forgot;
    CardView Login;
    TextInputEditText Email,Password;
    ProgressDialog mDialog;
    FirebaseAuth auth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        Login = findViewById(R.id.Login);
        Forgot = findViewById(R.id.Forgot);
        Forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(Email.getText().toString())){
                    Email.setError("Please Enter Your  Email Address");
                    return;
                }
                mDialog  = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Sending The Reset Link To Your Password...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                auth.sendPasswordResetEmail(Email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDialog.dismiss();
                        Toast.makeText(SignIn.this, "Link Was Send Successfully ", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        Toast.makeText(SignIn.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                mDialog  = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Accessing Your Account...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
                auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(Email.getText().toString(),Password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(final AuthResult authResult) {


                        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
                        users.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(authResult.getUser().getUid()).exists()){
                                    User user = dataSnapshot.child(authResult.getUser().getUid()).getValue(User.class);
                                    if (user!=null){
                                        Paper.init(SignIn.this);
                                        Paper.book().write("UserID",authResult.getUser().getUid());
                                        Paper.book().write("Name",user.getName() +" "+user.getSurname());


                                        mDialog.dismiss();

                                        Intent i = new Intent(SignIn.this, Home.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        overridePendingTransition(0,0);

                                    }
                                }else {
                                    mDialog.dismiss();
                                    Toast.makeText(SignIn.this, "There Is No User In That Name ", Toast.LENGTH_SHORT).show();
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        Toast.makeText(SignIn.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        register = findViewById(R.id.signup);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), SignUp.class));
            }
        });
    }
}