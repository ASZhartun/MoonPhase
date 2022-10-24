package com.example.moonphase;

// Phase - phase of the moon calculations
//
// Adapted from "moontool.c" by John Walker, Release 2.0.
//
// Copyright (C)1996,1998 by Jef Poskanzer <jef@mail.acme.com>.  All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
// OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
// LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
// OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE.
//
// Visit the ACME Labs Java page for up-to-date versions of this and other
// fine Java utilities: http://www.acme.com/java/


//	јлгоритм расчета фаз в классе Phase тер€ет новолуние 01.02.2003
//	«а новолунием 02.01.2003 следует новолуние 03.03.2003
//	Ёта ошибка повтор€етс€ в 2022 2041 2060 2079 2098 годах (щаг 19 лет)


public class Phase {

	// Astronomical constants.

	// 1980 January 0.0
	private static final double epoch = 2444238.5D;

	// Constants defining the Sun's apparent orbit.

	// ecliptic longitude of the Sun at epoch 1980.0
	private static final double elonge = 278.833540D;

	// ecliptic longitude of the Sun at perigee
	private static final double elongp = 282.596403D;

	// eccentricity of Earth's orbit
	private static final double eccent = 0.016718D;

	// semi-major axis of Earth's orbit, km
	private static final double sunsmax = 1.495985e8D;

	// sun's angular size, degrees, at semi-major axis distance
	private static final double sunangsiz = 0.533128D;

	// Elements of the Moon's orbit, epoch 1980.0.

	// moon's mean lonigitude at the epoch
	private static final double mmlong = 64.975464D;

	// mean longitude of the perigee at the epoch
	private static final double mmlongp = 349.383063D;

	// mean longitude of the node at the epoch
	// private static final double mlnode = 151.950429D;

	// inclination of the Moon's orbit
	// private static final double minc = 5.145396D;

	// eccentricity of the Moon's orbit
	private static final double mecc = 0.054900D;

	// moon's angular size at distance a from Earth
	private static final double mangsiz = 0.5181D;

	// semi-major axis of Moon's orbit in km
	private static final double msmax = 384401.0D;

	// parallax at distance a from Earth
	// private static final double mparallax = 0.9507D;

	// synodic month (new Moon to new Moon)
	private static final double synmonth = 29.53058868D;

	// base date for E. W. Brown's numbered series of lunations (1923 Jan 16)
	// private static final double lunatbase = 2423436.0D;

	// Properties of the Earth.

	// radius of Earth in kilometres
	// private static final double earthrad = 6378.16D;

	// Mathematical constants.
	private static final double EPSILON = 1E-6D;

	// Handy mathematical functions.

	// Gives the fractional part of a number
	public static double fraction(double x) {
		return x - Math.floor(x);
	}

	// Fix angle.
	private static double fixangle(double a) {
		double b = a - 360.0 * Math.floor(a / 360.0D);
		// Can't use Math.IEEEremainder here because remainder differs
		// from modulus for negative numbers.
		return b;
	}

	// Degrees to radians.
	private static double torad(double d) {
		return d * Math.PI / 180.0D;
	}

	// Radians to degrees.
	private static double todeg(double r) {
		return r * 180.0D / Math.PI;
	}

	// Sin from degrees.
	private static double dsin(double d) {
		return Math.sin(torad(d));
	}

	// Cos from degrees.
	private static double dcos(double d) {
		return Math.cos(torad(d));
	}

	/*
	 * // jdate - convert internal GMT date to Julian day. private static long
	 * jdate(Date t) { long c, m, y;
	 * 
	 * Calendar cal = new GregorianCalendar(); cal.setTime(t); y =
	 * cal.get(Calendar.YEAR) + 1900; m = cal.get(Calendar.MONTH) + 1; if (m > 2) m
	 * = m - 3; else { m = m + 9; --y; } c = y / 100L; // compute century y -= 100L
	 * * c; return cal.get(Calendar.DATE) + (c * 146097L) / 4 + (y * 1461L) / 4 + (m
	 * * 153L + 2) / 5 + 1721119L; }
	 * 
	 * // Convert internal date and time to astronomical Julian time // (i.e. Julian
	 * date plus day fraction, expressed as a double) public static double
	 * jtime(Date t) { int cc, ss, mm, hh;
	 * 
	 * Calendar cal = new GregorianCalendar(); cal.setTime(t); cc =
	 * cal.get(Calendar.ZONE_OFFSET); // !!! should this be negative? ss =
	 * cal.get(Calendar.SECOND); mm = cal.get(Calendar.MINUTE); hh =
	 * cal.get(Calendar.HOUR_OF_DAY); return (jdate(t) - 0.5) + jgdates.hhh(hh - cc,
	 * mm, ss) / 24.; }
	 * 
	 * // jyear - convert Julian date to year, month, day, which are // returned via
	 * integer pointers to integers public static void jyear(double td, RefInt yy,
	 * RefInt mm, RefInt dd) { double j, d, y, m;
	 * 
	 * td += 0.5; // astronomical to civil j = Math.floor(td); j = j - 1721119.0; y
	 * = Math.floor(((4 * j) - 1) / 146097.0); j = (j * 4.0) - (1.0 + (146097.0 *
	 * y)); d = Math.floor(j / 4.0); j = Math.floor(((4.0 * d) + 3.0) / 1461.0); d =
	 * ((4.0 * d) + 3.0) - (1461.0 * j); d = Math.floor((d + 4.0) / 4.0); m =
	 * Math.floor(((5.0 * d) - 3) / 153.0); d = (5.0 * d) - (3.0 + (153.0 * m)); d =
	 * Math.floor((d + 5.0) / 5.0); y = (100.0 * y) + j; if (m < 10.0) m = m + 3;
	 * else { m = m - 9; y = y + 1; } yy.val = (int) y; mm.val = (int) m; dd.val =
	 * (int) d; }
	 */
	// jdate - convert internal GMT date to Julian day.
	private static double jdate(int Y, int M, int D) { // Date t
		if (M > 2)
			M = M - 3;
		else {
			M = M + 9;
			--Y;
		}
		long c = Y / 100L; // compute century
		Y -= 100L * c;
		return D + (c * 146097L) / 4 + (Y * 1461L) / 4 + (M * 153L + 2) / 5 + 1721119L - 0.5;
	}

	private static double usek_val;
	private static double yy_val, mm_val, dd_val;

	// jyear - convert Julian date to year, month, day, which are
	// returned via integer pointers to integers
	public static void jyear(double td) {
		double j, d, y, m;

		td += 0.5; // astronomical to civil
		j = Math.floor(td);
		j = j - 1721119.0;
		y = Math.floor(((4 * j) - 1) / 146097.0);
		j = (j * 4.0) - (1.0 + (146097.0 * y));
		d = Math.floor(j / 4.0);
		j = Math.floor(((4.0 * d) + 3.0) / 1461.0);
		d = ((4.0 * d) + 3.0) - (1461.0 * j);
		d = Math.floor((d + 4.0) / 4.0);
		m = Math.floor(((5.0 * d) - 3) / 153.0);
		d = (5.0 * d) - (3.0 + (153.0 * m));
		d = Math.floor((d + 5.0) / 5.0);
		y = (100.0 * y) + j;
		if (m < 10.0)
			m = m + 3;
		else {
			m = m - 9;
			y = y + 1;
		}
		yy_val = (int) y;
		mm_val = (int) m;
		dd_val = (int) d;
	}
	
	public static long julian(int Y, int M, int D){
		return (1461*(Y+4800+(M-14)/12))/4 + (367*(M-2-12*((M-14)/12)))/12
		    - (3*((Y+4900+(M-14)/12)/100))/4 + D - 32075;
	}

	////////////////////////////////////////////////////////////
	// Calculation of the Sun's position (ecliptic longitude)
	public static double sunPosition(double jd) {
		double M, L, pi2 = Math.PI * 2, T = (jd - 2451545.) / 36525.;
		M = pi2 * fraction(0.993133 + 99.997361 * T);
		L = pi2 * fraction(
				0.7859453 + M / pi2 + (6893.0 * Math.sin(M) + 72.0 * Math.sin(2.0 * M) + 6191.2 * T) / 1296.0e3);
		return todeg(L);
	}

	////////////////////////////////////////////////////////////
	public static int zodiacSign(int Y, int M, int D) {
		return (int) sunPosition(jdate(Y, M, D)) / 30;
	}

	////////////////////////////////////////////////////////////
	public static int eastYearSign(int Y, int M, int D) {
		double phases[] = new double[5];
		double cj = jdate(Y, M, D);
		// ѕроверка на начало года по восточному календарю
		phasehunt5(jdate(Y, 2, 20), phases);
		if (cj >= jdate(Y, 1, 1) && cj < phases[0])
			Y--;
		return (Y - 40) % 12;
	}

	// вычисл€ет начало нового года по восточному календарю
	// второе новолуние после зимнего солнцесто€ни€
	//  между 21 €нвар€ и 21 феврал€.
	// простейший вариант, почти всегда верно :)
	public static double chineseNewYear(int Y) {
		// ver 1
		double[] phases = new double[5];
		Phase.phasehunt5(jdate(Y, 1, 20), phases);
		return phases[4];

		// ver 2
//		double ws = seasonDate(3, Y, 3) + 2; // зимнее солнцесто€ние
//		double[] phases = new double[5];
//		Phase.phasehunt5(ws, phases); // 1е новолуние
//		ws = phases[4] + 2;
//		Phase.phasehunt5(ws, phases); // 2е новолуние
//		return phases[4];
	}

	// seasons start dates
	// mYear - номер года
	// dt - разница с GMT, часы 
	public static void seasonDates(int mYear, double dt, double[] sd) {
		double m1, m2, m3, m4;
		dt /= 24;
		m1 = ((double) mYear - 2000.) / 1000.;
		m2 = m1 * m1;
		m3 = m2 * m1;
		m4 = m3 * m1;

		sd[0] = (2451623.80984 + 365242.37404 * m1 + 0.05169 * m2 - 0.00411 * m3 - 0.00057 * m4) + dt;
		sd[1] = (2451716.56767 + 365241.62603 * m1 + 0.00325 * m2 + 0.00888 * m3 - 0.00030 * m4) + dt;
		sd[2] = (2451810.21715 + 365242.01767 * m1 - 0.11575 * m2 + 0.00337 * m3 + 0.00078 * m4) + dt;
		sd[3] = (2451900.05952 + 365242.74049 * m1 - 0.06223 * m2 - 0.00823 * m3 + 0.00032 * m4) + dt;
	}
	public static double seasonDate(int season, int mYear, double dt) {
		double m1, m2, m3, m4;
		dt /= 24;
		m1 = ((double) mYear - 2000.) / 1000.;
		m2 = m1 * m1;
		m3 = m2 * m1;
		m4 = m3 * m1;

		switch(season) {
		case 0: return (2451623.80984 + 365242.37404 * m1 + 0.05169 * m2 - 0.00411 * m3 - 0.00057 * m4) + dt;
		case 1: return (2451716.56767 + 365241.62603 * m1 + 0.00325 * m2 + 0.00888 * m3 - 0.00030 * m4) + dt;
		case 2: return (2451810.21715 + 365242.01767 * m1 - 0.11575 * m2 + 0.00337 * m3 + 0.00078 * m4) + dt;
		case 3: return (2451900.05952 + 365242.74049 * m1 - 0.06223 * m2 - 0.00823 * m3 + 0.00032 * m4) + dt;
		default: return 0;
		}
	}

	// truephase - given a K value used to determine the mean phase of the
	// new moon, and a phase selector (0.0, 0.25, 0.5, 0.75),
	// obtain the true, corrected phase time
	private static double truephase(double k, double phase) {
		double t, t2, t3, pt, m, mprime, f;
		boolean apcor = false;

		k += phase; /* add phase to new moon time */
		t = k / 1236.85; /* time in Julian centuries from 1900 January 0.5 */
		t2 = t * t; /* square for frequent use */
		t3 = t2 * t; /* cube for frequent use */
		pt = 2415020.75933 /* mean time of phase */
				+ synmonth * k + 0.0001178 * t2 - 0.000000155 * t3
				+ 0.00033 * dsin(166.56 + 132.87 * t - 0.009173 * t2);

		m = 359.2242 /* Sun's mean anomaly */
				+ 29.10535608 * k - 0.0000333 * t2 - 0.00000347 * t3;
		mprime = 306.0253 /* Moon's mean anomaly */
				+ 385.81691806 * k + 0.0107306 * t2 + 0.00001236 * t3;
		f = 21.2964 /* Moon's argument of latitude */
				+ 390.67050646 * k - 0.0016528 * t2 - 0.00000239 * t3;
		if ((phase < 0.01) || (Math.abs(phase - 0.5) < 0.01)) {
			/* Corrections for New and Full Moon. */
			pt += (0.1734 - 0.000393 * t) * dsin(m) + 0.0021 * dsin(2 * m) - 0.4068 * dsin(mprime)
					+ 0.0161 * dsin(2 * mprime) - 0.0004 * dsin(3 * mprime) + 0.0104 * dsin(2 * f)
					- 0.0051 * dsin(m + mprime) - 0.0074 * dsin(m - mprime) + 0.0004 * dsin(2 * f + m)
					- 0.0004 * dsin(2 * f - m) - 0.0006 * dsin(2 * f + mprime) + 0.0010 * dsin(2 * f - mprime)
					+ 0.0005 * dsin(m + 2 * mprime);
			apcor = true;
		} else if ((Math.abs(phase - 0.25) < 0.01 || (Math.abs(phase - 0.75) < 0.01))) {
			pt += (0.1721 - 0.0004 * t) * dsin(m) + 0.0021 * dsin(2 * m) - 0.6280 * dsin(mprime)
					+ 0.0089 * dsin(2 * mprime) - 0.0004 * dsin(3 * mprime) + 0.0079 * dsin(2 * f)
					- 0.0119 * dsin(m + mprime) - 0.0047 * dsin(m - mprime) + 0.0003 * dsin(2 * f + m)
					- 0.0004 * dsin(2 * f - m) - 0.0006 * dsin(2 * f + mprime) + 0.0021 * dsin(2 * f - mprime)
					+ 0.0003 * dsin(m + 2 * mprime) + 0.0004 * dsin(m - 2 * mprime) - 0.0003 * dsin(2 * m + mprime);
			if (phase < 0.5)
				/* First quarter correction. */
				pt += 0.0028 - 0.0004 * dcos(m) + 0.0003 * dcos(mprime);
			else
				/* Last quarter correction. */
				pt += -0.0028 + 0.0004 * dcos(m) - 0.0003 * dcos(mprime);
			apcor = true;
		}
		if (!apcor)
			throw new InternalError("Phase.truephase() called with invalid phase selector");
		return pt;
	}

	//////////////////////////////////////////////////////////////////////////
	private static double meanphase(double sdate, double phase) {
		double k, t, t2, t3, nt1;

		jyear(sdate);
		k = (yy_val + ((mm_val - 1) * (1.0 / 12.0)) - 1900) * 12.3685;

		// Время в юлианских столетиях от 1900 January 0.5.
		t = (sdate - 2415020.0) / 36525;
		t2 = t * t; // квадрат величины
		t3 = t2 * t; // куб величины

		usek_val = k = Math.floor(k) + phase;
		nt1 = 2415020.75933 + synmonth * k + 0.0001178 * t2 - 0.000000155 * t3
				+ 0.00033 * dsin(166.56 + 132.87 * t - 0.009173 * t2);

		return nt1;
	}

	// Find time of phases of the moon which surround the current
	// date. Five phases are found, starting and ending with the
	// new moons which bound the current lunation.
	public static void phasehunt5(double sdate, double[] phases) {
		double adate, nt1, nt2, k1, k2;

		adate = sdate - 45;
		nt1 = meanphase(adate, 0.0);
		k1 = usek_val;
		for (;;) {
			adate += synmonth;
			nt2 = meanphase(adate, 0.0);
			k2 = usek_val;
			if (nt1 <= sdate && nt2 > sdate)
				break;
			nt1 = nt2;
			k1 = k2;
		}
		phases[0] = truephase(k1, 0.0);
		phases[1] = truephase(k1, 0.25);
		phases[2] = truephase(k1, 0.5);
		phases[3] = truephase(k1, 0.75);
		phases[4] = truephase(k2, 0.0);
	}

	// phasehunt2 - find time of phases of the moon which surround the current
	// date. Two phases are found.
	public static void phasehunt2(double sdate, double[] phases, double[] which) {
		double adate, nt1, nt2, k1, k2;

		adate = sdate - 45;
		nt1 = meanphase(adate, 0.0);
		k1 = usek_val;
		for (;;) {
			adate += synmonth;
			nt2 = meanphase(adate, 0.0);
			k2 = usek_val;
			if (nt1 <= sdate && nt2 > sdate)
				break;
			nt1 = nt2;
			k1 = k2;
		}
		phases[0] = truephase(k1, 0.0);
		which[0] = 0.0;
		phases[1] = truephase(k1, 0.25);
		which[1] = 0.25;
		if (phases[1] <= sdate) {
			phases[0] = phases[1];
			which[0] = which[1];
			phases[1] = truephase(k1, 0.5);
			which[1] = 0.5;
			if (phases[1] <= sdate) {
				phases[0] = phases[1];
				which[0] = which[1];
				phases[1] = truephase(k1, 0.75);
				which[1] = 0.75;
				if (phases[1] <= sdate) {
					phases[0] = phases[1];
					which[0] = which[1];
					phases[1] = truephase(k2, 0.0);
					which[1] = 0.0;
				}
			}
		}
	}

	// kepler - solve the equation of Kepler
	private static double kepler(double m, double ecc) {
		double e, delta;

		e = m = torad(m);
		do {
			delta = e - ecc * Math.sin(e) - m;
			e -= delta / (1 - ecc * Math.cos(e));
		} while (Math.abs(delta) > EPSILON);
		return e;
	}

	// Calculate phase of moon as a fraction.
	//
	// RefDouble pphaseR, RefDouble mageR, RefDouble distR,
	// RefDouble angdiaR, RefDouble sudistR, RefDouble suangdiaR)
	//
	// @param pdate time for which the phase is requested, as from jtime()
	// @param pphaseR Ref for illuminated fraction of Moon's disk
	// @param mageR Ref for age of moon in days
	// @param distR Ref for distance in km from center of Earth
	// @param angdiaR Ref for angular diameter in degrees as seen from Earth
	// @param sudistR Ref for distance in km to Sun
	// @param suangdiaR Ref for Sun's angular diameter
	// @return terminator phase angle as a fraction of a full circle (i.e., 0 to 1)
	//
	public static double phase(double pdate, double[] pp) {
		double Day, N, M, Ec, Lambdasun, ml, MM, Ev, Ae, A3, MmP, mEc, A4, lP, V, lPP, MoonAge, MoonPhase, MoonDist,
				MoonDFrac, MoonAng, F, SunDist, SunAng;

		// Calculation of the Sun's position.

		Day = pdate - epoch; // date within epoch
		N = fixangle((360 / 365.2422) * Day); // mean anomaly of the Sun
		M = fixangle(N + elonge - elongp); // convert from perigee co-ordinates
											// to epoch 1980.0
		Ec = kepler(M, eccent); // solve equation of Kepler
		Ec = Math.sqrt((1 + eccent) / (1 - eccent)) * Math.tan(Ec / 2);
		Ec = 2 * todeg(Math.atan(Ec)); // true anomaly
		Lambdasun = fixangle(Ec + elongp); // Sun's geocentric ecliptic
											// longitude
		// Orbital distance factor.
		F = ((1 + eccent * Math.cos(torad(Ec))) / (1 - eccent * eccent));
		SunDist = sunsmax / F; // distance to Sun in km
		SunAng = F * sunangsiz; // Sun's angular size in degrees

		// Calculation of the Moon's position.

		// Moon's mean longitude.
		ml = fixangle(13.1763966 * Day + mmlong);

		// Moon's mean anomaly.
		MM = fixangle(ml - 0.1114041 * Day - mmlongp);

		// Evection.
		Ev = 1.2739 * Math.sin(torad(2 * (ml - Lambdasun) - MM));

		// Annual equation.
		Ae = 0.1858 * Math.sin(torad(M));

		// Correction term.
		A3 = 0.37 * Math.sin(torad(M));

		// Corrected anomaly.
		MmP = MM + Ev - Ae - A3;

		// Correction for the equation of the centre.
		mEc = 6.2886 * Math.sin(torad(MmP));

		// Another correction term.
		A4 = 0.214 * Math.sin(torad(2 * MmP));

		// Corrected longitude.
		lP = ml + Ev + mEc - Ae + A4;

		// Variation.
		V = 0.6583 * Math.sin(torad(2 * (lP - Lambdasun)));

		// True longitude.
		lPP = lP + V;

		// Calculation of the phase of the Moon.

		// Age of the Moon in degrees.
		MoonAge = lPP - Lambdasun;

		// Phase of the Moon.
		MoonPhase = (1 - Math.cos(torad(MoonAge))) / 2;

		// Calculate distance of moon from the centre of the Earth.

		MoonDist = (msmax * (1 - mecc * mecc)) / (1 + mecc * Math.cos(torad(MmP + mEc)));

		// Calculate Moon's angular diameter.

		MoonDFrac = MoonDist / msmax;
		MoonAng = mangsiz / MoonDFrac;

		pp[0] = MoonPhase;
		pp[1] = synmonth * (fixangle(MoonAge) / 360.0);
		pp[2] = MoonDist;
		pp[3] = MoonAng;
		pp[4] = SunDist;
		pp[5] = SunAng;

		return torad(fixangle(MoonAge));
	}

}
