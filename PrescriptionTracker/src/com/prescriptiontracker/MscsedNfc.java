package com.prescriptiontracker;



import java.io.IOException;
import java.nio.charset.Charset;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;

import com.prescriptiontracker.prescriptionendpoint.Prescriptionendpoint;

import com.prescriptiontracker.prescriptionendpoint.model.Prescription;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;

import android.util.Log;
//import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.Toast;


public class MscsedNfc extends Activity {

	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private String prescription="";
	private boolean mWriteMode =false;
	IntentFilter[] mReadTagFilters;
    IntentFilter[] mWriteTagFilters;
    private static final String TAG = "MSCSED NFC";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set content view
		setContentView(R.layout.activity_mscsed_nfc);
		
		//prescription text
		 final EditText PrescriptionEditText = (EditText)this.findViewById(R.id.PrescriptionEditText);
		 
		 //patientName text
		 final EditText patientNameText  = (EditText)this.findViewById(R.id.PatientNameText);
		 
		//patientEmail text
		 final EditText patientEmailText  = (EditText)this.findViewById(R.id.PatientEmailText);
		 
		 
		 //DoctorName text
		 final EditText doctorNameText  = (EditText)this.findViewById(R.id.DoctorNameText);
		
		//Date To text
		 final EditText perDayText  = (EditText)this.findViewById(R.id.PerDayText);
		  
		
		//buttons
		 Button writePrescriptionButton = (Button)this.findViewById(R.id.writePrescriptionButton);
		 
		 final Button dateFromButton = (Button)this.findViewById(R.id.btnPickFromDate);
		 
		 final Button dateToButton = (Button)this.findViewById(R.id.btnPickToDate);
		 
			 
		 final Calendar presdateFrom=Calendar.getInstance();
		 
		 final Calendar presdateTo=Calendar.getInstance();
		 
		
	
		
		 
		 UUID uuid =  UUID.randomUUID();
		 
		 
		 
		 final String prescriptionId =uuid.toString();
		
		 class DatePickerFragmentTo extends DialogFragment
		 implements DatePickerDialog.OnDateSetListener  {
		 	  private int day;
		 	    private int month;
		 	    private int year;
		
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		 	// Use the current date as the default date in the picker
		 	final Calendar c = Calendar.getInstance();
		 	int year = c.get(Calendar.YEAR);
		 	int month = c.get(Calendar.MONTH);
		 	int day = c.get(Calendar.DAY_OF_MONTH);
		 	presdateTo.set(year,month,day);
		 	// Create a new instance of DatePickerDialog and return it
		 	return new DatePickerDialog(getActivity(), this, year, month, day);
		 	}
		 	
		 
			public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
		 
		 	day=selectedDay;
		   month=selectedMonth;
		   year=selectedYear;
		 	
		   dateToButton.setText(day + " / " + (month + 1) + " / " + year);
		   
		  
		   
		   presdateTo.set(year,month,day);
		   
		  
		 			
		 	}
		 	
		 }
		 
		 class DatePickerFragmentFrom extends DialogFragment
		 implements DatePickerDialog.OnDateSetListener {
		 	  private int day;
		 	    private int month;
		 	    private int year;
		
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		 	// Use the current date as the default date in the picker
		 	final Calendar c = Calendar.getInstance();
		 	int year = c.get(Calendar.YEAR);
		 	int month = c.get(Calendar.MONTH);
		 	int day = c.get(Calendar.DAY_OF_MONTH);
		 	
		 	presdateFrom.set(year,month,day);
		 	
		 	// Create a new instance of DatePickerDialog and return it
		 	return new DatePickerDialog(getActivity(), this, year, month, day);
		 	}
		 	
		 
			public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
		 	
		 		day=selectedDay;
		 	    month=selectedMonth;
		 	    year=selectedYear;
		 		
		 	   presdateFrom.set(year,month,day);
		 	    
		 	   dateFromButton.setText(day + " / " + (month + 1) + " / " + year);
		 	}
		 	
		 }
		  
		  
		 dateFromButton.setOnClickListener(new android.view.View.OnClickListener() 
	        { public void onClick(View view) {
	        	
	        	        	
	        		  DatePickerFragmentFrom newFragment = new DatePickerFragmentFrom();
	        		    newFragment.show(getFragmentManager(), "datePicker");
	        		  
	        	
	        		        }
	        });
		 
		 
		 
		 dateToButton.setOnClickListener(new android.view.View.OnClickListener() 
	        { public void onClick(View view) {
	        	
	        	        	
	        		  DatePickerFragmentTo newFragment = new DatePickerFragmentTo();
	        		    newFragment.show(getFragmentManager(), "datePicker");
	        		  
	        	
	        		        }
	        });
		  
		
		 
		 writePrescriptionButton.setOnClickListener(new android.view.View.OnClickListener() 
	        {
			 public void onClick(View view) {
				
				String pharmacyId="MyTestPharmacy@phar.ie";
				String presText=PrescriptionEditText.toString();
				Integer perDay=Integer.parseInt(perDayText.getText().toString());
				Calendar dateFrom=presdateFrom;
			    Calendar dateTo=presdateTo;
			    String doctorName=doctorNameText.toString();
				String patientId=patientEmailText.toString();
				String patientName=patientNameText.toString(); 	
					
				
				
		/*		 createPrescription(prescriptionId, 
						 pharmacyId,
						 presText,
  						dateFrom,
  						dateTo,
  						perDay,
  						doctorName,
  		       		patientId, 
  						patientName	);*/			 
				 
				 //set write mode to TRUE
				 mWriteMode=true;
				 
				
			 } 	
	        	
	        	
	        });
		 
		 
			mNfcAdapter = NfcAdapter.getDefaultAdapter(this); 
	    	mPendingIntent = PendingIntent.getActivity(this, 0,
	                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	   	 	IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
	   	 	
	   	 // Create intent filter to handle NDEF NFC tags detected from inside our
	   	    // application when in "read mode":
	   	    IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
	   	 	
	   	 	try
	     {
	   		
	         ndefDetected.addDataType("application/mscsed.nfc");
	     } catch (MalformedMimeTypeException e)
	     {
	         throw new RuntimeException("Could not add MIME type.", e);
	     }
	   	 	
	   	 	mFilters = new IntentFilter[] {
	             ndef,
	   	 	};
	   	 	
	   	 IntentFilter tagDetected = new IntentFilter(
	                NfcAdapter.ACTION_TAG_DISCOVERED);
	   	 	
	   	 mWriteTagFilters = new IntentFilter[] { tagDetected };
	        mReadTagFilters = new IntentFilter[] { ndefDetected, tagDetected };
	          mTechLists = new String[][] { new String[] { Ndef.class.getName() },
	       		   new String[] { NdefFormatable.class.getName() }};
	    }
	       
	
   
	 @Override
	    public void onResume() {
	        super.onResume();
	      
	        if (mNfcAdapter != null) mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mReadTagFilters,
                    mTechLists);
        
	        Log.d(TAG, "onResume: " + getIntent());
	        Toast.makeText(this,
                    "Intent Action"+getIntent().getAction()+"  Intent: "+getIntent(),
                    Toast.LENGTH_LONG).show();
	       
	       
	        
	        
	        if (getIntent().getAction() != null)
	        {
	            // tag received when app is not running and not in the foreground:
	            if (getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
	            {
	            	 processIntent(getIntent());
	            }
	        }

	        // Enable priority for current activity to detect scanned tags
	        // enableForegroundDispatch( activity, pendingIntent,
	        // intentsFiltersArray, techListsArray );*/
	     
	          
	 }  

	
	 
	 @Override
	    public void onNewIntent(Intent intent) {
	     //   Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		 if (!mWriteMode)
	        {
			 setIntent(intent);
	            	
	        } else{  
	        	Toast.makeText(this,
	                     "Write Tag",
	                     Toast.LENGTH_LONG).show();
	        	
	        	Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);  
	        	
	        		        	
	        	String payload=prescription;
	        	
	        	NdefRecord mimeRecord = NdefRecord.createMime("application/mscsed.nfc",
	        			payload.getBytes(Charset.forName("UTF-8")));
	        	
	       
	        	
	        	NdefMessage newMessage = new NdefMessage(new NdefRecord[] { mimeRecord,NdefRecord.createApplicationRecord("com.prescriptiontracker")});
	        	
	        	writeNdefMessageToTag(newMessage, tag);   
	        }
	 }     
	        
	 
	 
	 boolean writeNdefMessageToTag(NdefMessage message, Tag detectedTag) {
		//set write mode back to false
		 mWriteMode=false;   
		 int size = message.toByteArray().length;
	            try {
	                Ndef ndef = Ndef.get(detectedTag);
	                if (ndef != null) {
	                    ndef.connect();

	                    if (!ndef.isWritable()) {
	                    	Toast.makeText(this, "Tag is read-only.", Toast.LENGTH_SHORT).show();
	                        return false;
	                    }
	                    if (ndef.getMaxSize() < size) {
	                    	Toast.makeText(this, "The data cannot written to tag, Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size + " bytes.", Toast.LENGTH_SHORT).show();
	                        return false;
	                    }

	                    ndef.writeNdefMessage(message);
	                    ndef.close();                
	                    Toast.makeText(this, "Message is written NDEF tag.", Toast.LENGTH_SHORT).show();
	                    return true;
	                    
	                } else {
	                    NdefFormatable ndefFormat = NdefFormatable.get(detectedTag);
	                    if (ndefFormat != null) {
	                        try {
	                        	ndefFormat.connect();
	                        	ndefFormat.format(message);
	                        	ndefFormat.close();
	                            Toast.makeText(this, "The data is written to the tag ", Toast.LENGTH_SHORT).show();
	                            return true;
	                        } catch (IOException e) {
	                        	 Toast.makeText(this, "Failed to format tag", Toast.LENGTH_SHORT).show();
	                            return false;
	                        }
	                    } else {
	                    	 Toast.makeText(this, "NDEF is not supported", Toast.LENGTH_SHORT).show();
	                        return false;
	                    }
	                }
	            } catch (Exception e) {
	            	Toast.makeText(this, "Write opreation is failed", Toast.LENGTH_SHORT).show();
	            }
	            return false;
	            
	        }

	        
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mscsed_nfc, menu);
		return true;
	}
	
	 /*
     * **** HELPER METHODS ****
     */
	 private void setTextFieldValues(String tagString)
	    {

		 EditText PrescriptionEditText = (EditText)this.findViewById(R.id.PrescriptionEditText);
		 PrescriptionEditText.clearComposingText();
		 PrescriptionEditText.append("Data Read: "+tagString);
		  Toast.makeText(this, "Tag Data"+tagString, Toast.LENGTH_SHORT).show();

	    }
	  NdefMessage[] getNdefMessagesFromIntent(Intent intent)
	    {
	        // Parse the intent
	        NdefMessage[] msgs = null;
	        String action = intent.getAction();
	        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)
	                || action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
	        {
	            Parcelable[] rawMsgs = intent
	                    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	            if (rawMsgs != null)
	            {
	                msgs = new NdefMessage[rawMsgs.length];
	                for (int i = 0; i < rawMsgs.length; i++)
	                {
	                    msgs[i] = (NdefMessage) rawMsgs[i];
	                }
	            } else
	            {
	                // Unknown tag type
	                byte[] empty = new byte[] {};
	                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
	                        empty, empty, empty);
	                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
	                msgs = new NdefMessage[] { msg };
	            }
	        } else
	        {
	         
	            finish();
	        }
	        return msgs;
	    }
	  
	  /**
	     * Parses the NDEF Message from the intent and prints to the TextView
	     */
	    void processIntent(Intent intent) {
	    	
	        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
	                NfcAdapter.EXTRA_NDEF_MESSAGES);
	        
	        NdefMessage msg = (NdefMessage) rawMsgs[0];
	       
	        readTag(msg);
	    
	    }

	  private void readTag(final NdefMessage msg)
	    {
	        
	                        String payload = new String(msg.getRecords()[0].getPayload());
	                        setTextFieldValues(payload);
	                        Toast.makeText(this, "Tag Data"+payload, Toast.LENGTH_SHORT).show();
	    }
	  
	  @Override
	    public void onPause() {
	        super.onPause();
	       // mNfcAdapter.disableForegroundDispatch(this);
	    }
	  
	 
	  
	  public static java.util.Date getDateFromDatePicket(DatePicker datePicker){
		    int day = datePicker.getDayOfMonth();
		    int month = datePicker.getMonth();
		    int year =  datePicker.getYear();

		    Calendar calendar = Calendar.getInstance();
		    calendar.set(year, month, day);

		    return calendar.getTime();
		}
	  
	
	  
	
	 
	  
	 /* private class CreatePrescriptionThread extends AsyncTask<com.prescriptiontracker.prescriptionendpoint.model.Prescription, Void, String> {
		  com.prescriptiontracker.prescriptionendpoint.model.Prescription prescription;
	  	  
				@Override
				protected String doInBackground(com.prescriptiontracker.prescriptionendpoint.model.Prescription... params) {
					
					prescription=params[0];
					
					 com.prescriptiontracker.prescriptionendpoint.Prescriptionendpoint.Builder endpointBuilder = new Prescriptionendpoint.Builder(
							 AndroidHttp.newCompatibleTransport(),
							 new JacksonFactory(),
							 new HttpRequestInitializer() {
							 public void initialize(HttpRequest httpRequest) { }
							 });
							            
					 Prescriptionendpoint endpoint = CloudEndpointUtils.updateBuilder(
							 endpointBuilder).build();
							             try {
							            	 							            	// patient.setPatientDob(bdDate);
							 endpoint.insertPrescription(prescription).execute();
							 
							 prescription= endpoint.getPrescription(prescription.getPrescriptionId()).execute();
				            	if (prescription!=null){
				            		
				                 	Context context = getApplicationContext();
								       Toast.makeText(context,
								                         "Prescription added successfully",
								                         Toast.LENGTH_LONG).show();
				            	}
					
							            } catch (IOException e) {
							            	Context context = getApplicationContext();
							       Toast.makeText(context,
							                         "Error inserting new patient",
							                         Toast.LENGTH_LONG).show();
							       
							       throw new RuntimeException("Could not patient", e);
							 }
					return prescription.getPrescriptionId();
				}
				
				protected void onPostExecute(String patientId){
					
					
					
				}
				
	  }*/
				
	  
	  private class CreatePrescriptionThread extends AsyncTask<com.prescriptiontracker.prescriptionendpoint.model.Prescription, Void, String> {
		  com.prescriptiontracker.prescriptionendpoint.model.Prescription prescription;
	  	  
				@Override
				protected String doInBackground(com.prescriptiontracker.prescriptionendpoint.model.Prescription... params) {
					
					prescription=params[0];
					
					 com.prescriptiontracker.prescriptionendpoint.Prescriptionendpoint.Builder endpointBuilder = new Prescriptionendpoint.Builder(
							 AndroidHttp.newCompatibleTransport(),
							 new JacksonFactory(),
							 new HttpRequestInitializer() {
							 public void initialize(HttpRequest httpRequest) { }
							 });
							            
					 Prescriptionendpoint endpoint = CloudEndpointUtils.updateBuilder(
							 endpointBuilder).build();
							             try {
							            	 							            	// patient.setPatientDob(bdDate);
							 endpoint.insertPrescription(prescription).execute();
							 
							 prescription= endpoint.getPrescription(prescription.getPrescriptionId()).execute();
				            	if (prescription!=null){
				            		
				                 	Context context = getApplicationContext();
								       Toast.makeText(context,
								                         "Prescription added successfully",
								                         Toast.LENGTH_LONG).show();
				            	}
					
							            } catch (IOException e) {
							            	Context context = getApplicationContext();
							       Toast.makeText(context,
							                         "Error inserting new patient",
							                         Toast.LENGTH_LONG).show();
							       
							       throw new RuntimeException("Could not patient", e);
							 }
					return prescription.getPrescriptionId();
				}
				
				protected void onPostExecute(String patientId){
					
					
					
				}
				
	  }
	  
	  
				 private class GetPrescriptionThread extends AsyncTask<String, Void, Prescription> {
				  	  
					 Prescription prescription; 
				  	
					 
						@Override
						protected Prescription doInBackground(String... params) {
							
							 com.prescriptiontracker.prescriptionendpoint.Prescriptionendpoint.Builder endpointBuilder = new Prescriptionendpoint.Builder(
									 AndroidHttp.newCompatibleTransport(),
									 new JacksonFactory(),
									 new HttpRequestInitializer() {
									 public void initialize(HttpRequest httpRequest) { }
									 });
									            
							 Prescriptionendpoint endpoint = CloudEndpointUtils.updateBuilder(
									 endpointBuilder).build();
									             try {
									            	 
									            	 prescription= endpoint.getPrescription(params[0].toString()).execute();
									      								 
									            } catch (IOException e) {
									            	Context context = getApplicationContext();
									       Toast.makeText(context,
									                         "Error g prescription",
									                         Toast.LENGTH_LONG).show();
									       
									       throw new RuntimeException("Error retrieving prescription", e);
									 }
					         	
								            		
					         		return prescription;
												
									          
							
						}
						
						protected void onPostExecute(String patientId){
							
						/*TextView messageText = (TextView)findViewById(R.id.PrescriptionEditText);
							
							if (patientId !=null){
							//get prescription field
				        	
				        	
				        	//set text to prescription
				        	messageText.setText(patientId); */
							
						}		     
				 }
						
				 
		  
	  
	  public void createPrescription(String  prescriptionId, 
			  						 String pharmacyId,
			  						 String prescriptionName,
			  						Calendar dateFrom,
			  						 Calendar dateTo,
			  						Integer perDay,
			  						 String doctorName,
			  						 String patientId, 
			  						 String patientName) {
		  
		  com.prescriptiontracker.prescriptionendpoint.model.Prescription prescription=new com.prescriptiontracker.prescriptionendpoint.model.Prescription();
		 
		  Date pfrom=dateFrom.getTime();
		  Date pto=dateTo.getTime();
		  com.google.api.client.util.DateTime prescriptionDateFrom=new com.google.api.client.util.DateTime(pfrom);
		  com.google.api.client.util.DateTime prescriptionDateTo=new com.google.api.client.util.DateTime(pto);
		  
		  		
		  
		  prescription.setPrescriptionId(prescriptionId);
		  prescription.setPharmacyId(pharmacyId);
		  prescription.setPrescriptionName(prescriptionName);
		  prescription.setToDateTo(prescriptionDateTo);	
		  prescription.setDateFrom(prescriptionDateFrom);	
		  prescription.setTakePerDay(perDay);
		  prescription.setPrescriptionDoctorName(doctorName);
		  prescription.setPatientId(patientId);
		  prescription.setPatientName(patientName);
		  
		 CreatePrescriptionThread prescriptionThread=new CreatePrescriptionThread();
		prescriptionThread.execute(prescription);

		  }
	  
	  public void getPrescription(String prescriptionId){
		  
		  GetPrescriptionThread prescription = new GetPrescriptionThread();
		  
		  prescription.execute(prescriptionId);
	  }

	 
		
	  
}


