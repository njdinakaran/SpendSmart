package com.example.myprojectnew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    Button register;
    EditText email;
    TextInputEditText passwd, cpasswd;
    TextView login;
    TextInputLayout layoutpass1;
    TextInputEditText name;
    FirebaseAuth mAuth;

    FirebaseDatabase mydb;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        mydb=FirebaseDatabase.getInstance();
        name = findViewById(R.id.register_name);
        email = findViewById(R.id.register_email);
        passwd = findViewById(R.id.register_password);
        cpasswd = findViewById(R.id.register_cpassword);
        register = findViewById(R.id.rbutton);

        layoutpass1=findViewById(R.id.password_layout);

        login = findViewById(R.id.login_move);


        passwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass= s.toString();
                if(pass.length() >=8){
                    Pattern pat = Pattern.compile("[^a-zA-Z0-9]");
                    Matcher mat = pat.matcher(pass);
                    boolean isPwdContainsSpeChar = mat.find();
                    if(isPwdContainsSpeChar){
                        layoutpass1.setHelperText("Strong password");
                        layoutpass1.setError("");
                    }else {
                        layoutpass1.setHelperText("Weak Password, Use atleast One Special character");
                        layoutpass1.setError("");
                    }

                }else {
                    layoutpass1.setHelperText("Enter Minimum 8 characters");
                    layoutpass1.setError("");
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String myname = name.getText().toString();
                String myemail = email.getText().toString();
                String mypass = passwd.getText().toString();
                String mycpass = cpasswd.getText().toString();



                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+"[a-zA-Z0-9_+&*-]+)*@"+"(?:[a-zA-Z0-9-]+\\.)+[a-z" +"A-Z]{2,7}$";
                Pattern pat = Pattern.compile("[^a-zA-Z0-9]");
                Matcher mat = pat.matcher(mypass);
                boolean isPwdContainsSpeChar = mat.find();

                if (myname.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your Name", Toast.LENGTH_SHORT).show();
                } else if (myemail.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your Email", Toast.LENGTH_SHORT).show();
                } else if (mypass.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter your Password", Toast.LENGTH_SHORT).show();
                } else if (!myemail.matches(emailRegex)) {
                    Toast.makeText(Registration.this, "Enter a Valid Email Id", Toast.LENGTH_SHORT).show();
                } else if (mycpass.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter confirm password", Toast.LENGTH_SHORT).show();
                } else if (!isPwdContainsSpeChar) {
                    Toast.makeText(Registration.this, "Please Use atleast one special character in password", Toast.LENGTH_LONG).show();
                } else if (mypass.contains(" ")){
                    Toast.makeText(Registration.this, "No White Space Allowed for password", Toast.LENGTH_SHORT).show();
                } else if (!mypass.equals(mycpass)) {
                    Toast.makeText(getApplicationContext(), "Passwords doesn't match", Toast.LENGTH_SHORT).show();
                } else if(!mypass.equals("") && !myname.equals("") && !myemail.equals("") && !mycpass.equals("") && (mycpass.equals(mypass))){
                    mAuth.createUserWithEmailAndPassword(myemail, mypass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                mAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //add data to realtime database
                                                myRef= FirebaseDatabase.getInstance().getReference().child("UserInfo");
                                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                                                RegistrationData data = new RegistrationData(myname,myemail,mypass);

                                                String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                myRef.child(currentuser).setValue(data);

                                                name.setText("");
                                                email.setText("");
                                                passwd.setText("");
                                                cpasswd.setText("");
                                                Intent move = new Intent(getApplicationContext(), Login.class);
                                                startActivity(move);
                                                finish();
                                                Toast.makeText(getApplicationContext(), "New user created, Please Verify Your Email.", Toast.LENGTH_LONG).show();


                                            }
                                        });
                            } else {
                                Toast.makeText(getApplicationContext(), "User Not created", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent move=new Intent(getApplicationContext(),Login.class);
                startActivity(move);
                finish();
            }
        });

    }
}