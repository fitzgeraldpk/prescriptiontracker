package com.prescriptiontracker;
/**
 * @author Patrick Fitzgerald MScSED Minor Thesis 2013
 * 
 */

import android.app.Activity;
import android.app.ProgressDialog;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.prescriptiontracker.CloudEndpointUtils;
import com.prescriptionwriter.prescriptionendpoint.Prescriptionendpoint;
import com.prescriptionwriter.prescriptionendpoint.model.CollectionResponsePrescription;
import com.prescriptionwriter.prescriptionendpoint.model.Prescription;
import com.prescriptionwriter.prescriptionendpoint.Prescriptionendpoint.Builder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

/**
 * 
 * @author Patrick
 *
 */
public class Prescriptiondetail extends Activity implements OnInitListener {

	
	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private String viewCurr;
   
	private boolean mWriteMode =false;
	IntentFilter[] mReadTagFilters;
    IntentFilter[] mWriteTagFilters;
    List<DateTime> datesTaken=new ArrayList<DateTime>();
    private static final String TAG = "MSCSED NFC";
    private String userEmail; 
    private static TextToSpeech mText2Speech;
	private static final int MY_DATA_CHECK_CODE = 1234;
	private String speechText;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set content view
		setContentView(R.layout.activity_read_tag);
		
		
		//buttons
		 Button readPrescriptionButton = (Button)this.findViewById(R.id.writePrescriptionButton);
		 
		 Button ListPrescriptionButton = (Button)this.findViewById(R.id.btnReadTag);
		 
		 Button signOutButton = (Button)this.findViewById(R.id.btnSignout);
		 		
		 viewCurr="MAIN";
		 
		// final EditText prescriptionIdText =  (EditText)this.findViewById(R.id.PrescriptionId);
		 
		// prescriptionIdText.setVisibility(View.GONE);
			 
		 userEmail=getIntent().getStringExtra("userEmail"); 

		
		 /**
		  * 
		  */
		 readPrescriptionButton.setOnClickListener(new android.view.View.OnClickListener() 
	        {
			 public void onClick(View view) {
			
					
				 mWriteMode=false;
				
				 Context context = getApplicationContext();
				 Toast.makeText(context,
	                     "Touch your mobile against the prescription tag",
	                     Toast.LENGTH_LONG).show();
				 
				 setContentView(R.layout.activity_mscsed_nfc); 
				 viewCurr="PRES";
			 } 	
	        	
	        	
	        });
	
		 
		 
		 
		 ListPrescriptionButton.setOnClickListener(new android.view.View.OnClickListener() 
	        {
			 public void onClick(View view) {
				 Context context = getApplicationContext();
				 //set write mode to FALSE
				 mWriteMode=false;
								
				if (isUserConnected()){
                 getUserPrescriptions(userEmail);
				
			 
				}else{
					 context = getApplicationContext();
			        Toast.makeText(context, "There is no network access (mobile data or wifi access)", Toast.LENGTH_SHORT).show();
				}
				} 	
	        	
	        	
	        });
		 
		 
		 signOutButton.setOnClickListener(new android.view.View.OnClickListener() 
	        {
			 public void onClick(View view) {
				Context context = getApplicationContext();
				Intent intent = new Intent(context, Prescriptiontracker.class);
				startActivity(intent);
				
	             
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
	   		
	         ndefDetected.addDataType("application/prestracker.nfc");
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
	/**
	 * 
	 * @param prescription
	 */
	public void setPrescriptionFields(Prescription prescription){
		
		 SimpleDateFormat dateDetailFormat = new SimpleDateFormat("EEE, d MMM yyyy"); 
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
		 Date dateFrm = null;
		 Date date2=null;
		TextView prescriptionEditText = (TextView)this.findViewById(R.id.PrescriptionEditText);
			
		 
		 //patientName text
		TextView patientNameText  = (TextView)this.findViewById(R.id.PatientNameText);
		 
		//patientEmail text
		TextView patientEmailText  = (TextView)this.findViewById(R.id.PatientEmailText);
		 
		 
		 //DoctorName text
		TextView doctorNameText  = (TextView)this.findViewById(R.id.DoctorNameText);
		
		//Date To text
		TextView perDayText  = (TextView)this.findViewById(R.id.PerDayText);
		
		String perDayTime=null;
		
		 
		TextView dateFrom = (TextView)this.findViewById(R.id.btnPickFromDate);
		 
		TextView dateTo = (TextView)this.findViewById(R.id.btnPickToDate);
		
		if (prescription !=null){
			 
			speechText="Prescription name is";
			if (prescription.getPrescriptionName()!=null){
			 prescriptionEditText.setText(prescription.getPrescriptionName());
			 
			 speechText=speechText+" "+prescription.getPrescriptionName()+". ";
			}
			
			if (prescription.getPatientName()!=null){
			 patientNameText.setText(prescription.getPatientName());
			}
			
			if (prescription.getPatientId()!=null){
				patientEmailText.setText(prescription.getPatientId());	
				
			}
			 
			if (prescription.getPrescriptionDoctorName()!=null){
			 doctorNameText.setText(prescription.getPrescriptionDoctorName());
			 
			 
			}
			
			if (prescription.getTakePerTime()!=null){
				perDayTime=prescription.getTakePerTime().toString();
				//speechText=speechText+"Take this prescription "+prescription.getTakePerDay().toString()+" per day. ";
				}
		
			
			if (prescription.getTakePerDay()!=null){
				perDayText.setText(perDayTime +" / "+prescription.getTakePerDay().toString());
				speechText=speechText+"Take "+perDayTime+" of this prescription" +prescription.getTakePerDay().toString()+" times per day. ";
				}
			
			
			
			if (prescription.getDateFrom()!=null){
				
				try {
					dateFrm=(dateFormat.parse(prescription.getDateFrom().toString()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				}
				
				dateFrom.setText(dateDetailFormat.format(dateFrm));
				
			}
			
			if (prescription.getDateTo()!=null){
				
				try {
					date2=(dateFormat.parse(prescription.getDateTo().toString()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
				dateTo.setText(dateDetailFormat.format(date2));
				
				speechText=speechText+"Take from "+dateDetailFormat.format(dateFrm)+" until "+dateDetailFormat.format(date2);
				
			}
			 
			Intent checkIntent = new Intent();
	        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
	        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);	 
		 
		}
		
	
		
	
		
	}
	       
	
   /**
    * 
    * */
	 @Override
	    public void onResume() {
	        super.onResume();
	      
	        if (mNfcAdapter != null) mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mReadTagFilters,
                    mTechLists);
        
	        Log.d(TAG, "onResume: " + getIntent());
	          
	        if (getIntent().getAction() != null)
	        {
	            // tag received when app is not running and not in the foreground:
	            if (getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
	            {
	            	 processIntent(getIntent());
	            }
	        }

	       
	     
	          
	 }  

	
	 /**
	  * 
	  */
	 @Override
	    public void onNewIntent(Intent intent) {
	     //   Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		 if (!mWriteMode)
	        {
			 setIntent(intent);
			 processIntent(intent);
			
	            	
	        } 
	 }     
	        
	 
	 /**
	  * 
	  * @param message
	  * @param detectedTag
	  * @return
	  */
	 

	 /**
	  *        
	  */
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mscsed_nfc, menu);
		return true;
	}*/
	
	 /*
     * **** HELPER METHODS ****
     */
	/**
	 * 
	 * @param tagString
	 */
	
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
	 private void processIntent(Intent intent) {
	    	
	        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
	                NfcAdapter.EXTRA_NDEF_MESSAGES);
	        
	        NdefMessage msg = (NdefMessage) rawMsgs[0];
	       
	        readTag(msg);
	    
	    }
	    
	    /**
	     * 
	     * @param msg
	     */
	  private void readTag(final NdefMessage msg)
	    {
	        
		  String payload = new String(msg.getRecords()[0].getPayload());
		  if (isUserConnected()){
			  getPrescription(payload); 	
		  }else{
			  Toast.makeText(this, "There is no network access (mobile data or wifi access)", Toast.LENGTH_SHORT).show();
			  
		  }
	                        	                      
	    }
	  
	  /**
	   * 
	   */
	  @Override
	    public void onPause() {
	        super.onPause();
	        mNfcAdapter.disableForegroundDispatch(this);
	    }
	 
	  /**
	   * NFC Tag swipe
	   * Add dateTime occurrence of prescription being recorded
	   * @author Patrick
	   *
	   */
	  
				 
	  
	  private class GetPrescriptionThread extends AsyncTask<String, Void, Prescription> {
		  	
		  Prescription prescription=new Prescription(); 
		  ProgressDialog progDialog;	
		  protected void onPreExecute() {
	            super.onPreExecute();
	            progDialog = new ProgressDialog(Prescriptiondetail.this);
	            progDialog.setMessage("Retrieving prescription data....");
	            progDialog.setIndeterminate(false);
	            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            progDialog.setCancelable(true);
	            progDialog.show();
	        }
		
		  @Override
		  protected Prescription doInBackground(String... params) {
							
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
											 
			  } catch (IOException e) {
									            									       
				  throw new RuntimeException("Error retrieving prescription", e);
									 }
					         	
								  
			  return prescription;
			  
		  }
												
					         		
						
		  protected void onPostExecute(Prescription prescription){
			  progDialog.dismiss();	
			  Context context = getApplicationContext();
			  if (prescription.getPatientId().equals(userEmail)){
			  
				  setPrescriptionFields(prescription);
						
			  }else{
				  
				  
				  
				  Toast.makeText(context,
						  "This prescription is not linked to your account. Please ensure that medication is prescribed to you.",
						  Toast.LENGTH_LONG).show();
				  
				  speechText="This prescription is not linked to your account. Please ensure that medication is prescribed to you.";
				  Intent checkIntent = new Intent();
			        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
				  
			  }
					
							
		  }		    
	  }
						
		  			/**
		  			 * List <DateTime> of prescription taken
		  			 * @author Patrick
		  			 *
		  			 */
		  
	  private class GetPrescriptionDatesTakenThread extends AsyncTask<String, Void, List<DateTime>> {
					  	  
		  Prescription prescription=new Prescription(); 
									
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
								
					Context context = getApplicationContext();
					Intent intent = new Intent(context, PrescriptionTakenList.class);
					intent.putExtra("prescriptionDatesTaken",(ArrayList<DateTime>)datesTaken);
					startActivity(intent);
								
		  }		    
	  }		 
		  
	
	  private class GetPrescriptionListThread extends AsyncTask<String, Void, CollectionResponsePrescription> {
					  	  
		  CollectionResponsePrescription prescriptionList=new CollectionResponsePrescription(); 
		  ProgressDialog progDialog;			  	 
		
		  protected void onPreExecute() {
	            super.onPreExecute();
	            progDialog = new ProgressDialog(Prescriptiondetail.this);
	            progDialog.setMessage("Loading...");
	            progDialog.setIndeterminate(false);
	            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            progDialog.setCancelable(true);
	            progDialog.show();
	        }
		  
		  
		  @Override
		  protected CollectionResponsePrescription doInBackground(String... params) {
									
			
									
			  Builder endpointBuilder = new Prescriptionendpoint.Builder(
					  AndroidHttp.newCompatibleTransport(),
					  new JacksonFactory(),
					  new HttpRequestInitializer() {
						  public void initialize(HttpRequest httpRequest) { }
					  });
											            
			  Prescriptionendpoint endpoint = CloudEndpointUtils.updateBuilder(
					  endpointBuilder).build();
			  try {
						   						            	
				  String query="select prescription from Prescription as prescription where prescription.patientId='"+params[0].toString()+"'";
																									            					            	
				  prescriptionList= endpoint.listPrescription().setQuery(query).execute();
														             
			  } catch (IOException e) {
											            									       
				  //throw new RuntimeException("Error retrieving prescription", e);
			  }
			  
			  return prescriptionList;
							         	
							         		
		  }
														
							         		
								
		  protected void onPostExecute(CollectionResponsePrescription prescriptionList){
								
			  Context context = getApplicationContext(); 
			  List<String> prescriptionNames=new ArrayList<String>();
			  List<String> prescriptionIds=  new ArrayList<String>();
			  List<Prescription> prescriptionObjList= new ArrayList<Prescription>();
			  String dateFrom="No Date From";
			  String dateTo="No Date To";
			
			  if (prescriptionList !=null){
							
				  for (Prescription prescription : prescriptionList.getItems()) {
					if (prescription.getDateFrom()!=null){
						dateFrom=prescription.getDateFrom().toString().substring(0,10);
					} 
					if (prescription.getDateTo()!=null){
						
						dateTo=prescription.getDateTo().toString().substring(0,10);
						
					}
						
					  prescriptionNames.add("Prescription Name: "+prescription.getPrescriptionName().toUpperCase()+"\n\n Take "+prescription.getTakePerTime()+" of this prescription. "+
							  prescription.getTakePerDay()+" times per Day: "
							  				+
							  				"\n Start Date: "+dateFrom
							  				+ "\n End Date: "+dateTo);
					  prescriptionIds.add(prescription.getPrescriptionId());
					 
					  prescriptionObjList.add(prescription);
								        
				  }
									
			  }else{
				  Toast.makeText(context,
						  "Unable to retrieve any prescriptions at this time.",
						  Toast.LENGTH_LONG).show();
			  }
									
			  progDialog.dismiss();	
			  Intent intent = new Intent(context, PrescriptionList.class);
			  intent.putExtra("prescriptionNames",(ArrayList<String>)prescriptionNames);
			  intent.putExtra("prescriptionIds",(ArrayList<String>)prescriptionIds);
			  intent.putExtra("prescriptionList",(ArrayList<Prescription>)prescriptionObjList);
			  startActivity(intent);
			  
		  }		    
	  }		 
			  
					 
					 
					 
					 
					 
	  /**
	   * 
	   * @param prescriptionId
	   */
	  public void getPrescription(String prescriptionId){
		 
		  GetPrescriptionThread prescription = new GetPrescriptionThread();
		  
		  	prescription.execute(prescriptionId);
		
	  }
	  /**
	   * 
	   * @param prescriptionId
	   */
	  public void getPrescriptionDatesTaken (String prescriptionId){
		  
		  GetPrescriptionDatesTakenThread datesTaken = new GetPrescriptionDatesTakenThread();
		  
		  datesTaken.execute(prescriptionId);
		  
		
		  
	  }
	 
	  /**
	   * Get back all prescription for a given user email address
	   */
	
	  public void getUserPrescriptions(String userEmail){
		  
		 GetPrescriptionListThread prescriptionList = new GetPrescriptionListThread();
		  prescriptionList.execute(userEmail);
		  
		  
		  
	  }

	  //disable back action on application
	  @Override
	  public void onBackPressed() {
		  
		  if (viewCurr.equals("PRES")){
			  setContentView(R.layout.activity_read_tag); 
			  	Context context = getApplicationContext();
			  	Intent intent = new Intent(context, Prescriptiondetail.class);
			  	intent.putExtra("userEmail",userEmail);
			  	startActivity(intent);
		  }else{
			  
			//
		  }
		  
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
	
	 @Override
	    public void onDestroy() {
	        // Don't forget to shutdown tts!
	        if (mText2Speech != null) {
	        	mText2Speech.stop();
	        	mText2Speech.shutdown();
	        }
	        super.onDestroy();
	    }
	
	 
	


@Override
public void onInit(int arg0) {
	 if(arg0 == TextToSpeech.SUCCESS) {
            mText2Speech.setLanguage(Locale.getDefault());
          ///  mText2Speech.speak("Hello folks, welcome to my little demo on Text To Speech.",
           //         TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
           //         null);
            
            
            
          
            	textToSpeech();
            	
            	
         
        }
	
}

/**
 * TextToSpeech (TTS) Section	
 * @param text
 */
	public void textToSpeech (){
		
		 if (mText2Speech==null){
			 
			 mText2Speech = new TextToSpeech(Prescriptiondetail.this,Prescriptiondetail.this);	 
		 }
		  if(mText2Speech.isSpeaking()) {
              mText2Speech.stop();
              mText2Speech.shutdown();
          } else {
        	  mText2Speech.setSpeechRate((float) 1.0);
              mText2Speech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
          }
		  
		  
		  
	}

public void onActivityResult(int requestCode, int resultCode, Intent data)
{
	if (requestCode == MY_DATA_CHECK_CODE)
	{
      
        	mText2Speech = new TextToSpeech(this, this);
      
    }
		
}
	 
	  
}
