package me.oei.util;

/**
 * @author Chris Oei
 *
 */

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Locale;

import org.apache.log4j.*;

public class StarDate {
	protected static final TimeZone UTC = TimeZone.getTimeZone("UTC");
	protected static final Locale LOCALE = Locale.ENGLISH;
	public static final String StandardDateFormat = "%1$tA, %1$tB %1$td, %1$tY %1$tI:%1$tM:%1$tS %1$Tp %1$tZ";
	public static final double SECOND = 3.1688765e-08;
	public static final double MINUTE = 1.9013259e-06;
	public static final double HOUR = 0.00011407955;
	public static final double DAY = 0.0027379093;
	public static final String VERSION = "3.0.0";
	public static final String helpURL = "http://jarvis/notes/StarDateHelp";
	private static final int MAX_YEAR_CACHED = 2100;
	private static long START_OF_YEAR[] = new long[MAX_YEAR_CACHED + 1];
	static { // Pre-fill cache with most commonly queried years.
		START_OF_YEAR[2011] = 1293840000000L;
		START_OF_YEAR[2012] = 1325376000000L;
	}

	static Logger log = Logger.getLogger(StarDate.class);

	/**
	 * The double-precision floating point representation.
	 */
	private double y;

	// Factory Methods
	// --------------------------------------------------------------------------------------
	/**
	 * Create a StarDate object using the time in milliseconds past the epoch.
	 * 
	 * @param timeInMillis
	 * 
	 */
	public static StarDate getStarDate(long timeInMillis) {
		Calendar calutc = getDefaultCalendar();
		calutc.setTimeInMillis(timeInMillis);
		int year = calutc.get(Calendar.YEAR);
		double y0 = getStartOfYear(year);
		double y1 = getStartOfYear(year + 1);
		return getStarDate(year + (double) (timeInMillis - y0) / (y1 - y0));
	}

	/**
	 * Create a StarDate object using the date/time of a Calendar object.
	 * 
	 * @param cal
	 *            the input calendar object (any time zone)
	 */
	public static StarDate getStarDate(Calendar cal) {
		if (cal.getTimeZone() == UTC) {
			// Special case. No need to convert to UTC, so go for speed.
			int year = cal.get(Calendar.YEAR);
			double y0 = getStartOfYear(year);
			double y1 = getStartOfYear(year + 1);
			return getStarDate(year + (double) (cal.getTimeInMillis() - y0)
					/ (y1 - y0));
		}
		return getStarDate(cal.getTimeInMillis());
	}

	/**
	 * Create a StarDate object using the double-precision float representation
	 * of a StarDate.
	 * 
	 * @param x
	 *            the input double-precision float
	 */
	public static StarDate getStarDate(double x) {
		StarDate sd = new StarDate();
		sd.y = x;
		if (x < 1) {
			log.warn("StarDates < 1.0 have not been unit tested.");
		}
		return sd;
	}

	/**
	 * The StarDate copy constructor.
	 * 
	 * @param x
	 *            the StarDate object to copy
	 */
	public static StarDate getStarDate(StarDate sd) {
		return getStarDate(sd.y);
	}

	/*
	 * Create a StarDate object using a string representation of the StarDate.
	 * 
	 * @param x the String representation
	 */
	public static StarDate parseStarDate(String x) {
		return getStarDate(Double.parseDouble(x));
	}

	/**
	 * Create a StarDate object using the date/time stored in a Date object.
	 * 
	 * @param d
	 *            the input Date object
	 */
	public static StarDate getStarDate(Date d) {
		Calendar cal = getDefaultCalendar();
		cal.setTime(d);
		return getStarDate(cal);
	}

	/**
	 * Create a StarDate object using the current date/time.
	 */
	public static StarDate getCurrentStarDate() {
		return getStarDate(new Date());
	}

	public static StarDate getStarDate(TimeZone tz, int year, int month,
			int day, int hour, int minute, int second) {
		Calendar cal = new GregorianCalendar(tz, LOCALE);
		cal.set(Calendar.YEAR, year);
		// Don't forget month starts with 0
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		return getStarDate(cal);
	}
	
	/**
	 * @param x	The date in RFC2822 format (used in emails)
	 * @return A new StarDate object
	 * @throws ParseException
	 */
	public static StarDate parseRFC2822(String x) throws ParseException {
		return StarDate.getStarDate(new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss Z").parse(x));
	}
	
	/**
	 * @param x The date in the format git uses
	 * @return A new StarDate object
	 * @throws ParseException
	 */
	public static StarDate parseGitDate(String x) throws ParseException {
		return StarDate.getStarDate(new SimpleDateFormat(
		"EEE MMM dd HH:mm:ss yyyy Z").parse(x));
	}

	// ------------------------------------------------------------------------------------------------

	// Clients should use the factory methods instead of the constructor.
	protected StarDate() {

	}

	// Getters
	// ----------------------------------------------------------------------------------------------

	public double getDouble() {
		return y;
	}

	public long getTimeInMillis() {
		int y0 = (int) y;
		long t0 = getStartOfYear(y0);
		long t1 = getStartOfYear(y0 + 1);
		return (long) (t0 + (t1 - t0) * (y - y0));
	}

	/**
	 * Returns the StarDate as a string with precision of approximately 1/3 day.
	 * 
	 * @return A string representation of the floating-point StarDate.
	 */
	public String getApproximate() {
		return String.format("%.3f", y);
	}

	// public String getStandardDateString(String timezoneString) {
	// TimeZone tz = TimeZone.getTimeZone(timezoneString);
	// Calendar cal = new GregorianCalendar(tz, LOCALE);
	// return String.format(StandardDateFormat, cal);
	// }

	/**
	 * Get the Calendar representation of the StarDate object.
	 * 
	 * @return the Calendar object
	 */
	public Calendar getCalendar() {
		Calendar cal = getDefaultCalendar();
		cal.setTimeInMillis(getTimeInMillis());
		return cal;
	}

	/**
	 * Get the Date representation of the StarDate object.
	 * 
	 * @return the Date object
	 */
	public Date getDate() {
		return new Date(getTimeInMillis());
	}

	public String getYearString() {
		return Integer.toString((int) y);
	}

	public String toString() {
		return String.format("%04.13f", y);
	}

	// Utilities
	// ------------------------------------------------------------------------------------------------
	private static long getStartOfYear(int y) {
		if ((y >= 0) && (y <= MAX_YEAR_CACHED)) {
			if (START_OF_YEAR[y] != 0) {
//				log.info("cache hit for year " + y + ": " + START_OF_YEAR[y]);
				return START_OF_YEAR[y];
			} else if (y == 1970) {
				// Special hack. Auto-initialized cache (zero) is by coincidence correct for 1970.
				return 0;
			} else {
				Calendar cal = getDefaultCalendar();
				cal.set(Calendar.YEAR, y);
				cal.set(Calendar.MONTH, Calendar.JANUARY);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				START_OF_YEAR[y] = cal.getTimeInMillis();
				log.info("cache store for year " + y + ": " + START_OF_YEAR[y]);
				return START_OF_YEAR[y];
			}
		}
		Calendar cal = getDefaultCalendar();
		cal.set(Calendar.YEAR, y);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	protected static Calendar getDefaultCalendar() {
		Calendar cal = new GregorianCalendar(UTC, LOCALE);
		cal.clear();
		return cal;
	}

	/**
	 * java com.nestria.sim.StarDate "America/Los_Angeles" 2010 2 27 8 33
	 * computes the StarDate for 2/27/2010 08:33 AM PST.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		int sz = args.length;
		// If no args, then return current StarDate.
		if (sz == 0) {
			System.out.println(new StarDate());
			System.exit(0);
		}
		// If only 1 arg, assume it is a StarDate to be converted to Date/Time
		if (sz == 1) {
			if (args[0].equals("--version")) {
				System.out.println(VERSION);
			} else if (args[0].equals("--help")) {
				java.awt.Desktop.getDesktop().browse(
						java.net.URI.create(helpURL));
			} else {
				System.out.println(StarDate.parseStarDate(args[0]).getDate());
			}
			System.exit(0);
		}

		if ((sz > 1) && args[0].equals("--file")) {
			if (sz == 2) {
				System.out.println(StarDate.getStarDate(new Date((new File(
						args[1])).lastModified())));
				System.exit(0);
			} else {
				log.error("Unknown usage.");
				System.exit(1);
			}
		}
		if ((sz > 1) && args[0].equals("--rfc2822")) {
			if (sz == 2) {
				SimpleDateFormat format = new SimpleDateFormat(
						"EEE, dd MMM yyyy HH:mm:ss Z");
				try {
					System.out.println(StarDate.getStarDate(format
							.parse(args[1])));
				} catch (ParseException e) {
					log.error(e);
					e.printStackTrace();
				}
				System.exit(0);
			} else {
				log.error("Unknown usage.");
				System.exit(1);
			}
		}

		TimeZone tz = TimeZone.getTimeZone(args[0]);
		// Unknown time zone returns "GMT".
		if ((!args[0].equals("GMT")) && (tz.getID().equals("GMT"))) {
			log.error("Unknown time zone: " + args[0]);
			System.exit(1);
		}
		Calendar cal = new GregorianCalendar(tz, LOCALE);
		cal.clear();
		cal.set(Calendar.YEAR, Integer.valueOf(args[1]));
		// Don't forget month starts with 0
		cal.set(Calendar.MONTH, Integer.valueOf(args[2]) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(args[3]));
		if (sz >= 5) {
			cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(args[4]));
		} else {
			// if hour is not specified, assume mid-day in that time zone
			cal.set(Calendar.HOUR_OF_DAY, 12);
		}
		if (sz >= 6) {
			cal.set(Calendar.MINUTE, Integer.valueOf(args[5]));
		}
		if (sz >= 7) {
			cal.set(Calendar.SECOND, Integer.valueOf(args[6]));
		}
		log.info(new Date(cal.getTimeInMillis()));
		System.out.println(StarDate.getStarDate(cal));
	}
}
