package com.prescriptiontracker;
/**
 * @author Patrick Fitzgerald MScSED Minor Thesis 2013
 * creates the SQLite database which can store prescription id and dateTime if no network access
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class Trackerlocal extends SQLiteOpenHelper {

	public static final String TABLE_TRACKER = "trackerlocal";
	  public static final String COLUMN_ID = "prescriptionId";
	  public static final String COLUMN_DATETIME = "datetime";
	  public static final String COLUMN_PROCESSED = "processed";

	  private static final String DATABASE_NAME = "trackerlocal.db";
	  private static final int DATABASE_VERSION = 3;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + TABLE_TRACKER + "(" + COLUMN_ID
	      + " TEXT," + COLUMN_DATETIME +" TEXT,"+COLUMN_PROCESSED
	      + " text);";

	  public Trackerlocal(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(Trackerlocal.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKER);
	    onCreate(db);
	  }

	} 

