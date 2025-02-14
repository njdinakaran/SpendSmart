package com.example.myprojectnew;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class DashBoard extends AppCompatActivity {

    CardView home,exp,inc,user,log,view_exp,view_inc,analyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board_2);

        home=findViewById(R.id.card_home);
        exp=findViewById(R.id.card_exp);
        inc=findViewById(R.id.card_inc);
        view_exp=findViewById(R.id.card_analytics);
        view_inc=findViewById(R.id.card_view_exp);
        log=findViewById(R.id.card_logout);
        analyzer=findViewById(R.id.card_analyze);
        user=findViewById(R.id.card_user);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent move = new Intent(getApplicationContext(), HomePage.class);
                startActivity(move);
                finish();
            }
        });
        exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent move = new Intent(getApplicationContext(), AddExpense.class);
                startActivity(move);
                finish();
            }
        });
        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent move = new Intent(getApplicationContext(), AddIncome.class);
                startActivity(move);
                finish();
            }
        });

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent move = new Intent(getApplicationContext(), Login.class);
                startActivity(move);
                finish();
            }
        });
        view_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent move = new Intent(getApplicationContext(), ExpenseDisplay.class);
                startActivity(move);
                finish();
            }
        });
        view_inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(getApplicationContext(), IncomeDisplay.class);
                startActivity(m);
                finish();
            }
        });

        analyzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(getApplicationContext(),Analyze_data.class);
                startActivity(m);
                finish();
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(getApplicationContext(), User_Page.class);
                startActivity(m);
                finish();
            }
        });


    }
}