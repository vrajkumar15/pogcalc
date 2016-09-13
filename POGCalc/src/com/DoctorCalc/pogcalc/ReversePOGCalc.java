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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

public class ReversePOGCalc extends Activity {
	
	public Calendar month;
	public CalendarAdapter adapter;
	public Handler handler;
	public ArrayList<String> items; // container to store some random calendar items
	TextView tvPOG;
	TextView tvTopPOG;
	TextView tvSelect;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reverse_pogcalc);
	    month = Calendar.getInstance();
		tvSelect = (TextView) findViewById(R.id.tvSelect);
		tvPOG = (TextView) findViewById(R.id.tvPOG);
		tvTopPOG = (TextView) findViewById(R.id.tvTopPOG);

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
		        	String dt = day+"-"+ android.text.format.DateFormat.format("MM-yyyy", month);
		          	dateValidate(dt);

		        }
		        
		    }



		});
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
       
       long diffInMilis = milliSecD1 - milliSecD2;
       long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);
       if (diffInMilis < 0) {
    	   //		tvHola.setVisibility(View.INVISIBLE);
    	   tvPOG.setVisibility(View.INVISIBLE);
    	   tvTopPOG.setVisibility(View.INVISIBLE);
    	   tvSelect.setVisibility(View.VISIBLE);
    	   tvSelect.setText("Date clicked should be later than today..Please select your EDD date..");
    	  // Toast.makeText(getApplicationContext(), "Date clicked is not yet arrived..Please select your LMP date.." , Toast.LENGTH_SHORT).show();
       } else if (diffInDays > 280) {
    	   tvPOG.setVisibility(View.INVISIBLE);
    	   tvTopPOG.setVisibility(View.INVISIBLE);
    	   tvSelect.setVisibility(View.VISIBLE);
    	   tvSelect.setText("Please enter a date less than 10 months");
    	  // Toast.makeText(getApplicationContext(), "Please enter a date less than 10 months" , Toast.LENGTH_SHORT).show();
       } else {
    	   tvTopPOG.setVisibility(View.VISIBLE);
    	   tvPOG.setVisibility(View.VISIBLE);
    	   tvSelect.setVisibility(View.VISIBLE);
    	   tvSelect.setText("Selected EDD Date : "+ dateEntered);
       	calcPOG(dateEntered);
       }
       
	}
	
	public void calcPOG(String dateEntered) {
		int diffDay = 0;
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c1 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(dateEntered));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        //EDD
       c1.add(Calendar.DATE, -280);
        
       long milliSecD1 = c1.getTimeInMillis();
        
       //Current date
       Calendar c2 = Calendar.getInstance();
       c2.setTime(today);
       long milliSecD2 = c2.getTimeInMillis();
       
      
       long diffInMilisTdy = milliSecD2 - milliSecD1;  
       
//       long totalPeriod = 24192000000;
//       //EDD - 280 Days (LMP)
//       long conc = milliSecD1 - totalPeriod;
//       
//       //Current date - LMP
//       long pog =  milliSecD2 - conc;
  //     long diffInMilis = totalPeriod - diffInMilisTdy;
   //    long pogInMilis = diffInMilisTdy  - conc;
       long diffInDays = diffInMilisTdy / (24 * 60 * 60 * 1000);
       int weeks = (int) (diffInDays/7);
       int days = (int) (diffInDays % 7);
       tvPOG.setText(weeks + " weeks " + days + " days");
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
