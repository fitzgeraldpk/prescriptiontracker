package com.prescriptiontracker;
/**
 * @author Patrick Fitzgerald MScSED Minor Thesis 2013
 * 
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;

import com.prescriptionwriter.prescriptionendpoint.Prescriptionendpoint;
import com.prescriptionwriter.prescriptionendpoint.Prescriptionendpoint.Builder;
import com.prescriptionwriter.prescriptionendpoint.model.Prescription;

public class PrescriptionList extends ListActivity implements OnItemClickListener {
	// define a list of converters that the list will display
    static List<String> prescriptionNames=new ArrayList<String>();
    static List<String> prescriptionIds=new ArrayList<String>();
    static Integer takePerDay=null;
    List<DateTime> datesTaken=new ArrayList<DateTime>();
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	     
	       
	  
	        prescriptionNames=(List<String>) getIntent().getSerializableExtra("prescriptionNames"); 
	        prescriptionIds=(List<String>) getIntent().getSerializableExtra("prescriptionIds"); 
	    
	       
            //create list items from CONVERTERS array
	        setListAdapter(new ArrayAdapter<String>(this, R.layout.main, prescriptionNames));

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
	        setListAdapter(new ArrayAdapter<String>(this, R.layout.main, prescriptionNames));

	        //ListView gets the data to display via setListAdapter
	        ListView myList = getListView();

	        //You may turn on text filtering using the setTextFilterEnabled (true) method (not mandatory)
	        //When the user begins typing, the list will be filtered
	        myList.setTextFilterEnabled(true);
	        
	        // add onClick listener to the list
	        myList.setOnItemClickListener(this);
	     
	          
	 }  
	 
	 // when a list item is selected, this method is called
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {	
		
			if (isUserConnected()){
				getPrescriptionDatesTaken(prescriptionIds.get(position));
				
			 
				}else{
					Context context = getApplicationContext();
			        Toast.makeText(context, "There is no network access (mobile data or wifi access)", Toast.LENGTH_SHORT).show();
				}
			
			
		}


	
	 private class GetPrescriptionDatesTakenThread extends AsyncTask<String, Void, List<DateTime>> {
	  	  
			Prescription prescription=new Prescription(); 
		  	 
			 ProgressDialog progDialog;			  	 
				
			  protected void onPreExecute() {
		            super.onPreExecute();
		            progDialog = new ProgressDialog(PrescriptionList.this);
		            progDialog.setMessage("Loading...");
		            progDialog.setIndeterminate(false);
		            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		            progDialog.setCancelable(true);
		            progDialog.show();
		        }	
			
			
			
			@Override
				protected List<DateTime> doInBackground(String... params) {
					
					//List<DateTime> datesTaken=new ArrayList<DateTime>();
					
					 Builder endpointBuilder = new Prescriptionendpoint.Builder(
							 AndroidHttp.newCompatibleTransport(),
							 new JacksonFactory(),
							 new HttpRequestInitializer() {
							 public void initialize(HttpRequest httpRequest) { }
							 });
							            
					 Prescriptionendpoint endpoint = CloudEndpointUtils.updateBuilder(
							 endpointBuilder).build();
							             try {
							            	 
							            	 prescription= endpoint.getPrescription(params[0].toString()).execute();
							      								 
							            	 datesTaken= prescription.getDatesTaken();
							            	 takePerDay=prescription.getTakePerDay();
							            	 
							             } catch (IOException e) {
							            									       
							       throw new RuntimeException("Error retrieving prescription", e);
							 }
			         	
						return datesTaken;
			         	
			         		
				}
										
			         		
				
				protected void onPostExecute(List<DateTime> datesTaken){
				
					 
					  
				if (datesTaken !=null){
					Context context = getApplicationContext();
					 Toast.makeText(context,
	                         "Prescription taken "+datesTaken.size() +" times.",
	                         Toast.LENGTH_LONG).show();
					 
						
					
				}else{
					
					Context context = getApplicationContext();
				       Toast.makeText(context,
				                         "Prescription has not yet been taken",
				                         Toast.LENGTH_LONG).show();
				}
					
				if(datesTaken !=null){
				Context context = getApplicationContext();
				 progDialog.dismiss();	
				 Intent intent = new Intent(context, PrescriptionTakenList.class);
  				intent.putExtra("prescriptionDatesTaken",(ArrayList<DateTime>)datesTaken);
  				intent.putExtra("takePerDay",takePerDay);
  				startActivity(intent);
				}	else{
					
					progDialog.dismiss();
				}
				}		    
		 }		 
	 
	  /**
	   * 
	   * @param prescriptionId
	   */
	  public void getPrescriptionDatesTaken (String prescriptionId){
		  
		  GetPrescriptionDatesTakenThread datesTaken = new GetPrescriptionDatesTakenThread();
		  
		  datesTaken.execute(prescriptionId);
		  
		
		  
	  }

	public boolean isUserConnected(){
			
			ConnectivityManager connMgr = (ConnectivityManager) 
			        getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
			boolean isWifiConn = networkInfo.isConnected();
			networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			boolean isMobileConn = networkInfo.isConnected();
			if (isMobileConn || isWifiConn){
				return true;
				
			}else{
				
				return false;
			}
		}
}
