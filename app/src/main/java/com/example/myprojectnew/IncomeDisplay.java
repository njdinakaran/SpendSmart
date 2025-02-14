package com.example.myprojectnew;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.models.PieModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class IncomeDisplay extends AppCompatActivity {

    Button todash,toview,printinc;
    TextView tvfromdate,todate;
    FirebaseDatabase mydatabase;
    DatabaseReference myRef;
    IncomeAdapter myAdapter;
    ArrayList<Income_recycle> list;
    RecyclerView recyclerView;
    File incomePdf;
    LinearLayout pagesize;

    @Override
    protected void onStart() {
        super.onStart();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-M-yyyy", Locale.getDefault());
        String enddate = df.format(c);

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();

        cal.setTime(currentDate);
        cal.add(Calendar.MONTH, -1);
        Date newDate = cal.getTime();
        SimpleDateFormat nf = new SimpleDateFormat("dd-M-yyyy", Locale.getDefault());
        String fromdate = nf.format(newDate);
        tvfromdate.setText(fromdate);
        todate.setText(enddate);

        setpie(newDate,c);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_display);

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        todash = findViewById(R.id.incview_dash);
        toview = findViewById(R.id.clk_view);
        pagesize=findViewById(R.id.incomesize);

        tvfromdate = findViewById(R.id.from_date);
        todate = findViewById(R.id.to_date);
        recyclerView = findViewById(R.id.recycyler_income);

        todash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(getApplicationContext(), DashBoard.class);
                startActivity(m);
                finish();
            }
        });


        mydatabase = FirebaseDatabase.getInstance();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("Income Database").child(currentuser);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new IncomeAdapter(this, list);
        recyclerView.setAdapter(myAdapter);


        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0);

        Random random = new Random();

        // Generate a random integer between 0 and 9
        String randomInt = String.valueOf(random.nextInt(100000000));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            incomePdf = new File(storageVolume.getDirectory().getPath() + "/Download/SpendSmart_income" + randomInt + ".pdf");
        }

        tvfromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(IncomeDisplay.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        tvfromdate.setText(dayOfMonth + "-" + month + "-" + year);
                    }
                },
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(IncomeDisplay.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        todate.setText(dayOfMonth + "-" + month + "-" + year);
                    }
                },
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        toview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mydateone = todate.getText().toString();
                String mydatetwo = tvfromdate.getText().toString();
                Date todate, fromdate;
                DateFormat df = new SimpleDateFormat("dd-M-yyyy");
                try {
                    todate = df.parse(mydateone);
                    fromdate = df.parse(mydatetwo);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                setpie(fromdate, todate);

            }
        });

        printinc = findViewById(R.id.printIncome);

        printinc.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                Toast.makeText(IncomeDisplay.this, "Printing Pdf Document", Toast.LENGTH_SHORT).show();
                int itemsPerPage = 2;
                int totalitem = recyclerView.getAdapter().getItemCount();
                int pageCount = (int) Math.ceil((double) totalitem / itemsPerPage);
                PdfDocument pdfDocument = new PdfDocument();
                printAllCardViewsToPdf(recyclerView);

            }
        });
    }


    private void printAllCardViewsToPdf(RecyclerView recyclerView) {
        PdfDocument pdfDocument = new PdfDocument();

        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        int totalItems = adapter.getItemCount();

        int itemsPerPage = 3;

        int pageCount = (int) Math.ceil((double) totalItems / itemsPerPage);

        for (int i = 0; i < pageCount; i++) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pagesize.getWidth(), pagesize.getHeight(), i + 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            float xpos = (canvas.getWidth() - 100) / 2;
            float ypos = 120f;
            Paint titlePaint = new Paint();
            titlePaint.setTextSize(90f);
            titlePaint.setColor(800080);
            titlePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            canvas.drawText("Spend Smart", xpos, ypos, titlePaint);
            ypos += 100;
            Paint detailsPaint = new Paint();
            detailsPaint.setTextSize(70f);
            detailsPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            canvas.drawText("Income Details", xpos - 100, ypos, detailsPaint);
            ypos += 100;
            Paint datePaint = new Paint();
            datePaint.setTextSize(55);
            canvas.drawText("From Date " + tvfromdate.getText().toString(), 150, ypos, datePaint);
            ypos += 100;
            canvas.drawText("To Date " + todate.getText().toString(), 150, ypos, datePaint);


            int startItemIndex = i * itemsPerPage;
            int endItemIndex = Math.min(startItemIndex + itemsPerPage, totalItems);

            for (int j = startItemIndex; j < endItemIndex; j++) {
                Income_recycle item = list.get(j);

                View cardViewLayout = LayoutInflater.from(this).inflate(R.layout.income_recycler_data, null);

                TextView textView1 = cardViewLayout.findViewById(R.id.amount_data);
                TextView textView2 = cardViewLayout.findViewById(R.id.date_data);
                TextView textView4 = cardViewLayout.findViewById(R.id.note_data);

                textView1.setText(String.valueOf(item.getAmount()));
                textView2.setText(item.getDate());
                textView4.setText(item.getNote());

                Bitmap cardViewBitmap = createBitmapFromView(cardViewLayout);

                if (cardViewBitmap != null) {
                    float cardXPos = (canvas.getWidth() - cardViewBitmap.getWidth()) / 2f;
                    float cardYPos = ypos + 200;
                    canvas.drawBitmap(cardViewBitmap, cardXPos, cardYPos, null);
                    ypos += cardViewBitmap.getHeight() + 100;
                }
            }

            pdfDocument.finishPage(page);
        }
        try {
            pdfDocument.writeTo(new FileOutputStream(incomePdf));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        pdfDocument.close();
    }

    private Bitmap createBitmapFromView(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, pagesize.getWidth()-200, view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void setpie(Date dateone, Date datetwo) {

        Date startDate=dateone;
        Date toDate=datetwo;

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();

        cal.setTime(toDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date newDate = cal.getTime();



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new IncomeAdapter(this, list);
        recyclerView.setAdapter(myAdapter);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Date currentdate;
                    Income_recycle data = dataSnapshot.getValue(Income_recycle.class);
                    String dbdate=String.valueOf(data.getDate());
                    DateFormat df = new SimpleDateFormat("dd-M-yyyy");
                    try {
                        currentdate = df.parse(dbdate);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    if( currentdate.after (startDate) && currentdate.before (newDate)){
                        list.add(data);

                        Collections.reverse(list);

                    }

                }
                myAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}


class Income_recycle {

    public int amount;
    public String note;
    public String date;
    public String category;
    public int getAmount() {
        return amount;
    }
    public String getNote() {
        return note;
    }
    public String getDate() {
        return date;
    }
}