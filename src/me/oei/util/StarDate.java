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

import net.jcip.annotations.*;

import org.apache.log4j.*;

// Note that all unit tests have been moved to a separate project (TestStarDate) so that the StarDate
// jar files are as lean as possible.
/**
 * @author Chris Oei
 * 
 */
@ThreadSafe
public final class StarDate implements Comparable<StarDate> {
	/**
	 * One second, measured in units of StarDate.
	 */
	public static final double SECOND = 3.1688765e-08;
	/**
	 * One minute, measured in units of StarDate.
	 */
	public static final double MINUTE = 1.9013259e-06;
	/**
	 * One hour, measured in units of StarDate.
	 */
	public static final double HOUR = 0.00011407955;
	/**
	 * One day, measured in units of StarDate.
	 */
	public static final double DAY = 0.0027379093;

	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
	private static final Locale LOCALE = Locale.ENGLISH;

	private static final int MAX_YEAR_CACHED = 2100;
	
	@GuardedBy("itself")
	private static long START_OF_YEAR[];
	static { // Pre-fill cache with most commonly queried years.
		synchronized (START_OF_YEAR) {
			START_OF_YEAR = new long[MAX_YEAR_CACHED + 1];
			START_OF_YEAR[2009] = 1230768000000L;
			START_OF_YEAR[2010] = 1262304000000L;
			START_OF_YEAR[2011] = 1293840000000L;
			START_OF_YEAR[2012] = 1325376000000L;
		}
	}

	private static Logger log = Logger.getLogger(StarDate.class);

	/**
	 * The double-precision floating point representation.
	 */
	private final double y;

	// Factory Methods
	// --------------------------------------------------------------------------------------
	// See Effective Java: 41.8/731
	/**
	 * Create a StarDate object using the time in milliseconds past the epoch.
	 * 
	 * @param timeInMillis
	 *            the time in milliseconds past the epoch
	 * @return a new StarDate object
	 * 
	 */
	public static StarDate newInstance(long timeInMillis) {
		Calendar calutc = getDefaultCalendar();
		calutc.setTimeInMillis(timeInMillis);
		int year = calutc.get(Calendar.YEAR);
		double y0 = getStartOfYear(year);
		double y1 = getStartOfYear(year + 1);
		return newInstance(year + (timeInMillis - y0) / (y1 - y0));
	}

	/**
	 * Create a StarDate object using the date/time of a Calendar object.
	 * 
	 * @param cal
	 *            the input calendar object (any time zone)
	 * @return a new StarDate object
	 */
	public static StarDate newInstance(Calendar cal) {
		if (cal.getTimeZone() == UTC) {
			// Special case. No need to convert to UTC, so go for speed.
			int year = cal.get(Calendar.YEAR);
			double y0 = getStartOfYear(year);
			double y1 = getStartOfYear(year + 1);
			return newInstance(year + (cal.getTimeInMillis() - y0) / (y1 - y0));
		}
		return newInstance(cal.getTimeInMillis());
	}

	/**
	 * Create a StarDate object using the double-precision float representation
	 * of a StarDate.
	 * 
	 * @param x
	 *            the input double-precision float
	 * @return a new StarDate object
	 */
	public static StarDate newInstance(double x) {
		StarDate sd = new StarDate(x);

		return sd;
	}

	/**
	 * The StarDate copy factory method.
	 * 
	 * @param sd
	 *            the StarDate object to copy
	 * @return a new StarDate object
	 */
	public static StarDate newInstance(StarDate sd) {
		return newInstance(sd.y);
	}

	/**
	 * Create a StarDate object using a string representation of the StarDate.
	 * 
	 * @param x
	 *            the string representation of a StarDate (example:
	 *            "2010.12345")
	 * @return a new StarDate object
	 */
	public static StarDate parseStarDate(String x) {
		return newInstance(Double.parseDouble(x));
	}

	/**
	 * Create a StarDate object using the date/time stored in a Date object.
	 * 
	 * @param d
	 *            the input Date object
	 * @return a new StarDate object
	 */
	public static StarDate newInstance(Date d) {
		Calendar cal = getDefaultCalendar();
		cal.setTime(d);
		return newInstance(cal);
	}

	/**
	 * Create a StarDate object using the current date/time.
	 * 
	 * @return a new StarDate object
	 */
	public static StarDate newInstance() {
		return newInstance(new Date());
	}

	/**
	 * @param tz
	 *            a TimeZone object
	 * @param year
	 *            the (integer) year
	 * @param month
	 *            the month (January = 1, ..., December = 12)
	 * @param day
	 *            the day (1 to 31 inclusive)
	 * @param hour
	 *            the hour (0 to 23 inclusive)
	 * @param minute
	 *            the minute (0 to 59 inclusive)
	 * @param second
	 *            the second (0 to 59 inclusive)
	 * @return a new StarDate object
	 */
	public static StarDate newInstance(TimeZone tz, int year, int month,
			int day, int hour, int minute, int second) {
		Calendar cal = new GregorianCalendar(tz, LOCALE);
		cal.set(Calendar.YEAR, year);
		// Don't forget month starts with 0
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		return newInstance(cal);
	}

	/**
	 * Creates a new StarDate object from the last modification time of File x.
	 * 
	 * @param x
	 *            the File object to examine.
	 * @return a new StarDate object
	 */
	public static StarDate newInstance(File x) {
		return StarDate.newInstance(new Date(x.lastModified()));
	}

	/**
	 * @param x
	 *            the date in RFC2822 format (used in emails)
	 * @return a new StarDate object
	 * @throws ParseException
	 *             if date cannot be parsed
	 */
	public static StarDate parseRFC2822(String x) throws ParseException {
		return StarDate.newInstance(new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss Z").parse(x));
	}

	/**
	 * @param x
	 *            The date in the format git uses
	 * @return A new StarDate object
	 * @throws ParseException
	 *             if date cannot be parsed
	 */
	public static StarDate parseGitDate(String x) throws ParseException {
		return StarDate.newInstance(new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss yyyy Z").parse(x));
	}

	// ------------------------------------------------------------------------------------------------

	// Clients should use the factory methods instead of the constructor.
	// A private constructor and the lack of mutators makes this class
	// immutable.
	private StarDate() {
		y = 0;
		throw new AssertionError();
	}

	private StarDate(double x) {
		y = x;
		if (x < 1) {
			log.warn("StarDates < 1.0 have not been unit tested.");
		}
	}

	// Getters
	// ----------------------------------------------------------------------------------------------

	/**
	 * @return the double representation of the StarDate
	 */

	public double getDouble() {
		return y;
	}

	/**
	 * @return the milliseconds past the epoch that represents the StarDate
	 */
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
		return String.format("%.3f", Double.valueOf(y));
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

	/**
	 * @return the "year" part of the StarDate, represented as a String
	 */
	public String getYearString() {
		return Integer.toString((int) y);
	}

	// Utilities
	// ------------------------------------------------------------------------------------------------
	private static long getStartOfYear(int y) {
		synchronized (START_OF_YEAR) {
			if ((y >= 0) && (y <= MAX_YEAR_CACHED)) {
				if (START_OF_YEAR[y] != 0) {
					// log.info("cache hit for year " + y + ": " +
					// START_OF_YEAR[y]);
					return START_OF_YEAR[y];
				} else if (y == 1970) {
					// Special hack. Auto-initialized cache (zero) is by
					// coincidence
					// correct for 1970.
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
					// log.info("cache store for year " + y + ": " +
					// START_OF_YEAR[y]);
					return START_OF_YEAR[y];
				}
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

	private static Calendar getDefaultCalendar() {
		Calendar cal = new GregorianCalendar(UTC, LOCALE);
		// cal.clear();
		return cal;
	}

	// Object overrides
	// ----------------------------------------------------------------------------------------------

	@Override
	public boolean equals(Object o) {
		// See Effective Java: 92.9/731
		if (o instanceof StarDate) { // instanceof catches null case
			return y == ((StarDate) o).y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		// See Effective Java: 121.2/731
		long l = Double.doubleToLongBits(y);
		return (int) (l ^ (l >>> 32));
	}

	/**
	 * Returns the string representation of this StarDate. The string consists
	 * of a 4-digit year, followed by a period, followed by exactly 13 digits
	 * (padded in back with zeros if necessary.)
	 **/
	@Override
	public String toString() {
		return String.format("%.13f", Double.valueOf(y));
	}

	// @Override public StarDate clone() {
	// // See Effective Java: 135.3/731
	// try {
	// return (StarDate) super.clone();
	// } catch(CloneNotSupportedException e) {
	// throw new AssertionError(); //Can't happen
	// }
	// }

	@Override
	public int compareTo(StarDate x) {
		return Double.valueOf(y).compareTo(Double.valueOf(x.y));
	}
}
