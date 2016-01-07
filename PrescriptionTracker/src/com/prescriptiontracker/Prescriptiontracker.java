package com.prescriptiontracker;

/**
 * @author Patrick Fitzgerald MScSED Minor Thesis 2013
 * 
 */

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.IntentSender.SendIntentException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
//database
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.prescriptionwriter.prescriptionendpoint.Prescriptionendpoint;
import com.prescriptionwriter.prescriptionendpoint.Prescriptionendpoint.Builder;
import com.prescriptionwriter.prescriptionendpoint.model.Prescription;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.GooglePlusUtil;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;


public class Prescriptiontracker  extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener,OnInitListener, OnAccessRevokedListener {

		private NfcAdapter mNfcAdapter;
		private PendingIntent mPendingIntent;
	    private IntentFilter[] mFilters;
	    private String[][] mTechLists;
	    private ConnectionResult mConnectionResult=new ConnectionResult(0, mPendingIntent);
		private boolean mWriteMode =false;
		IntentFilter[] mReadTagFilters;
	    IntentFilter[] mWriteTagFilters;
	   // List<DateTime> datesTaken=new ArrayList<DateTime>();
	    
	    //login 
	    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	    private ProgressDialog mConnectionProgressDialog;
	    private PlusClient mPlusClient;
	    
	    //database
	    private SQLiteDatabase database;
	    private Trackerlocal dbHelper;
  	  	private String[] allColumns = { Trackerlocal.COLUMN_ID,Trackerlocal.COLUMN_DATETIME,Trackerlocal.COLUMN_PROCESSED };
  	  	
  	  	//TTS
  	  	private static TextToSpeech mText2Speech;
  	  	private static final int MY_DATA_CHECK_CODE = 1234;
  	  	private static final String START="START";
  	  	private static final String IN_PROGRESS="INPROGRESS";
  	  	private static final String UPDATED="UPDATED";
  	  	private static final String ERROR="ERROR";
  	  	private static final String NOT_CONNECTED="NOTCONNECTED";
		
  	  	private String txtInstance;
  	  	private boolean updateInProgress=false;
  	  	
  	  	
  	  	
		
  	 
  	  	@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			//set content view
			setContentView(R.layout.activity_main);
				 
			int errorCode = GooglePlusUtil.checkGooglePlusApp(this);
		    
			if (errorCode != GooglePlusUtil.SUCCESS)
			{
				GooglePlusUtil.getErrorDialog(errorCode, this, 0).show();
		    }
			else{
			
				mPlusClient = new PlusClient.Builder(this, this, this)
				.setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
				.build();
			  
		    }
	
			// Progress bar to be displayed if the connection failure is not resolved.
			mConnectionProgressDialog = new ProgressDialog(this);
			mConnectionProgressDialog.setMessage("Signing in...");
			//instructions
			 Button instructionBtn = (Button) findViewById(R.id.btnInstructions);
			//login button		 
			 Button signInButton = (Button) findViewById(R.id.sign_in_button);
			 
			// Button revokeBtn = (Button) findViewById(R.id.btnRevoke);
			signInButton.setOnClickListener(new View.OnClickListener() {
				
			//video view
				
			@Override
			public void onClick(View v) {
        	  
				if (isUserConnected()){
					Context context = getApplicationContext();
					
					if (mConnectionResult == null)
					{      	
						mConnectionProgressDialog.show();
					}
					else{
						try {
							mConnectionResult.startResolutionForResult(Prescriptiontracker.this,REQUEST_CODE_RESOLVE_ERR);
                    
                     
						} 
						catch (SendIntentException e) {
							// Try connecting again.
							mConnectionResult = null;
							mPlusClient.connect();
						}
        	 
					}
             
					if (mPlusClient.isConnected())
					{
						if (mPlusClient.getAccountName()!=null){	
	           	  		String useremail=mPlusClient.getAccountName();
	            	 	Intent intent = new Intent(context, Prescriptiondetail.class);
	            	 	intent.putExtra("userEmail",useremail);
	            	 	startActivity(intent);
						}
					}
             
             
				}
				else{
					txtInstance=NOT_CONNECTED;   	
					// Fire off an intent to check if a TTS engine is installed
			        Intent checkIntent = new Intent();
			        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
			        
					
					Context context = getApplicationContext();
						Toast.makeText(context, "There is no network access (mobile data or wifi access)", Toast.LENGTH_SHORT).show();
       
				}
        	  
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
		     } 
		   	 catch (MalformedMimeTypeException e)
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
		     
		     // Fire off an intent to check if a TTS engine is installed
		        
		        
		        instructionBtn.setOnClickListener(new android.view.View.OnClickListener() 
		        {
				 public void onClick(View view) {
					

					 /*Intent checkIntent = new Intent();
				        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
				        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
				        txtInstance=START;*/
					 	Context context = getApplicationContext();
						Intent intent = new Intent(context, InstructionVideo.class);
	            	 	
	            	 	startActivity(intent);
				        
				 }
				 
		        });
		        
		      
		        
		        
		      /* revokeBtn.setOnClickListener(new android.view.View.OnClickListener() 
		        {
				 public void onClick(View view) {
					
					 if (mPlusClient.isConnected()) {
							// Clear the default account as in the Sign Out.
							mPlusClient.clearDefaultAccount();
			 
							// Go away and revoke access to this entire application.
							// This will call back to onAccessRevoked when it is
							// complete as it needs to go away to the Google
							// authentication servers to revoke all token.
							mPlusClient.revokeAccessAndDisconnect(Prescriptiontracker.this);
						}
		        	
		        
				        
				 }
				 
		        });*/
		        
		        
		        
		    }
		
		//creates dialog for Yes and No options
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	switch (which){
		        	case DialogInterface.BUTTON_POSITIVE:
		        	processIntent(getIntent());
		            break;

		        	case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};
	   
		
		
		/**
	    * 
	    * */
		 @Override
		public void onResume() {
			 super.onResume();
		      
		     if (mNfcAdapter != null) 
		    	 mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mReadTagFilters,
	                    mTechLists);
	        
		       //alter dialog not being used --delete?
		      //  AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		              
		        
		     if (getIntent().getAction() != null)
		     {
		       
		    	 // tag received when app is not running and not in the foreground:
		    	 if (getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
		            {
		    		 processIntent(getIntent());
		    		 //builder.setMessage("Do you want to record a prescription intake?").setPositiveButton("Yes", dialogClickListener)
		    		 //    .setNegativeButton("No", dialogClickListener).show();
		            }
		      }
		      //set action so prescription not updated if application is just resuming and not tag not touched  
		      getIntent().setAction("MAIN");

		           
		  }  

		
		 /**
		  * 
		  */
		 @Override
		public void onNewIntent(Intent intent) {
		    
			 if (!mWriteMode)
			 {
		        
				 	setIntent(intent);
		            	
		     } 
			 else{  
		        	 
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
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.mscsed_nfc, menu);
			return true;
		}
		
		  @Override 
	        public boolean onOptionsItemSelected(MenuItem item) {
	            if (item.getItemId() == R.id.action_settings) {
	            	 if (mPlusClient.isConnected()) {
							// Clear the default account as in the Sign Out.
							mPlusClient.clearDefaultAccount();
			 
							// Go away and revoke access to this entire application.
							// This will call back to onAccessRevoked when it is
							// complete as it needs to go away to the Google
							// authentication servers to revoke all token.
							mPlusClient.revokeAccessAndDisconnect(Prescriptiontracker.this);
						}
	            }
	           
	            return true;
	        }
		
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
		       
		        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) || action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
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
		    
		    /**
		     * 
		     * @param msg
		     */
		  private void readTag(final NdefMessage msg)
		    {
			  String prescriptionId = new String(msg.getRecords()[0].getPayload());
		      
			  //This will add new prescription intake for the prescription id
			  getPrescription(prescriptionId); 
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
  
		  	private class GetPrescriptionThread extends AsyncTask<String, Void, String> {
					  	  
		  		Prescription prescription=new Prescription(); 
		  		List<DateTime> datesTaken=new ArrayList<DateTime>();
		  		Date now = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateString = dateFormat.format(now);
				com.google.api.client.util.DateTime today=new com.google.api.client.util.DateTime(now);//,TimeZone.getTimeZone("GMT")
				com.google.api.client.util.DateTime presDate;
				private String updated;
				final String UPDATED="UPDATED";
				final String NOTUPDATED="NOTUPDATED";
				
				 ProgressDialog progDialog;			  	 
					
				  protected void onPreExecute() {
			            super.onPreExecute();
			            progDialog = new ProgressDialog(Prescriptiontracker.this);
			            progDialog.setMessage("Updating prescription data....");
			            progDialog.setIndeterminate(false);
			            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			            progDialog.setCancelable(true);
			            progDialog.show();
			        }
				
				
				@Override
				protected String doInBackground(String... params) {
					Context context = getApplicationContext();
					ContentValues values = new ContentValues();
					String processedN="n";
					TrackerDataSource(context);
					database = dbHelper.getWritableDatabase();
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
							            	 
										            	//add to existing list
						if (prescription.getDatesTaken()==null)
						{
							//do nothing
										            			
						}
						else{
							//get back list
							datesTaken=prescription.getDatesTaken();
						}
																	 
					} catch (IOException e) {
						//store to local storage if you cannot access cloud backend
						values.put(Trackerlocal.COLUMN_ID, params[0].toString()); 
						values.put(Trackerlocal.COLUMN_DATETIME,dateString);
						values.put(Trackerlocal.COLUMN_PROCESSED, processedN);
						database.insert(Trackerlocal.TABLE_TRACKER, null,values);
						updated=UPDATED;
						//  throw new RuntimeException("Error retrieving prescription", e);
					}
					if (prescription!=null&&isUserConnected())
					{ 	
						List<Date> localTimes = new ArrayList<Date>();
						try {
							localTimes=getUnprocessedDates(Prescriptiontracker.this,params[0].toString());
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (localTimes.size()>0)
						{
							ContentValues upvalues = new ContentValues();
							String processedY="y";
							String dateUpdString;
							          	
							for (Date d:localTimes){
								presDate=new com.google.api.client.util.DateTime(d);
								datesTaken.add(presDate);
								dateUpdString	= dateFormat.format(d);
								String strFilterUpdate = "datetime='"+dateUpdString+"'"+" and prescriptionid='" + params[0].toString()+"'" ;
								String strFilterDelete= "processed='y'";
								upvalues.put(Trackerlocal.COLUMN_ID, params[0].toString()); 
								upvalues.put(Trackerlocal.COLUMN_DATETIME,dateUpdString);
								upvalues.put(Trackerlocal.COLUMN_PROCESSED, processedY);	
								database.update(Trackerlocal.TABLE_TRACKER,upvalues,strFilterUpdate,null);
								database.delete(Trackerlocal.TABLE_TRACKER,strFilterDelete,null);
							}
													            	
						}
						datesTaken.add(today);
						if (datesTaken!=null&&isUserConnected())
						{
							
							prescription.setDatesTaken(datesTaken);
									    	
							try {
								prescription=	endpoint.updatePrescription(prescription).execute();
								updated=UPDATED;
							} catch (IOException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								updated=NOTUPDATED;
							}
									    	
						}
										
										
					}
					return updated;
						         		
				}
													
						         		
							
				protected void onPostExecute(String updated){
							
					updateInProgress=false;
					progDialog.dismiss();		
								/*	List<Date> localTimes = new ArrayList<Date>();
									
							    	
									 try {
										localTimes=getUnprocessedDates(Prescriptiontracker.this,"e2f35764-8ba1-4fde-895c-75b32ee3ccf2");
									} catch (ParseException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									 
									 Toast.makeText(Prescriptiontracker.this,
					                         "cursor count: " +cursorCount,
					                         Toast.LENGTH_LONG).show();
									 for (Date d:localTimes){
										 Toast.makeText(Prescriptiontracker.this,
						                         "Date not processed: " +d.toString(),
						                         Toast.LENGTH_LONG).show();
										 
									 }*/
								 
					if (updated==UPDATED)
					{   txtInstance=UPDATED;
						Intent checkIntent = new Intent();
				        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
				        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
				           
						
						Toast.makeText(Prescriptiontracker.this,
				                         "Prescription tracker sucessfully updated",
				                         Toast.LENGTH_LONG).show();
					}	 
					else{
						
						txtInstance=ERROR;
						Intent checkIntent = new Intent();
				        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
				        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
								
						Toast.makeText(Prescriptiontracker.this,
							                         "Prescription Tag is empty or not valid",
							                         Toast.LENGTH_LONG).show();
					}	    
				}
		  	}		
			  			
						 
						 
						 
		  /**
		   * 
		   * @param prescriptionId
		   */
		  public void getPrescription(String prescriptionId){
			  
			  if (updateInProgress==false)
			  {//do not want to record accidental touches
				  updateInProgress=true;
				  
				  GetPrescriptionThread prescription = new GetPrescriptionThread();
			  
				  prescription.execute(prescriptionId);
			  }
			  
			  
		  }

		  /**
		   * 
		   */
		  @Override
		  public void onConnectionFailed(ConnectionResult result) {
			  if (mConnectionProgressDialog.isShowing())
			  {
              // The user clicked the sign-in button already. Start to resolve
              // connection errors. Wait until onConnected() to dismiss the
              // connection dialog.
				  if (result.hasResolution()) {
					  try {
						  result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                      	} 	catch (SendIntentException e) {
                          // mPlusClient.connect();
                      		Context context = getApplicationContext();
                      		Toast.makeText(context,
			                         "Login unsuccessful",
			                         Toast.LENGTH_LONG).show();
                      	}
				  }
			  }

      // Save the intent so that we can start an activity when the user clicks
      // the sign-in button.
			  mConnectionResult = result;
		  }

		  /**
		   * 
		   */
		  @Override
		  protected void onStart() {
		     
			  super.onStart();
		      
			  mPlusClient.connect();
		  }

		 /**
		  *  
		  */
		  @Override
		  protected void onStop() {
		  
			  super.onStop();
		      
			  mPlusClient.disconnect();
		  }
		  
		/**
		 *   
		 */
			  @Override
		public void onConnected(Bundle connectionHint) {
		
			// We've resolved any connection errors.
		 
			mConnectionProgressDialog.dismiss();
		}


		/**
		 * 	  
		 */
			  @Override
		public void onDisconnected() {
				  Log.d("mscSED NFC", "disconnected");
			
		}
		 
			  //datasbase
		/**
		 * 	  
		 * @param context
		 */
		public void TrackerDataSource(Context context) {
		   
			dbHelper = new Trackerlocal(context);
		  
		}
		
		/**
		 * 
		 * @throws SQLException
		 */
		public void open() throws SQLException {
		
			  database = dbHelper.getWritableDatabase();
		  
		  }
		
		/**
		 * 
		 */
		public void close() {
		  
			  dbHelper.close();
		  
		  }
		/**
		 *   
		 * @param context
		 * @param prescriptionId
		 * @return
		 * @throws ParseException
		 */
		  @SuppressLint("SimpleDateFormat")
		public List<Date> getUnprocessedDates(Context context,String prescriptionId) throws ParseException {
			  
			  dbHelper = new Trackerlocal(context);
			  
			  Date date=new Date();
			  
			  String strDate;
				
			  com.google.api.client.util.DateTime today;
			  
			  List<Date> dates = new ArrayList<Date>();
			  
			  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  
			  SQLiteDatabase db = dbHelper.getWritableDatabase();
			  
			  Cursor cursor = db.rawQuery("select datetime from trackerlocal where processed='n' and prescriptionId='"+prescriptionId+"'", null);
			  
			  if (cursor.getCount() > 0) 
			  {
				  if (cursor.moveToFirst()) 
				  {
					  do {
						  	strDate=cursor.getString(0);            	
						  	date=(Date)dateFormat.parse(strDate);
		                
		                	today=new com.google.api.client.util.DateTime(date);
		                	dates.add(date);           

		                } while (cursor.moveToNext());
				  }
		        }
		        
			  cursor.close();
		      
			  db.close();
		      
			  return dates;
		    	}
			    	
	/**
	 * 	
	 * @return
	 */
		public boolean isUserConnected(){
			
			
			ConnectivityManager connMgr = (ConnectivityManager) 
			
			getSystemService(Context.CONNECTIVITY_SERVICE);
			
			NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
			
			boolean isWifiConn = networkInfo.isConnected();
			
			networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			
			boolean isMobileConn = networkInfo.isConnected();
			
			if (isMobileConn || isWifiConn)
			{
				return true;
				
			}
			else{
				
				return false;
			}
		}
		
	
	/**
	 * TextToSpeech (TTS) Section	
	 * @param text
	 */
		public void textToSpeech (String text){
			
			 if (mText2Speech==null){
				 
				 mText2Speech = new TextToSpeech(Prescriptiontracker.this,Prescriptiontracker.this);	 
			 }
			  if(mText2Speech.isSpeaking()) {
                  mText2Speech.stop();
                  mText2Speech.shutdown();
              } else {
            	  mText2Speech.setSpeechRate((float) 1.0);
                  mText2Speech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
              }
			  
			  
			  
		}


	@Override
	public void onInit(int arg0) {
		 if(arg0 == TextToSpeech.SUCCESS) {
	            mText2Speech.setLanguage(Locale.getDefault());
	          ///  mText2Speech.speak("Hello folks, welcome to my little demo on Text To Speech.",
	           //         TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
	           //         null);
	            
	            
	            
	            if (txtInstance==START)
	            {
	            	textToSpeech("Touch your phone against the prescription tag if you have taken your prescription. Or login to see your prescription data.");
	            }
	            else if(txtInstance==UPDATED) {
	            
	            	 textToSpeech("Your prescription data has been updated.");
	            	
	            }
	            else if (txtInstance==NOT_CONNECTED){
	            	
	            	textToSpeech("You cannot login. You do not have mobile or wifi access. You can still touch tag to updated prescription data");
	            	
	            	
	            }
	            else if (txtInstance==ERROR){
	            	
	            	textToSpeech("There was an error updating the prescription data.");
	            	
	            	
	            }
	        }
		
	}
	

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	if (requestCode==9000)
    	{
    	//mPlusClient
    	 if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
             mConnectionResult = null;
             mPlusClient.connect();
         }
    	
    	
    	}
    	else{
    		if (requestCode == MY_DATA_CHECK_CODE)
    		
        
    		{
           // if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
          //  {
                // success, create the TTS instance
            	mText2Speech = new TextToSpeech(this, this);
          //  }
          //  else
          //  {
                // missing data, install it
         //       Intent installIntent = new Intent();
         //       installIntent.setAction(
          //              TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
          //      startActivity(installIntent);
          //  }
        }
    		
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
	public void onAccessRevoked(ConnectionResult status) {
		// TODO Auto-generated method stub
		mPlusClient.connect();
	}
		
		
		

	}//end of class
	
