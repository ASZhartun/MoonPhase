package com.example.moonphase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	final String DATE_PATTERN = "dd.MM.yyyy HH:mm:ss";

	EditText dateField;
	EditText zoneField;
	
	ListView moonParams;
	ListView moonPhases;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dateField = (EditText) findViewById(R.id.dateField);
		zoneField = (EditText) findViewById(R.id.zoneField);
		moonParams = (ListView) findViewById(R.id.moonParams);
		moonPhases = (ListView) findViewById(R.id.moonPhases);
		
		Date curr = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));
		dateField.setText(sdf.format(curr));
		int hoursGMT = TimeZone.getTimeZone("Europe/Minsk").getRawOffset() / 3600000;
		zoneField.setText("GMT: " + Integer.toString(hoursGMT));
		
		
	}

}
