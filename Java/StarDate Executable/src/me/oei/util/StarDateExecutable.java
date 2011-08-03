package me.oei.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.eluctari.util.StarDate;

import org.apache.log4j.Logger;

/**
 * This class provides the main() method for the StarDate executable. It relies on the
 * StarDate library, which is defined in its own Eclipse project.
 * @author Chris Oei
 *
 */
public class StarDateExecutable {
	private static final String helpURL = "http://jarvis/notes/StarDateHelp";
	private static Logger log = Logger.getLogger(StarDateExecutable.class);

	/**
	 * @param args
	 */

	// Main
	// ----------------------------------------------------------------------------------------------
	/**
	 * java com.nestria.sim.StarDate "America/Los_Angeles" 2010 2 27 8 33
	 * computes the StarDate for 2/27/2010 08:33 AM PST.
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		int sz = args.length;
		// If no args, then return current StarDate.
		if (sz == 0) {
			System.out.println(StarDate.newInstance());
			System.exit(0);
		}
		// If only 1 arg, assume it is a StarDate to be converted to Date/Time
		if (sz == 1) {
			if (args[0].equals("--version")) {
				System.out.println("3.2.2");
			} else if (args[0].equals("--help")) {
				try {
					java.awt.Desktop.getDesktop().browse(
							java.net.URI.create(helpURL));
				} catch (IOException e) {
					System.out.println("I/O Exception while attempting to open " + helpURL);
					System.exit(1);
				}
			} else {
				System.out.println(StarDate.parseStarDate(args[0]).getDate());
			}
			System.exit(0);
		}

		if ((sz > 1) && args[0].equals("--file")) {
			if (sz == 2) {
				System.out.println(StarDate.newInstance(new File(
						args[1])));
				System.exit(0);
			} else {
				log.error("Unknown usage.");
				System.exit(1);
			}
		}
		if ((sz > 1) && args[0].equals("--rfc2822")) {
			if (sz == 2) {
				try {
					System.out.println(StarDate.parseRFC2822(args[1]));
				} catch (ParseException e) {
					System.out.println("Unable to parse RFC-2822 date: " + args[1]);
					System.exit(1);
				}
				System.exit(0);
			} else {
				log.error("Unknown usage.");
				System.exit(1);
			}
		}
		
		if ((sz > 1) && args[0].equals("--git")) {
			if (sz == 2) {
				try {
					System.out.println(StarDate.parseGitDate(args[1]));
				} catch (ParseException e) {
					System.out.println("Unable to parse git date: " + args[1]);
					System.exit(1);
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
		Calendar cal = new GregorianCalendar(tz, Locale.ENGLISH);
		cal.clear();
		cal.set(Calendar.YEAR, Integer.parseInt(args[1]));
		// Don't forget month starts with 0
		cal.set(Calendar.MONTH, Integer.parseInt(args[2]) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(args[3]));
		if (sz >= 5) {
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(args[4]));
		} else {
			// if hour is not specified, assume mid-day in that time zone
			cal.set(Calendar.HOUR_OF_DAY, 12);
		}
		if (sz >= 6) {
			cal.set(Calendar.MINUTE, Integer.parseInt(args[5]));
		}
		if (sz >= 7) {
			cal.set(Calendar.SECOND, Integer.parseInt(args[6]));
		}
		log.info(new Date(cal.getTimeInMillis()));
		System.out.println(StarDate.newInstance(cal));
	}

}
