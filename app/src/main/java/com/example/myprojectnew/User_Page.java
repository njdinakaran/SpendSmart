package com.example.myprojectnew;

import static android.view.View.FOCUS_RIGHT;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBufferSafeParcelable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.BlockingDeque;

public class User_Page extends AppCompatActivity {

    TextView name,email,pass;
    Button move,delete;

    @Override
    protected void onStart() {
        super.onStart();

        String currentuser= FirebaseAuth.getInstance().getUid();
        DatabaseReference mref= FirebaseDatabase.getInstance().getReference().child("UserInfo").child(currentuser);

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RegistrationData data = snapshot.getValue(RegistrationData.class);

                    name.setText(data.getName());
                    email.setText(data.getEmail());

                }
            }
                @Override
                public void onCancelled (@NonNull DatabaseError error){

                }

        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        name=findViewById(R.id.username);
        email=findViewById(R.id.useremail);
        move=findViewById(R.id.movepg);
        delete=findViewById(R.id.userdelete);
        pass=findViewById(R.id.userpass);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(User_Page.this);

                 builder.setMessage("All the user Data will be removed permanently!");

                 builder.setTitle("Do You Want to Delete this Account?");

                builder.setCancelable(false);

                builder.setPositiveButton("Delete", (DialogInterface.OnClickListener) (dialog, which) -> {
                    Toast.makeText(User_Page.this, "Account Deleted successfully", Toast.LENGTH_SHORT).show();
                    deleteUserDataFromDatabase();
                });

                builder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
                    Toast.makeText(User_Page.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n = new Intent(User_Page.this, DashBoard.class);
                startActivity(n);
                finish();
            }
        });
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth=FirebaseAuth.getInstance();
                mAuth.sendPasswordResetEmail((String) email.getText()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(User_Page.this, "Check your email for Resent Link", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
    private void deleteUserDataFromDatabase() {
        Toast.makeText(this, "Deleting Account", Toast.LENGTH_SHORT).show();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Income Database").child(currentuser);
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Expense Database").child(currentuser);

        DatabaseReference mref = FirebaseDatabase.getInstance().getReference("UserInfo").child(currentuser);
        dref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                }
            }
        });
        dbref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                    }
            }
        });
        mref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent m = new Intent(getApplicationContext(), Registration.class);
                    startActivity(m);
                    finish();
                }
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(User_Page.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(User_Page.this, "Failed to delete account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}