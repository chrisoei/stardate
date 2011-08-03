/**
 * 
 */
package me.oei.util;

import org.apache.log4j.Logger;
import net.jcip.annotations.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.TimeZone;
import java.util.Locale;

/**
 * @author Chris Oei
 *
 */
@ThreadSafe
class StartOfYear {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(StartOfYear.class);


		private static ConcurrentHashMap<Integer, Long> START_OF_YEAR;

		static { // Pre-fill cache with most commonly queried years.
			// No need to synchronize within static initializer, according to the
			// Java Language Specifications.
			START_OF_YEAR = new ConcurrentHashMap<Integer, Long>();
			START_OF_YEAR.put(Integer.valueOf(2009), Long.valueOf(1230768000000L));
			START_OF_YEAR.put(Integer.valueOf(2010), Long.valueOf(1262304000000L));
			START_OF_YEAR.put(Integer.valueOf(2011), Long.valueOf(1293840000000L));
			START_OF_YEAR.put(Integer.valueOf(2012), Long.valueOf(1325376000000L));
		}
		
		/**
		 * This function caches the result of the (expensive) Calendar functions. There
		 * is a better implementation in Java Concurrency In Practice listing 5.19, but
		 * this should work well enough for our purposes.
		 * @param y the (integer) year
		 * @return the (long) time in milliseconds from the epoch (UTC) to the start of year y
		 */
		public static long getStartOfYear(int y) {
			Integer y0 = Integer.valueOf(y);
			if (START_OF_YEAR.containsKey(y0)) {
//				log.info("cache hit for year " + y + ": " +START_OF_YEAR.get(y0));
				return START_OF_YEAR.get(y0).longValue();
			}
			
			Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

			cal.set(Calendar.YEAR, y);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			START_OF_YEAR.put(y0, Long.valueOf(cal.getTimeInMillis()));
//			log.info("cache store for year " + y + ": " + START_OF_YEAR.get(y0));
			return START_OF_YEAR.get(y0).longValue();
		}
}
