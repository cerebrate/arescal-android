package net.arkanesystems.arescal;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class Clock extends Activity {
	
	private Handler mHandler = new Handler();
	private String[] monthNames;
	private String[] dayNames;
	private String[] epMonthNames;
	private String[] earthDayNames;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        monthNames = getResources().getStringArray(R.array.darian_months);
        dayNames = getResources().getStringArray(R.array.darian_days);
        epMonthNames = getResources().getStringArray(R.array.ep_darian_months);
        earthDayNames = getResources().getStringArray(R.array.earth_days);
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    
    private Runnable mUpdateTimeTask = new Runnable() {
    	public void run() {
    		long curTime = System.currentTimeMillis();
    		// Set up our next callback.
    		// Do calculations based on curTime here.
    		final int daysPerDecade = 6686;
    		final int dayLength = 88775244; 
    		
    		//double msd_float = (2440587.50000 - 2405522 + (curTime / (float)86400000))/1.02749125;
    		double msd = (curTime - 947116800000L) / (double)dayLength + 44795.9998;
    		
    		// Adjust for start of Darian calendar
    		curTime += 11385983964000L;
    		
    		int timeOfDay = (int) ((msd - (int)msd)*dayLength);
    		long days = curTime / dayLength;
    		long decade = days / daysPerDecade;
    		
    		// XXX: This will probably break every century or so.
    		days += decade / 10;
    		
    		int day_of_decade = (int)(days % 6686);
    		int day_of_year = day_of_decade;
    		int year;
    		
    		// Subtract years until we get the one we want.
    		for(year = 0; year < 9; year++) {
    			if(year == 0 || year % 2 == 1) {
    				if(day_of_year < 669) {
    					break;
    				} else {
    					day_of_year -= 669;
    				}
    			} else {
    				if(day_of_year < 668) {
    					break;
    				} else {
    					day_of_year -= 668;
    				}
    			}
    		}
    		// Divide the year into (mostly) equal length quarters, then find our offset in that 
    		// quarter.
    		int month = ((day_of_year / 167) * 6 + (day_of_year % 167) / 28);
    		int day_of_month = ((day_of_year % 167) % 28) + 1;
    		// Adjust for leap years
    		if(month == 25) {
    			month = 24;
    			day_of_month = 28;
    		}
    		// Calculate time of day
    		int hour = timeOfDay / 3600000;
    		int minute = (timeOfDay  % 3600000)/ 60000;
    		int seconds = (timeOfDay% 60000) / 1000;
    		//String timeOfDay = Integer.toString( hour ) + ":" +
    		String date = String.format("%s, %d %s %d", dayNames[(day_of_month -1)% 7], (int)(year + decade * 10), monthNames[month], day_of_month);
    		String epDate = String.format("EP: %s, %d %s %d", earthDayNames[(day_of_month - 1)%7], (int)(year + decade * 10), epMonthNames[month], day_of_month);
    		String timeString = String.format("%d:%02d:%02d AMT", hour, minute, seconds);
    		String msdString = String.format("%.6f", msd);
    		
    		((TextView)findViewById(R.id.MarsTime)).setText(timeString);
    		((TextView)findViewById(R.id.MarsDate)).setText(date);
    		((TextView)findViewById(R.id.EPMarsDate)).setText(epDate);
    		((TextView)findViewById(R.id.MarsSolDate)).setText(msdString);
    		mHandler.postDelayed( this, 1000);
    	}
    };
}

