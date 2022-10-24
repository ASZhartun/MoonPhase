package com.example.moonphase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

	final String DATE_PATTERN = "dd.MM.yyyy HH:mm";

	EditText dateField;
	EditText zoneField;

	ListView moonParams;
	ListView moonPhases;

	Button runBtn;
	
	ArrayList<HashMap<String, String>> sourceParams;
	ArrayList<HashMap<String, String>> sourcePhases;
	
	SimpleAdapter paramAdapter;
	SimpleAdapter phaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Выбор инпутов, кнопки, списков
		dateField = (EditText) findViewById(R.id.dateField);
		zoneField = (EditText) findViewById(R.id.zoneField);
		moonParams = (ListView) findViewById(R.id.moonParams);
		moonPhases = (ListView) findViewById(R.id.moonPhases);
		runBtn = (Button) findViewById(R.id.runBtn);
		// Получение текущей даты в нужном формате и часового пояса
		Calendar myCal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Minsk"));
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));
		dateField.setText(sdf.format(myCal.getTime()));
		int hoursGMT = TimeZone.getTimeZone("Europe/Minsk").getRawOffset() / 3600000;
		zoneField.setText("GMT: " + Integer.toString(hoursGMT));
		// Отображение расчетных значений луны
		sourceParams = getParamCalculations(dateField.getText().toString());
		paramAdapter = new SimpleAdapter(
				this, 
				sourceParams, 
				R.layout.item_data, 
				new String[] {ParameterItem.NAME, ParameterItem.VALUE}, 
				new int[] {R.id.name, R.id.value});
		moonParams.setAdapter(paramAdapter);
		// Получение расчетных значений фаз луны
		sourcePhases = getPhasesCalculations(dateField.getText().toString());
		phaseAdapter = new SimpleAdapter(
				this, 
				sourcePhases, 
				R.layout.item_data, 
				new String[] {ParameterItem.NAME, ParameterItem.VALUE}, 
				new int[] {R.id.name, R.id.value});
		moonPhases.setAdapter(phaseAdapter);
		
		runBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sourceParams.clear();
				ArrayList<HashMap<String, String>> newerParamSource = getParamCalculations(dateField.getText().toString());
				for (int i = 0; i < newerParamSource.size(); i++) {
					sourceParams.add(newerParamSource.get(i));
				}
				paramAdapter.notifyDataSetChanged();
				
				sourcePhases.clear();
				ArrayList<HashMap<String, String>> newerPhasesSource = getPhasesCalculations(dateField.getText().toString());
				for (int i = 0; i < newerPhasesSource.size(); i++) {
					sourcePhases.add(newerPhasesSource.get(i));
				}
				phaseAdapter.notifyDataSetChanged();
			}
		});
	}

	private ArrayList<HashMap<String, String>> getParamCalculations(String dateForm) {
		Jdays myDate = new Jdays();
		myDate.set(dateForm, true);
		double jd = myDate.get();
		HashMap<String, String> jdMap = new ParameterItem("Юл. дата", Double.toString(jd));

		double[] pp = new double[6];
		Phase.phase(jd, pp);

		int phase = (int) (pp[0] * 100);
		HashMap<String, String> phaseMap = new ParameterItem("Фаза", Integer.toString(phase));

		String grow = (int) pp[1] + "д " + (int) (24 * (pp[1] - Math.floor(pp[1]))) + "ч " + (int) (1440 * (pp[1] - Math.floor(pp[1]))) % 60 + "м";
		HashMap<String, String> growMap = new ParameterItem("Возраст", grow);
		
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
		
		HashMap<String, String> newMoonBegin = new ParameterItem("Новолуние: ", new Jdays(pp[0]).format(10));
		HashMap<String, String> firstQuarter = new ParameterItem("1 четверть: ", new Jdays(pp[1]).format(10));
		HashMap<String, String> fullMoon = new ParameterItem("Полнолуние: ", new Jdays(pp[2]).format(10));
		HashMap<String, String> thirdQuarter = new ParameterItem("3 четверть: ", new Jdays(pp[3]).format(10));
		HashMap<String, String> newMoonEnd = new ParameterItem("Новолуние: ", new Jdays(pp[4]).format(10));
		
		ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		results.add(newMoonBegin);
		results.add(firstQuarter);
		results.add(fullMoon);
		results.add(thirdQuarter);
		results.add(newMoonEnd);
		return results;
	}



}
