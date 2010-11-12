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
	public static final String StandardDateFormat = "%1$tA, %1$tB %1$td, %1$tY %1$tI:%1$tM:%1$tS %1$Tp %1$tZ";
	public static final double SECOND = 3.1688765e-08;
	public static final double MINUTE = 1.9013259e-06;
	public static final double HOUR = 0.00011407955;
	public static final double DAY = 0.0027379093;

	public static final double FallOfRome = 476.6747495446266;
	public static final double MayerRothschildBorn = 1744.1446948998178;
	public static final double BlackFriday = 1869.7307077625571; // Gould gold
																	// scheme
	public static final double BlackThursday = 1929.8130136986301;
	public static final double MicrosoftIPO = 1986.1961757990869;
	public static final double September11 = 2001.6946061643835;
	public static final double MadoffConfession = 2008.9404599271402;

	public static final String VERSION = "1.1.0";

	static Logger logger = Logger.getLogger(StarDate.class);

	/**
	 * The double-precision floating point representation.
	 */
	public double y;

	public static StarDate parseStarDate(String x) {
		return new StarDate(Double.parseDouble(x));
	}

	/**
	 * Create a StarDate object using the current date/time.
	 */
	public StarDate() {
		Calendar cal = getDefaultCalendar();
		cal.setTime(new Date());
		initialize(cal);
	}

	/**
	 * Create a StarDate object using the double-precision float representation
	 * of a StarDate.
	 * 
	 * @param x
	 *            the input double-precision float
	 */
	public StarDate(double x) {
		y = x;
	}

	/**
	 * Create a StarDate object using the date/time stored in a Date object.
	 * 
	 * @param d
	 *            the input Date object
	 */
	public StarDate(Date d) {
		Calendar cal = getDefaultCalendar();
		cal.setTime(d);
		initialize(cal);
	}

	/**
	 * Create a StarDate object using the date/time of a Calendar object.
	 * 
	 * @param cal
	 *            the input calendar object (any time zone)
	 */
	public StarDate(Calendar cal) {
		Calendar calutc = getDefaultCalendar();
		calutc.setTimeInMillis(cal.getTimeInMillis());
		initialize(calutc);
	}

	/**
	 * The StarDate copy constructor.
	 * 
	 * @param x
	 *            the StarDate object to copy
	 */
	public StarDate(StarDate x) {
		y = x.y;
	}

	/**
	 * Returns the StarDate as a string with precision of approximately 1/3 day.
	 * 
	 * @return A string representation of the floating-point StarDate.
	 */
	public String getStarDate() {
		return String.format("%.3f", y);
	}

	public String getStandardDateString(String timezoneString) {
		TimeZone tz = TimeZone.getTimeZone(timezoneString);
		Locale loc = Locale.ENGLISH;
		Calendar cal = new GregorianCalendar(tz, loc);
		return String.format(StandardDateFormat, cal);
	}

	public String getHTML(String timezoneString) {
		String r = "<a href='http://www.oei.me/stardate.php/";
		r += y;
		r += "/" + timezoneString;
		r += "' title='";
		r += getStandardDateString(timezoneString);
		r += "'>";
		r += y;
		r += "</a>";
		return r;
	}

	/**
	 * Get the Calendar representation of the StarDate object.
	 * 
	 * @return the Calendar object
	 */
	public Calendar getCalendar() {
		Calendar cal = getDefaultCalendar();
		int y0 = (int) y;
		long t0 = getStartOfYear(y0);
		long t1 = getStartOfYear(y0 + 1);
		cal.setTimeInMillis((long) (t0 + (t1 - t0) * (y - y0)));
		return cal;
	}
	
	public String getYearString() {
		return Integer.toString((int) y);
	}

	public String toString() {
		return String.format("%04.13f", y);
	}

	/**
	 * Sets the StarDate object based on the Calendar object.
	 * 
	 * @param cal
	 *            Input calendar -- must be in UTC.
	 */
	protected void initialize(Calendar cal) {
		assert (cal.getTimeZone() == TimeZone.getTimeZone("UTC"));
		int year = cal.get(Calendar.YEAR);
		double y0 = getStartOfYear(year);
		double y1 = getStartOfYear(year + 1);
		y = year + (double) (cal.getTimeInMillis() - y0) / (y1 - y0);
		logger.debug("StarDate.y = " + y);
		if (y < 1) {
			logger.warn("StarDates < 1.0 have not been unit tested.");
		}
	}

	protected long getStartOfYear(int y) {
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

	protected Calendar getDefaultCalendar() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		Locale loc = Locale.ENGLISH;
		Calendar cal = new GregorianCalendar(tz, loc);
		cal.clear();
		return cal;
	}

	/**
	 * java com.nestria.sim.StarDate "America/Los_Angeles" 2010 2 27 8 33
	 * computes the StarDate for 2/27/2010 08:33 AM PST.
	 */
	public static void main(String[] args) {
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
			} else {
				StarDate sd = new StarDate(Double.valueOf(args[0]));
				System.out
						.println(new Date(sd.getCalendar().getTimeInMillis()));
			}
			System.exit(0);
		}
		
		if ((sz > 1) && args[0].equals("--file")) {
			if (sz == 2) {
				System.out.println(new StarDate(new Date((new File(args[1])).lastModified())));
				System.exit(0);
			} else {
				logger.error("Unknown usage.");
				System.exit(1);
			}
		}
		if ((sz > 1) && args[0].equals("--rfc2822")) {
			if (sz == 2) {
				SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
				try {
					System.out.println(new StarDate(format.parse(args[1])));
				} catch (ParseException e) {
					logger.error(e);
					e.printStackTrace();
				}
				System.exit(0);
			} else {
				logger.error("Unknown usage.");
				System.exit(1);
			}
		}

		TimeZone tz = TimeZone.getTimeZone(args[0]);
		// Unknown time zone returns "GMT".
		if ((!args[0].equals("GMT")) && (tz.getID().equals("GMT"))) {
			logger.error("Unknown time zone: " + args[0]);
			System.exit(1);
		}
		Locale loc = Locale.ENGLISH;
		Calendar cal = new GregorianCalendar(tz, loc);
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
		logger.info(new Date(cal.getTimeInMillis()));
		System.out.println(new StarDate(cal));
	}
}
