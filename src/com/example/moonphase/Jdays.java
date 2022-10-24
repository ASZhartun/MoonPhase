package com.example.moonphase;

import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;

public class Jdays {

	private double jd;
	private int sign;
	private int t[] = new int[6];

	public static final int YEAR = 1;
	public static final int MONTH = 2;
	public static final int DAY = 3;
	public static final int HOUR = 4;
	public static final int MIN = 5;
	public static final int SEC = 6;

	public static final int JDAY = 7;
	public static final int YMDHMS = 8;
	public static final int DMYHMS = 9;
	public static final int DMYHM = 10;
	public static final int DMY = 11;
	public static final int YMD = 12;
	public static final int HMS = 13;

	public Jdays() {
		jd = 0;
		sign = -1;
	}

	public Jdays(double j) {
		set(j);
	}

	public Jdays(String s) {
		set(s, true); // DMY
	}

	public Jdays(int year, int month, int day) {
		set(year, month, day, 0, 0, 0);
	}

	public Jdays(int year, int month, int day, int hou, int min, int sec) {
		set(year, month, day, hou, min, sec);
	}

	public Jdays(Calendar c) {
		set(c);
	}

	public void set(double j) {
		gDate(jd = j);
	}

	public double set(Calendar c) {
		set(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), //
		c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
		return jd;
	}

	public double set(int year, int month, int day) {
		gDate(jd = jDay(year, month, day, 0));
		return jd;
	}

	public double set(int year, int month, int day, int hou, int min, int sec) {
		gDate(jd = jDay(year, month, day, hhh(hou, min, sec)));
		return jd;
	}

	// YYYY.MM.DD или YYYY.MM.DD HH:MM:SS
	// Формат: YYYY.MM.DD если DMY=false, и DD.MM.YYYY если DMY=true
	// знак '-' до новой эры
	public double set(String S, boolean DMY) {
		if (S == null || S.length() == 0)
			return 0;

		jd = t[0] = t[1] = t[2] = t[3] = t[4] = t[5] = 0;
		sign = S.contains("-") ? -1 : 1; // знак '-' до новой эры
		String ss = S.replace('-', ' ');
		S = ss.trim();
		String[] part = S.split("[\\: \\.]");

		for (int i = 0; i < part.length; i++)
			t[i] = part[i] != null ? Integer.valueOf(part[i]) : 0;

		if (DMY) {
			int buf = t[0];
			t[0] = t[2];
			t[2] = buf;
		}

		return jd = jDay(t[0] * sign, t[1], t[2], hhh(t[3], t[4], t[5]));
	}

	public double get() {
		return jd;
	}

	public int get(int flag) {
		return flag >= 1 && flag <= 6 ? t[flag - 1] : 0;
	}

	public Calendar getCalendar() {
		return new GregorianCalendar(t[0], t[1] - 1, t[2], t[3], t[4], t[5]);
	}

	public int getWeekDayNumber() {
		return ((int) (jd + 1.5)) % 7;
	}

	// ==+== Преобразование часов, минут, секунд в градусы с долями ====
	public static double hhh(int h, int m, int s) {
		return h + m / 60. + s / 3600.;
	}

	// ==+== Юлианская дата для любой даты с 4713 г. до новой эры =========
	public static double jDay(int year, int month, int day, double hour) {
		int b;
		long a = 10000L * year + 100L * month + day;

		if (month <= 2) {
			month += 12;
			--year;
		}
		if (a <= 15821004L)
			b = -2 + (year + 4716) / 4 - 1179;
		else
			b = year / 400 - year / 100 + year / 4;

		return 365. * year - 679004. + 2400000.5 + b + Math.floor(30.6001 * (month + 1)) + day + hour / 24.;
	}

	public void gDate(double jd) {
		int b, d, f;
		double c, e, hour, jd0 = Math.floor(jd + .5);
		sign = jd > 1721423.5 ? 1 : -1; // - до новой эры

		if (jd0 < 2299161.) { // юлианский календарь
			b = 0;
			c = jd0 + 1524.;
		} else { // григориаский календарь
			b = (int) ((jd0 - 1867216.25) / 36524.25);
			c = jd0 + (b - (int) (b / 4)) + 1525.;
		}

		d = (int) ((c - 122.1) / 365.25);
		e = 365. * d + Math.floor(d / 4.);
		f = (int) ((c - e) / 30.6001);

		t[2] = (int) (Math.floor(c - e + .5) - Math.floor(30.6001 * f));
		t[1] = f - 1 - 12 * (int) (f / 14);
		t[0] = d - 4715 - (7 + t[1]) / 10;
		hour = 24. * (jd + 0.5 - jd0);

		hour += 0.5 / 3600;
		t[3] = (int) hour;

		hour -= t[3];
		hour *= 60;
		t[4] = (int) hour;

		hour -= t[4];
		hour *= 60;
		t[5] = (int) hour;
	}

	public String format(double jd, int flag) {
		set(jd);
		return format(flag);
	}

	public String format(int flag) {
		Formatter fmtr = new Formatter();
		switch (flag) {
		case YMDHMS: // date + time [yyyy.mm.dd hh:mm:ss]
			fmtr.format("%04d.%02d.%02d %02d:%02d:%02d", t[0] * sign, t[1], t[2], t[3], t[4], t[5]);
			break;
		case DMYHMS: // date + time [dd.mm.yyyy hh:mm:ss]
			fmtr.format("%02d.%02d.%04d %02d:%02d:%02d", t[2], t[1], t[0] * sign, t[3], t[4], t[5]);
			break;
		case DMYHM: // date + time [dd.mm.yyyy hh:mm]
			int rsec = (int) (t[5] + 0.5) / 60;
			fmtr.format("%02d.%02d.%04d %02d:%02d", t[2], t[1], t[0] * sign, t[3], t[4] + rsec);
			break;
		case DMY: // date [dd.mm.yyyy]
			fmtr.format("%02d.%02d.%04d", t[2], t[1], t[0] * sign);
			break;
		case YMD: // date [yyyy.mm.dd]
			fmtr.format("%04d.%02d.%02d", t[0] * sign, t[1], t[2]);
			break;
		case HMS: // time [hh.mm.ss]
			fmtr.format("%02d:%02d:%02d", t[3], t[4], t[5]);
			break;
		case JDAY: // julian day
			fmtr.format("%.6f", jd);
			break;
		}
		String result = fmtr.out().toString();
		fmtr.close();
		return result;
	}

}
