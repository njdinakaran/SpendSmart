package com.example.myprojectnew;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class HomePage extends AppCompatActivity {


    Button dashboard;
    TextView myname;

    DatabaseReference myRef;
    DatabaseReference myExp;
    TextView mytotal;
    FirebaseDatabase mydatabase;
    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    ExpenseAdapter myAdapter;
    ArrayList<Expense_recycle> list;

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        dashboard = findViewById(R.id.home_dash);
        myname = findViewById(R.id.username);
        mytotal = findViewById(R.id.total_amount);

        mAuth = FirebaseAuth.getInstance();

        mydatabase = FirebaseDatabase.getInstance();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(currentuser);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myus = snapshot.child("name").getValue(String.class);
                myname.setText(myus.toUpperCase());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recyclerView = findViewById(R.id.myrecycler_view);
        myExp = FirebaseDatabase.getInstance().getReference("Expense Database").child(currentuser);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new ExpenseAdapter(this, list);
        recyclerView.setAdapter(myAdapter);
        myExp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    int totalamount=0;

                    Expense_recycle data = dataSnapshot.getValue(Expense_recycle.class);
                    list.add(data);
                    Collections.reverse(list);

                    String dbdate=String.valueOf(data.getDate());
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-M-yyyy", Locale.getDefault());
                    String datetwo = df.format(c);

                    if(datetwo.equals(dbdate)) {
                        totalamount += data.getAmount();
                        String mytotalvalue = String.valueOf(totalamount);
                        mytotal.setText(mytotalvalue);
                    }

                }
                myAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(getApplicationContext(), DashBoard.class);
                startActivity(m);
                finish();
            }
        });
    }
}

