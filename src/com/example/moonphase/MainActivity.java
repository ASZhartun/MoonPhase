package com.example.moonphase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {

	final String DATE_PATTERN = "dd.MM.yyyy HH:mm:ss";

	EditText dateField;
	EditText zoneField;

	ListView moonParams;
	ListView moonPhases;

	Button runBtn;
	
	ArrayList<HashMap<String, String>> sourceParams;
	ArrayList<HashMap<String, String>> sourcePhases;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// ����� �������, ������, �������
		dateField = (EditText) findViewById(R.id.dateField);
		zoneField = (EditText) findViewById(R.id.zoneField);
		moonParams = (ListView) findViewById(R.id.moonParams);
		moonPhases = (ListView) findViewById(R.id.moonPhases);
		runBtn = (Button) findViewById(R.id.runBtn);
		// ��������� ������� ���� � ������ ������� � �������� �����
		Date curr = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));
		dateField.setText(sdf.format(curr));
		int hoursGMT = TimeZone.getTimeZone("Europe/Minsk").getRawOffset() / 3600000;
		zoneField.setText("GMT: " + Integer.toString(hoursGMT));
		// ��������� ��������� �������� ����
		sourceParams = getParamCalculations(dateField.getText().toString());
		ListAdapter myAdapter = new SimpleAdapter(
				this, 
				sourceParams, 
				R.layout.item_data, 
				new String[] {ParameterItem.NAME, ParameterItem.VALUE}, 
				new int[] {R.id.name, R.id.value});
		moonParams.setAdapter(myAdapter);
		// ��������� ��������� �������� ��� ����
		sourcePhases = getPhasesCalculations(dateField.getText().toString());
		ListAdapter phaseAdapter = new SimpleAdapter(
				this, 
				sourcePhases, 
				R.layout.item_data, 
				new String[] {ParameterItem.NAME, ParameterItem.VALUE}, 
				new int[] {R.id.name, R.id.value});
		moonPhases.setAdapter(phaseAdapter);
		
		runBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

	private ArrayList<HashMap<String, String>> getParamCalculations(String dateForm) {
		double jd = new Jdays(dateForm).get();
		HashMap<String, String> jdMap = new ParameterItem("��. ����", Double.toString(jd));

		double[] pp = new double[6];
		Phase.phase(jd, pp);

		int phase = (int) (pp[0] * 100);
		HashMap<String, String> phaseMap = new ParameterItem("����", Integer.toString(phase));

		String grow = (int) pp[1] + "� " + (int) (24 * (pp[1] - Math.floor(pp[1]))) + "� " + (int) (1440 * (pp[1] - Math.floor(pp[1]))) % 60 + "�";
		HashMap<String, String> growMap = new ParameterItem("�������", grow);
		
		ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		results.add(jdMap);
		results.add(phaseMap);
		results.add(growMap);
		return results;
	}
	
	private ArrayList<HashMap<String, String>> getPhasesCalculations(String dateForm) {
		double jd = new Jdays(dateForm).get();

		double[] pp = new double[5];
		Phase.phasehunt5(jd, pp);
		// TO-DO ��������� ��� � ������� �� ����� ����������� !!! 
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN.substring(0, DATE_PATTERN.length()-3));
		HashMap<String, String> newMoonBegin = new ParameterItem("���������: ", sdf.format(new Date((long) pp[0])));
		HashMap<String, String> firstQuarter = new ParameterItem("1 ��������: ", sdf.format(new Date((long) pp[1])));
		HashMap<String, String> fullMoon = new ParameterItem("����������: ", sdf.format(new Date((long) pp[2])));
		HashMap<String, String> thirdQuarter = new ParameterItem("3 ��������: ", sdf.format(new Date((long) pp[3])));
		HashMap<String, String> newMoonEnd = new ParameterItem("���������: ", sdf.format(new Date((long) pp[4])));
		
		ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		results.add(newMoonBegin);
		results.add(firstQuarter);
		results.add(fullMoon);
		results.add(thirdQuarter);
		results.add(newMoonEnd);
		return results;
	}


}
