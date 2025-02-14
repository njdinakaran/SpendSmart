package com.example.myprojectnew;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.GetChars;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddExpense extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    EditText mynote,myamount;
    TextView mydate;

    Button dashboard;
    Button submit;
    Spinner mycustomspinner;
    String mycatchoice;
    DatabaseReference myRef;

    ArrayList<ItemList> itemList;

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
        setContentView(R.layout.activity_add_expense);

        mycustomspinner=findViewById(R.id.exp_category);
        mydate=findViewById(R.id.expense_date);
        dashboard=findViewById(R.id.exp_dash);
        myamount=findViewById(R.id.expense_amount);
        mynote=findViewById(R.id.expense_note);
        submit=findViewById(R.id.exp_submit);

        itemList=getItemList();
        Exp_Adp adapter = new Exp_Adp(this,itemList);
        mycustomspinner.setAdapter(adapter);
        mycustomspinner.setOnItemSelectedListener(this);

        mydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                DatePickerDialog datePickerDialog=new DatePickerDialog(AddExpense.this, new DatePickerDialog.OnDateSetListener() {
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

                Date c = Calendar.getInstance().getTime();
                Date todate;
                DateFormat df = new SimpleDateFormat("dd-M-yyyy");
                try {
                    todate = df.parse(mydate.getText().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                if ((myamount.getText().toString()).equals("")) {
                    Toast.makeText(AddExpense.this, "Enter Amount", Toast.LENGTH_SHORT).show();
                } else if (todate.after(c)) {
                    Toast.makeText(AddExpense.this, "Enter Correct Date", Toast.LENGTH_SHORT).show();
                } else if (mynote.getText().toString().equals("")) {
                    Toast.makeText(AddExpense.this, "Enter Note", Toast.LENGTH_SHORT).show();
                } else {


                    int dbamt = Integer.parseInt(myamount.getText().toString());
                    String dbnote = mynote.getText().toString();

                    String dbdate = mydate.getText().toString();

                    ExpenseData data = new ExpenseData(dbamt, dbnote, mycatchoice, dbdate);
                    myRef = FirebaseDatabase.getInstance().getReference().child("Expense Database");
                    String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String id = myRef.push().getKey();
                    myRef.child(currentuser).child(id).setValue(data);

                    Toast.makeText(AddExpense.this, "New Expense Added", Toast.LENGTH_SHORT).show();

                    mydate.setText("");
                    mynote.setText("");
                    myamount.setText("");


                }
            }
        });

    }

    private ArrayList<ItemList> getItemList() {
        itemList=new ArrayList<>();
        itemList.add(new ItemList("Home",R.drawable.baseline_home_24));
        itemList.add(new ItemList("Shopping",R.drawable.baseline_shopping_cart_24));
        itemList.add(new ItemList("Gift",R.drawable.baseline_card_giftcard_24));
        itemList.add(new ItemList("Food",R.drawable.baseline_fastfood_24));
        itemList.add(new ItemList("Medicine",R.drawable.baseline_medical_services_24));
        return itemList;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        ItemList item = (ItemList) adapterView.getSelectedItem();
        Toast.makeText(this, item.getItemName(), Toast.LENGTH_SHORT).show();
        mycatchoice= item.getItemName();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


}