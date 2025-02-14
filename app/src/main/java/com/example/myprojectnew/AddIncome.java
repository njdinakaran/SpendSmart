package com.example.myprojectnew;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddIncome extends AppCompatActivity {

    EditText myamount,mynote;

    TextView mydate;


    Button dashboard;
    Button submit;


    DatabaseReference myRef;

    @Override
    protected void onStart() {
        super.onStart();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-M-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        mydate.setText(formattedDate);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        submit=findViewById(R.id.income_submit);
        mydate=findViewById(R.id.income_date);
        dashboard=findViewById(R.id.inc_dash);
        myamount=findViewById(R.id.income_amount);
        mynote=findViewById(R.id.income_note);
        mydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                DatePickerDialog datePickerDialog=new DatePickerDialog(AddIncome.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month=month+1;
                        mydate.setText(dayOfMonth+"-"+month+"-"+year);
                    }
                },
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent move = new Intent(getApplicationContext(), DashBoard.class);
                startActivity(move);
                finish();
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dbdate = mydate.getText().toString();
                String dbnote = mynote.getText().toString();
                Date c = Calendar.getInstance().getTime();
                Date todate;
                DateFormat df = new SimpleDateFormat("dd-M-yyyy");
                try {
                    todate = df.parse(mydate.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                if (dbnote.equals("")) {
                    Toast.makeText(AddIncome.this, "Enter Note", Toast.LENGTH_SHORT).show();
                } else if (todate.after(c)) {
                    Toast.makeText(AddIncome.this, "Enter Correct Date", Toast.LENGTH_SHORT).show();
                } else if ((myamount.getText().toString()).equals("")) {
                    Toast.makeText(AddIncome.this, "Enter Amount", Toast.LENGTH_SHORT).show();
                } else {
                    int dbamount = Integer.parseInt(myamount.getText().toString());

                    IncomeData data = new IncomeData(dbamount, dbnote, dbdate);

                    myRef = FirebaseDatabase.getInstance().getReference().child("Income Database");
                    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String id = myRef.push().getKey();
                    myRef.child(currentuser).child(id).setValue(data);

                    Toast.makeText(AddIncome.this, "New Income Added", Toast.LENGTH_SHORT).show();

                    mydate.setText("");
                    mynote.setText("");
                    myamount.setText("");
                }
            }
        });

    }
}