package me.oei.util;

/**
 * @author Chris Oei
 *
 */

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Locale;

import org.apache.log4j.*;

public class WikiStarDate extends StarDate {
	static Logger logger = Logger.getLogger(WikiStarDate.class);

	public static void main(String[] args) {
		int sz = args.length;
		// If no args, then return current StarDate.
		if (sz == 0) {
			System.out.println(new StarDate().y);
			System.exit(0);
		}
		// If only 1 arg, assume it is a StarDate to be converted to Date/Time
		if (sz == 1) {
			StarDate sd = new StarDate(Double.valueOf(args[0]));
			System.out.println(new Date(sd.getCalendar().getTimeInMillis()));
			System.exit(0);
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
		System.out.print("<StarDate tz='"+args[0]+"'>");
		System.out.print(new StarDate(cal).y);
		System.out.println("</StarDate>");
	}
}
