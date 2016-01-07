import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;


public   class DatePickerFragmentFrom extends DialogFragment
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
	
	// Create a new instance of DatePickerDialog and return it
	return new DatePickerDialog(getActivity(), this, year, month, day);
	}
	
	public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
	
		day=selectedDay;
	    month=selectedMonth;
	    year=selectedYear;
		
	
	}
	

	public int getDay() {
		
		return this.day;
					
		}
	
public int getMonth() {
		
		return this.month;
					
		}

public int getYear() {
	
	return this.year;
				
	}
	
	}
