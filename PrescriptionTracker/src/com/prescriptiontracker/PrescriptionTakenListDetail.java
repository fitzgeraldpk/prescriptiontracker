package com.prescriptiontracker;
/**
 * @author Patrick Fitzgerald MScSED Minor Thesis 2013
 * 
 */
import java.util.ArrayList;
import java.util.List;
import com.google.api.client.util.DateTime;
import com.prescriptiontracker.R;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.app.ListActivity;


public class PrescriptionTakenListDetail extends ListActivity implements OnItemClickListener {
	// define a list of converters that the list will display
    static List<DateTime> datesTaken=new ArrayList<DateTime>();
   
    List<String> dateShow=new ArrayList<String>();
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	       // setContentView(R.layout.prescription_taken_list);
	        Context context = getApplicationContext(); 
	       // Bundle b = getIntent().getExtras();
	        dateShow=(List<String>) getIntent().getSerializableExtra("prescriptionDatesTakenDetail"); 
	       
	        
	   
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
	 
	 @Override
	    public void onResume() {
	        super.onResume();
	      
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
	 
	


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		
	}
	
	
	  
}

