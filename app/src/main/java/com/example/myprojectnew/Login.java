package com.example.myprojectnew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    TextView pass_forgot;
    TextView signup_move;
    Button login_button;
    TextView lemail,lpassword;
    AppCompatButton mysignbutton;



    FirebaseAuth mAuth;
    private static int RC_SIGN=100;

    @Override
    protected void onStart() {

        super.onStart();
        if(mAuth.getCurrentUser()!=null)
        {
            Toast.makeText(this, "Logged in using "+mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            Intent m = new Intent(getApplicationContext(), DashBoard.class);
            startActivity(m);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        login_button=findViewById(R.id.button_login);
        signup_move=findViewById(R.id.sign_up);
        pass_forgot=findViewById(R.id.forgot_pass);
        lemail=findViewById(R.id.login_email);
        mysignbutton = findViewById(R.id.google_signin);
        lpassword=findViewById(R.id.login_password);

        pass_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                View dialogView = getLayoutInflater().inflate(R.layout.forgot_window, null);
                EditText emailBox = dialogView.findViewById(R.id.forgot_email);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String myemail = emailBox.getText().toString();
                        if (myemail.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(myemail).matches()){
                            Toast.makeText(Login.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mAuth.sendPasswordResetEmail(myemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Login.this, "Check your email for Resent Link", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(Login.this, "Please Check the Email Entered", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });

        signup_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent move = new Intent(getApplicationContext(), Registration.class);
                startActivity(move);
                finish();
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String myemail=lemail.getText().toString();
                String mypass=lpassword.getText().toString();
                if(myemail.equals("") || mypass.isEmpty()){
                    Toast.makeText(Login.this, "Please Fill All Details", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.signInWithEmailAndPassword(myemail,mypass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        if(mAuth.getCurrentUser().isEmailVerified()){
                                            Toast.makeText(Login.this, "WELCOME!", Toast.LENGTH_SHORT).show();
                                            Intent move = new Intent(getApplicationContext(),DashBoard.class);
                                            startActivity(move);
                                            finish();
                                        }else {
                                            Toast.makeText(Login.this, "Please Verify Email", Toast.LENGTH_LONG).show();
                                        }
                                    }else {
                                        Toast.makeText(Login.this, "Enter Registered Email Id", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


        mysignbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Login.this, GoogleSignInActivity.class);
                startActivity(intent);

            }
        });

    }
}



