package com.DoctorCalc.pogcalc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

public class ReversePOGCalcUsg extends Activity {

    public Calendar month;
    public CalendarAdapter adapter;
    public Handler handler;
    public ArrayList<String> items; // container to store some random calendar items
    TextView tvPOG;
    TextView tvTopPOG;
    TextView tvSelect;
    EditText etWeeks;
    EditText etDays;
    TextView tvPOGonUSG;
    String dt = "00-00-0000";
    Button btnSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revpog_usg);
        month = Calendar.getInstance();
        tvSelect = (TextView) findViewById(R.id.tvSelect);
        tvPOG = (TextView) findViewById(R.id.tvPOG);
        tvTopPOG = (TextView) findViewById(R.id.tvTopPOG);
        etWeeks = (EditText) findViewById(R.id.etWeeks);
        etDays = (EditText) findViewById(R.id.etDays);
        tvPOGonUSG = (TextView) findViewById(R.id.tvPOGonUSG);
        btnSub = (Button) findViewById(R.id.btnSub);



        items = new ArrayList<String>();
        adapter = new CalendarAdapter(this, month);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(adapter);

        handler = new Handler();
        handler.post(calendarUpdater);

        TextView title  = (TextView) findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        TextView previous  = (TextView) findViewById(R.id.previous);

        previous.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
                } else {
                    month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
                }
                refreshCalendar();
            }
        });

        TextView next  = (TextView) findViewById(R.id.next);

        next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
                } else {
                    month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
                }
                refreshCalendar();

            }
        });

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextView date = (TextView)v.findViewById(R.id.date);
                if(date instanceof TextView && !date.getText().equals("")) {
                    String day = date.getText().toString();
                    if(day.length()==1) {
                        day = "0"+day;
                    }

                    //String dt = android.text.format.DateFormat.format("yyyy-MM", month)+"-"+day;
                    dt = day+"-"+ android.text.format.DateFormat.format("MM-yyyy", month);
                    dateValidate(dt);

                }

            }



        });

        etWeeks = (EditText) findViewById(R.id.etWeeks);
        etWeeks.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                etWeeks.setText("");
            }
        });
        etDays = (EditText) findViewById(R.id.etDays);
        etDays.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                etDays.setText("");
            }
        });

//        etWeeks = (EditText) findViewById(R.id.etWeeks);
//        etDays = (EditText) findViewById(R.id.etDays);
//
//        etWeeks.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                etWeeks.setText("");
//            }
//        });
//        etDays.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                etDays.setText("");
//            }
//        });
//
//        etWeeks.addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {}
//
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start,
//                                      int before, int count) {
//
////                    String weekstest = etWeeks.getText().toString();
////                    int weeksUSG = Integer.parseInt(weekstest);
////                if(weeksUSG > 40 ) {
////                    etWeeks.setError("should be < 40");
////                    //weekstest = "0";
////                    return;
////                }
//
//                dateValidate(dt);
//            }
//        });
//
//        etDays.addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {}
//
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start,
//                                      int before, int count) {
////                String daystest = etDays.getText().toString();
////                int daysUSG = Integer.parseInt(daystest);
////                if(daysUSG > 6 ) {
////                    etWeeks.setError("should be < 7");
////                    //weekstest = "0";
////                    return;
////                }
//
//                dateValidate(dt);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pogcalc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        infoDialog();
        return true;
    }

    public void dateValidate(String dateEntered) {
        int diffDay = 0;
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c1 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(dateEntered));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long milliSecD1 = c1.getTimeInMillis();

        Calendar c2 = Calendar.getInstance();
        c2.setTime(today);
        long milliSecD2 = c2.getTimeInMillis();

        long diffInMilis = milliSecD2 - milliSecD1;
        long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);
        if (diffInMilis < 0) {
            //		tvHola.setVisibility(View.INVISIBLE);
            tvPOG.setVisibility(View.INVISIBLE);
            tvTopPOG.setVisibility(View.INVISIBLE);
            tvSelect.setVisibility(View.VISIBLE);
            tvSelect.setText("Date clicked has not arrived yet..Please select your LMP date..");
            // Toast.makeText(getApplicationContext(), "Date clicked is not yet arrived..Please select your LMP date.." , Toast.LENGTH_SHORT).show();
        } else if (diffInDays > 300) {
            tvPOG.setVisibility(View.INVISIBLE);
            tvTopPOG.setVisibility(View.INVISIBLE);
            tvSelect.setVisibility(View.VISIBLE);
            tvSelect.setText("Please enter a date less than 10 months");
            // Toast.makeText(getApplicationContext(), "Please enter a date less than 10 months" , Toast.LENGTH_SHORT).show();
        } else {
            tvSelect.setVisibility(View.VISIBLE);
            tvSelect.setText("Selected Ultra Sound Date : "+ dateEntered);
            etWeeks.setVisibility(View.VISIBLE);
            etDays.setVisibility(View.VISIBLE);
            tvPOGonUSG.setVisibility(View.VISIBLE);
            btnSub.setVisibility(View.VISIBLE);
           // calcPOGUsg(dateEntered);
        }

    }



    public void onSubmit(View v){



        etWeeks = (EditText) findViewById(R.id.etWeeks);
        String weekstest = "0";
        weekstest = etWeeks.getText().toString();
        if(TextUtils.isEmpty(weekstest)) {
            //etWeeks.setError("cannot be empty");
            weekstest = "0";
            //return;
        }
        int weeksUSG = Integer.parseInt(weekstest);
        if(weeksUSG > 40 ) {
            etWeeks.setError("should be < 40");
        }

        etDays = (EditText) findViewById(R.id.etDays);
        String daystest = "0";
        daystest = etDays.getText().toString();
        if(TextUtils.isEmpty(daystest)) {
            //etDays.setError("cannot be empty");
            daystest = "0";
            //return;
        }

        int daysUSG = Integer.parseInt(daystest);
        if(daysUSG > 6 ) {
            etDays.setError("should be < 7");
        }




        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c1 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long milliSecD1 = c1.getTimeInMillis();

        Calendar c2 = Calendar.getInstance();
        c2.setTime(today);
        long milliSecD2 = c2.getTimeInMillis();

        long diffInMilis = milliSecD2 - milliSecD1;

        long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);
        int weeks = (int) (diffInDays/7);
        int days = (int) (diffInDays % 7);
        int pogWeek = weeks + weeksUSG;
        int pogDay = days + daysUSG;
        if (pogDay >= 7) {
            pogWeek += 1;
            pogDay -= 7;
        }
        tvTopPOG.setVisibility(View.VISIBLE);
        tvPOG.setVisibility(View.VISIBLE);
        tvPOG.setText(pogWeek + " weeks " + pogDay + " days");

//        etWeeks.addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {}
//
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start,
//                                      int before, int count) {
//
////                    String weekstest = etWeeks.getText().toString();
////                    int weeksUSG = Integer.parseInt(weekstest);
////                if(weeksUSG > 40 ) {
////                    etWeeks.setError("should be < 40");
////                    //weekstest = "0";
////                    return;
////                }
//
//                dateValidate(dt);
//            }
//        });
//
//        etDays.addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {}
//
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//            }
//
//            public void onTextChanged(CharSequence s, int start,
//                                      int before, int count) {
////                String daystest = etDays.getText().toString();
////                int daysUSG = Integer.parseInt(daystest);
////                if(daysUSG > 6 ) {
////                    etWeeks.setError("should be < 7");
////                    //weekstest = "0";
////                    return;
////                }
//
//                dateValidate(dt);
//            }
//        });

    }

    public void calcPOGUsg(String dateEntered) {
        int diffDay = 0;

        String weekstest = etWeeks.getText().toString();
        String daystest = etDays.getText().toString();
        if(TextUtils.isEmpty(weekstest)) {
            //etWeeks.setError("cannot be empty");
            weekstest = "0";
            //return;
        }
        if(TextUtils.isEmpty(daystest)) {
            //etDays.setError("cannot be empty");
            daystest = "0";
            //return;
        }


        int weeksUSG = Integer.parseInt(weekstest);
        int daysUSG = Integer.parseInt(daystest);
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c1 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(dateEntered));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long milliSecD1 = c1.getTimeInMillis();

        Calendar c2 = Calendar.getInstance();
        c2.setTime(today);
        long milliSecD2 = c2.getTimeInMillis();

        long diffInMilis = milliSecD2 - milliSecD1;

        long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);
        int weeks = (int) (diffInDays/7);
        int days = (int) (diffInDays % 7);
        int pogWeek = weeks + weeksUSG;
        int pogDay = days + daysUSG;
        if (pogDay >= 7) {
            pogWeek += 1;
            pogDay -= 7;
        }

        tvPOG.setText(pogWeek + " weeks " + pogDay + " days");
    }


    public void validateUSGDate() {
    }


        public void infoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final FrameLayout frameView = new FrameLayout(this);
        builder.setView(frameView);
        builder.setTitle("INFO")
                .setIcon(R.drawable.qmark)
                .setNegativeButton("Done",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.info, frameView);
        alertDialog.show();
    }

    public void refreshCalendar()
    {
        TextView title  = (TextView) findViewById(R.id.title);
        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        handler.post(calendarUpdater); // generate some random calendar items
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
    }

    public void onNewIntent(Intent intent) {
        String date = intent.getStringExtra("date");
        String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
        month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public Runnable calendarUpdater = new Runnable() {
        @Override
        public void run() {
            items.clear();
            // format random values. You can implement a dedicated class to provide real values
            for(int i=0;i<31;i++) {
                Random r = new Random();
                if(r.nextInt(10)>6)
                {
                    items.add(Integer.toString(i));
                }
            }

            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
    };
}
