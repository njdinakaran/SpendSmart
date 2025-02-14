package com.example.myprojectnew;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Analyze_data extends AppCompatActivity {

    Button move;
    TextView totalexp,totalinc;
    TextView monthone,monthtwo,monththree,monthfour,monthfive;
    TextView amtone,amttwo,amtthree,amtfour,amtfive;

    LinearLayout mylayout;
    int[] month = new int[13];
    BarChart barChart;

    DatabaseReference mrefinc,mref;
    TextView suggest;




    @Override
    protected void onStart() {
        super.onStart();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        Date firstDayOfYear = calendar.getTime();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR,1);
        Date firstd=cal.getTime();
        SimpleDateFormat nf = new SimpleDateFormat("dd-M-yyyy");
        String fromdate = nf.format(firstd);


        Date myfirst;
        DateFormat df = new SimpleDateFormat("dd-M-yyyy");
        try {
            myfirst = df.parse(fromdate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Date enddate = Calendar.getInstance().getTime();
        final int[] fullexp = {0};
        final int[] fullinc={0};


        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mref = FirebaseDatabase.getInstance().getReference().child("Expense Database").child(currentuser);

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense_recycle data = dataSnapshot.getValue(Expense_recycle.class);

                    String dbdate = (data.getDate());

                    Date currentdate;
                    DateFormat df = new SimpleDateFormat("dd-M-yyyy");
                    try {
                        currentdate = df.parse(dbdate);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    if(currentdate.after(firstd) && currentdate.before(enddate)){
                        fullexp[0] += (data.getAmount());
                        totalexp.setText(String.valueOf(fullexp[0]));

                        int mymonth=currentdate.getMonth()+1;

                        month[mymonth]+=data.getAmount();

                        String monthName = Month.of(mymonth).name();

                   }

                }


                MonthWithAmount[] monthsWithAmount = new MonthWithAmount[month.length - 1];
                for (int i = 1; i < month.length; i++) {
                    int monthv = i;
                    int totalAmount = month[i];
                    monthsWithAmount[i - 1] = new MonthWithAmount(monthv, totalAmount, i);
                }

                Arrays.sort(monthsWithAmount, Comparator.comparingInt(MonthWithAmount::getTotalAmount).reversed());
                int limit = Math.min(5, monthsWithAmount.length);
                for (int i = 0; i < limit; i++) {
                    MonthWithAmount monthWithAmount = monthsWithAmount[i];
                }

                monthone.setText(Month.of(monthsWithAmount[0].getMonth()).name());
                amtone.setText(String.valueOf(monthsWithAmount[0].getTotalAmount()));

                monthtwo.setText(Month.of(monthsWithAmount[1].getMonth()).name());
                amttwo.setText(String.valueOf(monthsWithAmount[1].getTotalAmount()));
                monththree.setText(Month.of(monthsWithAmount[2].getMonth()).name());
                amtthree.setText(String.valueOf(monthsWithAmount[2].getTotalAmount()));
                monthfour.setText(Month.of(monthsWithAmount[3].getMonth()).name());
                amtfour.setText(String.valueOf(monthsWithAmount[3].getTotalAmount()));
                monthfive.setText(Month.of(monthsWithAmount[4].getMonth()).name());
                amtfive.setText(String.valueOf(monthsWithAmount[4].getTotalAmount()));



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        mrefinc = FirebaseDatabase.getInstance().getReference().child("Income Database").child(currentuser);

        mrefinc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Income_recycle data = dataSnapshot.getValue(Income_recycle.class);

                    String dbdate = (data.getDate());

                    Date currentdate;
                    DateFormat df = new SimpleDateFormat("dd-M-yyyy");
                    try {
                        currentdate = df.parse(dbdate);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    if (currentdate.after(firstd) && currentdate.before(enddate)) {
                        fullinc[0] += (data.getAmount());
                        totalinc.setText(String.valueOf(fullinc[0]));

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    class MonthWithAmount {
        private int month;
        private int totalAmount;
        private int index;

        public MonthWithAmount(int month, int totalAmount, int index) {
            this.month = month;
            this.totalAmount = totalAmount;
            this.index = index;
        }

        public int getMonth() {
            return month;
        }

        public int getTotalAmount() {
            return totalAmount;
        }

        public int getIndex() {
            return index;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_data);

        move=findViewById(R.id.todashboard);
        totalexp = findViewById(R.id.showtotalexp);
        totalinc=findViewById(R.id.showtotalinc);
        monthone=findViewById(R.id.toponemonth);
        monthtwo=findViewById(R.id.toptwomonth);
        monththree=findViewById(R.id.topthreemonth);
        monthfour=findViewById(R.id.topfourmonth);
        monthfive=findViewById(R.id.topfivemonth);

        amtone=findViewById(R.id.toponeamt);
        amttwo=findViewById(R.id.toptwoamt);
        amtthree=findViewById(R.id.topthreeamt);
        amtfive=findViewById(R.id.topfiveamt);
        amtfour=findViewById(R.id.topfouramt);
        mylayout=findViewById(R.id.linearLayout6);

        AlertDialog.Builder builder = new AlertDialog.Builder(Analyze_data.this);
        View dialogView = getLayoutInflater().inflate(R.layout.expense_bar_plot, null);

        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(getApplicationContext(), DashBoard.class);
                startActivity(m);
                finish();
            }
        });

        mylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateBarChart();
            }
        });



    }

    private void populateBarChart() {
        List<BarEntry> entries = new ArrayList<>();



        for (int i = 1; i < month.length; i++) {
            entries.add(new BarEntry(i, month[i]));
        }

        String[] monthnames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};



        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.expense_bar_plot, null);
        BarChart barChart = dialogView.findViewById(R.id.barChart);
        BarDataSet dataSet = new BarDataSet(entries, "Monthly Expenses");

        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(16f);
        BarData barData = new BarData(dataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Expense Graph");
        barChart.animateY(2000);


        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthnames));

        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(5);
        float barSpace = 0.1f;
        barData.setBarWidth(0.15f);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.animate();
        barChart.invalidate();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = (int) (width * 1.25);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        barChart.setLayoutParams(layoutParams);

        dialog.show();
    }

}