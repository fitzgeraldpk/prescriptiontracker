package com.prescriptiontracker;
/**
 * @author Patrick Fitzgerald MScSED Minor Thesis 2013
 * Calls new intent to play instructional video
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class InstructionVideo  extends Activity{
	protected static VideoView vv = null;
	
 	@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.video_view);
			
			
			 Button btnBack = (Button) findViewById(R.id.btnVideoBack);
			
			 vv = (VideoView) findViewById(R.id.videoView);
			
			 
			 String fileName = "android.resource://" + getPackageName() + "/" + R.raw.tutorial;
			 
			 vv.setVideoURI(Uri.parse(fileName));
			 
			 vv.start();
			 
			 
			 
			 btnBack.setOnClickListener(new android.view.View.OnClickListener() 
		        {
				 public void onClick(View view) {
					 Context context = getApplicationContext();
					 Intent intent = new Intent(context, Prescriptiontracker.class);
	            	 	
					 startActivity(intent);
				        
				 }
				 
		        });
			 
			 
 	}
	
}
