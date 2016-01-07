package com.prescriptiontracker;
/**
 * @author Patrick Fitzgerald MScSED Minor Thesis 2013
 * 
 */


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.api.client.util.DateTime;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.ListActivity;


public class PrescriptionTakenList extends ListActivity implements OnItemClickListener {
	// define a list of converters that the list will display
    static List<DateTime> datesTaken=new ArrayList<DateTime>();
   
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
    
    SimpleDateFormat dateShowFormat = new SimpleDateFormat("EEE, d MMM yyyy");
    SimpleDateFormat dateDetailFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    List<Date> dmyDate= new ArrayList<Date> ();
    List<String> dmyDateStr= new ArrayList<String>();
    List<String> dateShowFinal=new ArrayList<String>();
    List<String> dateShow=new ArrayList<String>();
    Integer takePerDay;
    Integer dateCount=0;
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	       // setContentView(R.layout.prescription_taken_list);
	        Context context = getApplicationContext(); 
	       // Bundle b = getIntent().getExtras();
	        datesTaken=(List<DateTime>) getIntent().getSerializableExtra("prescriptionDatesTaken"); 
	        takePerDay=(Integer)getIntent().getSerializableExtra("takePerDay"); 
	 if   (datesTaken.size()>0)
	 {     
	        dateShow.clear();
	        for (DateTime d:datesTaken){
	        	
	        	    	
	        	dmyDateStr.add(d.toString());
	        	
	        	
			      	
	        }
	       
	      
	      
	        for (String s:dmyDateStr){
	        	try {
					dmyDate.add(dateFormat.parse(s));
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }
	     
	        for (Date d:dmyDate){
	        	d.setHours(d.getHours()+1); //hours hack
	        
	        	if (!dateShow.contains(dateShowFormat.format(d))){
	        	dateShow.add(dateShowFormat.format(d));
	        	}
	       
	   
	        }
	    	//dateCount=dmyDate.size();
        	
		       for(int i=0;i<dateShow.size();i++){
		    	   
		    	   String selectedDate=dateShow.get(i);
		   		
		  		 for (Date d:dmyDate){
		  	        	if (selectedDate.equals(dateShowFormat.format(d))){
		  	        		
		  	        		dateCount++;
		  	        	}
		  	        	
		  		 }
		    	   
		    	   dateShowFinal.add(i, dateShow.get(i)+"\nYou have taken this prescription "+dateCount + " times for this date."+ "\nDaily Prescription is for " +takePerDay+"\n*\nPlease press for the times the prescription was taken.");
		    	   dateCount=0;
		       }
	          
            //create list items from CONVERTERS array
	        setListAdapter(new ArrayAdapter<String>(this, R.layout.main, dateShow));

	        //ListView gets the data to display via setListAdapter
	        ListView myList = getListView();

	        //You may turn on text filtering using the setTextFilterEnabled (true) method (not mandatory)
	        //When the user begins typing, the list will be filtered
	        myList.setTextFilterEnabled(true);
	        
	        // add onClick listener to the list
	        myList.setOnItemClickListener(this);
	 
	 	}
	 
	 }
	 
	 @Override
	    public void onResume() {
	        super.onResume();
	        if   (datesTaken.size()>0)
	   	 {     
	        //create list items from CONVERTERS array
	        setListAdapter(new ArrayAdapter<String>(this, R.layout.main, dateShowFinal));

	        //ListView gets the data to display via setListAdapter
	        ListView myList = getListView();

	        //You may turn on text filtering using the setTextFilterEnabled (true) method (not mandatory)
	        //When the user begins typing, the list will be filtered
	        myList.setTextFilterEnabled(true);
	        
	        // add onClick listener to the list
	        myList.setOnItemClickListener(this);
	   	 } 
	          
	 }  
	 
	


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		List<String> dateDetail=new ArrayList<String>();
		 Context context = getApplicationContext(); 
		 String selectedDate=dateShow.get(position);
		
		 for (Date d:dmyDate){
	        	if (selectedDate.equals(dateShowFormat.format(d))){
	        		
	        		dateDetail.add(dateDetailFormat.format(d));
	        	}
	        	
		 }
		 
		
		 
		 Intent intent = new Intent(context, PrescriptionTakenListDetail.class);
			intent.putExtra("prescriptionDatesTakenDetail",(ArrayList<String>)dateDetail);
		startActivity(intent);
		
	}
	
	  /*public void onClickHandler(View view) {
	    	if(view.getId()==R.id.cancelButton){
				AlertDialog.Builder newAlert = new AlertDialog.Builder(this);
				newAlert.setTitle("Returning to Main Menu");
				newAlert.setMessage("Are you sure?");
				newAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int which) {
					   PrescriptionTakenList.this.finish();
				   }
				});
				newAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog, int which) {
						  dialog.cancel();
					   }
					});
				newAlert.show();
	
}
	    	
	  }*/
	  
}
