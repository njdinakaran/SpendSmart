package com.example.myprojectnew;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ExpenseDisplay extends AppCompatActivity {

    DatabaseReference myRef;
    TextView tvtodate, tvfromdate;
    Button subview, todashboard, expprint;
    PieChart mypie;

    ExpenseAdapter myAdapter;
    ArrayList<Expense_recycle> list;

    RecyclerView recyclerView;
    LinearLayout pagesize;
    File expensePDf;

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
        tvtodate.setText(enddate);

        setpie(newDate, c);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_display);

        tvtodate = findViewById(R.id.to_date);
        tvfromdate = findViewById(R.id.from_date);
        subview = findViewById(R.id.clk_viewexp);
        mypie = findViewById(R.id.piechart);
        todashboard = findViewById(R.id.expview_dash);
        expprint = findViewById(R.id.printExpense);
        pagesize = findViewById(R.id.expensesize);

        recyclerView = findViewById(R.id.recycyler_expense);
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference("Expense Database").child(currentuser);


        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0);

        Random random = new Random();

        // Generate a random integer between 0 and 9
        String randomInt = String.valueOf(random.nextInt(100000000));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            expensePDf = new File(storageVolume.getDirectory().getPath() + "/Download/SpendSmart_expense" + randomInt + ".pdf");
        }


        tvtodate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseDisplay.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        tvtodate.setText(dayOfMonth + "-" + month + "-" + year);
                    }
                },
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        tvfromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseDisplay.this, new DatePickerDialog.OnDateSetListener() {
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

        subview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mydateone = tvtodate.getText().toString();
                String mydatetwo = tvfromdate.getText().toString();

                mypie.clearChart();

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

        todashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(getApplicationContext(), DashBoard.class);
                startActivity(m);
                finish();
            }
        });
        expprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ExpenseDisplay.this, "Printing Pdf Document", Toast.LENGTH_SHORT).show();

                printAllCardViewsToPdf(recyclerView);

            }
        });


    }
    public void setpie(Date dateone, Date datetwo) {

        Date startDate = dateone;
        Date toDate = datetwo;

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();

        cal.setTime(toDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date newDate = cal.getTime();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new ExpenseAdapter(this, list);
        recyclerView.setAdapter(myAdapter);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Date currentdate;
                    int hometotal = 0, gifttotal = 0, shoptotal = 0, foodtotal = 0, medtotal = 0;


                    Expense_recycle data = dataSnapshot.getValue(Expense_recycle.class);
                    String thiscat = String.valueOf(data.getCategory());
                    String dbdate = String.valueOf(data.getDate());
                    DateFormat df = new SimpleDateFormat("dd-M-yyyy");
                    try {
                        currentdate = df.parse(dbdate);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    if (currentdate.after(startDate) && currentdate.before(newDate)) {

                        Log.i(TAG, "mydab dadadadadad" + data);
                        list.add(data);
                        Collections.reverse(list);

                        if (thiscat.equals("Home")) {
                            hometotal += data.getAmount();
                        } else if (thiscat.equals("Shopping")) {
                            shoptotal += data.getAmount();
                        } else if (thiscat.equals("Gift")) {
                            gifttotal += data.getAmount();
                        } else if (thiscat.equals("Food")) {
                            foodtotal += data.getAmount();
                        } else if (thiscat.equals("Medicine")) {
                            medtotal += data.getAmount();
                        }

                        mypie.addPieSlice(new PieModel("Home", hometotal, Color.parseColor("#555D50")));
                        mypie.addPieSlice(new PieModel("Gift", gifttotal, Color.parseColor("#32CD32")));
                        mypie.addPieSlice(new PieModel("Food", foodtotal, Color.parseColor("#BDB76B")));
                        mypie.addPieSlice(new PieModel("Shopping", shoptotal, Color.parseColor("#4169E1")));
                        mypie.addPieSlice(new PieModel("Medicine", medtotal, Color.parseColor("#DC143C")));
                        mypie.startAnimation();

                    }

                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void printAllCardViewsToPdf(RecyclerView recyclerView) {
        PdfDocument pdfDocument = new PdfDocument();

        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        int totalItems = adapter.getItemCount();
        Log.d(TAG, "printAllCardViewsToPdf: total items: " + totalItems);

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
            canvas.drawText("Expense Details", xpos - 100, ypos, detailsPaint);
            ypos += 100;
            Paint datePaint = new Paint();
            datePaint.setTextSize(55);
            canvas.drawText("From Date " + tvfromdate.getText().toString(), 150, ypos, datePaint);
            ypos += 100;
            canvas.drawText("To Date " + tvtodate.getText().toString(), 150, ypos, datePaint);


            int startItemIndex = i * itemsPerPage;
            int endItemIndex = Math.min(startItemIndex + itemsPerPage, totalItems);

            for (int j = startItemIndex; j < endItemIndex; j++) {
                Expense_recycle item = list.get(j);

                View cardViewLayout = LayoutInflater.from(this).inflate(R.layout.expense_view_card, null);

                TextView textView1 = cardViewLayout.findViewById(R.id.amount_data);
                TextView textView2 = cardViewLayout.findViewById(R.id.date_data);
                TextView textView3 = cardViewLayout.findViewById(R.id.category_data);
                TextView textView4 = cardViewLayout.findViewById(R.id.note_data);

                textView1.setText(String.valueOf(item.getAmount()));
                textView2.setText(item.getDate());
                textView3.setText(item.getCategory());
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
            pdfDocument.writeTo(new FileOutputStream(expensePDf));
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



}