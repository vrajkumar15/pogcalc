/*
* Copyright 2011 Lauri Nevala.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.DoctorCalc.pogcalc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class CalendarView extends Activity {

	public Calendar month;
	public CalendarAdapter adapter;
	public Handler handler;
	public ArrayList<String> items; // container to store some random calendar items
	TextView tvConc;
	TextView tvPOG;
	TextView tvEDD;
	TextView tvTopConc;
	TextView tvTopPOG;
	TextView tvTopEDD;
	TextView tvSelect;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar);
	    
	    month = Calendar.getInstance();
		tvSelect = (TextView) findViewById(R.id.tvSelect);
		tvEDD = (TextView) findViewById(R.id.tvEDD);
		tvPOG = (TextView) findViewById(R.id.tvPOG);
		tvConc = (TextView) findViewById(R.id.tvConc);
		tvTopEDD = (TextView) findViewById(R.id.tvTopEDD);
		tvTopPOG = (TextView) findViewById(R.id.tvTopPOG);
		tvTopConc = (TextView) findViewById(R.id.tvTopConc);
	    
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
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.pogcalc, menu);
	    return super.onCreateOptionsMenu(menu);
	} 	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		infoDialog();
    	return true;
	}
	
	public void onRPOG(View v){
	    Intent i = new Intent(this, ReversePOGCalc.class);
		startActivity(i);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
		
	}

	public void onRPOGUSG(View v){
		Intent ir = new Intent(this, ReversePOGCalcUsg.class);
		startActivity(ir);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);

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
    	   tvEDD.setVisibility(View.INVISIBLE);
    	   tvPOG.setVisibility(View.INVISIBLE);
    	   tvConc.setVisibility(View.INVISIBLE);
    	   tvTopEDD.setVisibility(View.INVISIBLE);
    	   tvTopPOG.setVisibility(View.INVISIBLE);
    	   tvTopConc.setVisibility(View.INVISIBLE);
    	   tvSelect.setVisibility(View.VISIBLE);
    	   tvSelect.setText("Date clicked has not arrived yet..Please select your LMP date..");
    	  // Toast.makeText(getApplicationContext(), "Date clicked is not yet arrived..Please select your LMP date.." , Toast.LENGTH_SHORT).show();
       } else if (diffInDays > 300) {
    	   tvEDD.setVisibility(View.INVISIBLE);
    	   tvPOG.setVisibility(View.INVISIBLE);
    	   tvConc.setVisibility(View.INVISIBLE);
    	   tvTopEDD.setVisibility(View.INVISIBLE);
    	   tvTopPOG.setVisibility(View.INVISIBLE);
    	   tvTopConc.setVisibility(View.INVISIBLE);
    	   tvSelect.setVisibility(View.VISIBLE);
    	   tvSelect.setText("Please enter a date less than 10 months");
    	  // Toast.makeText(getApplicationContext(), "Please enter a date less than 10 months" , Toast.LENGTH_SHORT).show();
       } else {
    	   tvTopEDD.setVisibility(View.VISIBLE);
    	   tvTopPOG.setVisibility(View.VISIBLE);
    	   tvTopConc.setVisibility(View.VISIBLE);
    	   tvEDD.setVisibility(View.VISIBLE);
    	   tvPOG.setVisibility(View.VISIBLE);
    	   tvConc.setVisibility(View.VISIBLE);
    	   tvSelect.setVisibility(View.VISIBLE);
    	   tvSelect.setText("Selected LMP Date : "+ dateEntered);
       	calcConc(dateEntered);
       	calcEDD(dateEntered);
       	calcCurr(dateEntered);
       }
       
	}
	
	public void calcEDD(String dateEntered) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dateEntered));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //c.set(Calendar.MONTH, (c.get(Calendar.MONTH)+9));
        c.add(Calendar.MONTH, 9);
        c.add(Calendar.DATE, 7);
       // c.add(Calendar.DATE, 280);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        String output = sdf1.format(c.getTime()); 
       // Toast.makeText(getApplicationContext(), output , Toast.LENGTH_SHORT).show();
        tvEDD.setText(output);
	}

	public void calcConc(String dateEntered) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dateEntered));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 14);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        String output = sdf1.format(c.getTime()); 
        tvConc.setText(output);
	}
	
	public void calcCurr(String dateEntered) {
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
       int weeks = (int) (diffInDays/7);
       int days = (int) (diffInDays % 7);
       tvPOG.setText(weeks + " weeks " + days + " days");
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
