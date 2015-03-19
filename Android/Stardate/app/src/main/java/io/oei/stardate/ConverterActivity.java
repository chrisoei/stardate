package io.oei.stardate;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;


public class ConverterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.converter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public static DatePicker datePicker;
        public static TimePicker timePicker;
        public static Spinner spinner;
        public static EditText editText;



        public static final Map<String, String> timeZoneMap = new HashMap<String, String>();
        static {
            timeZoneMap.put("Pacific Time", "US/Pacific");
            timeZoneMap.put("Mountain Time", "US/Mountain");
            timeZoneMap.put("Central Time", "US/Central");
            timeZoneMap.put("Eastern Time", "US/Eastern");
            timeZoneMap.put("Hawaii Time", "US/Hawaii");
            timeZoneMap.put("India Standard Time", "Asia/Kolkata");
            timeZoneMap.put("Coordinated Universal Time", "UTC");
        }

        public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
        public static final Locale LOCALE = Locale.ENGLISH;
        public static final String GMT =  "GMT";
        public static final String STRING_FORMAT = "%.15f";
        public static final int PLUS_YEARS = 1;

        public static final int INITIAL_DAY = 1;
        public static final int INITIAL_HOUR = 0;
        public static final int INITIAL_MINUTE = 0;
        public static final int INITIAL_SECOND = 0;
        public static final int INITIAL_MILLI = 0;

        public static final int START_YEAR = 2014;
        public static final int START_MONTH = 0;
        public static final int START_DAY = 1;
        public static final int START_HOUR = 1;
        public static final int START_MINUTE = 0;

        private static long getStartOfYear(int year) {
            Calendar cal = new GregorianCalendar(UTC, LOCALE);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, INITIAL_DAY);
            cal.set(Calendar.HOUR_OF_DAY, INITIAL_HOUR);
            cal.set(Calendar.MINUTE, INITIAL_MINUTE);
            cal.set(Calendar.SECOND, INITIAL_SECOND);
            cal.set(Calendar.MILLISECOND, INITIAL_MILLI);
            return cal.getTimeInMillis();
        }

        protected static TimeZone getSelectedTimeZone() {
            String tzs = timeZoneMap.get(spinner.getSelectedItem().toString());
            Log.d("stardate", "tzs = " + tzs);
            assert tzs != null;
            if (tzs == null) throw new RuntimeException();
            TimeZone tz = TimeZone.getTimeZone(tzs);
            Log.d("stardate", "tz.getID = " + tz.getID());
            // getTimeZone returns GMT for unknown strings
            if (tz.getID().equals(GMT) && !tzs.equals(GMT)) throw new RuntimeException();
            return tz;
        }

        protected static Calendar getSelectedCalendar() {
            return new GregorianCalendar(getSelectedTimeZone(), LOCALE);
        }

        public static void updateStardate() {
            Calendar cal = getSelectedCalendar();
            int year = datePicker.getYear();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, datePicker.getMonth());
            cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
            cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            cal.set(Calendar.SECOND, INITIAL_SECOND);
            cal.set(Calendar.MILLISECOND, INITIAL_MILLI);
            long millis = cal.getTimeInMillis();
            double y0 = getStartOfYear(year);
            double y1 = getStartOfYear(year + PLUS_YEARS);
            double sd = year + (millis - y0) / (y1 - y0);
            Log.d("stardate", "year = " + year + ", y0 = " + y0 + ", y1 = " + y1 + ", millis = " + millis + ", sd = " + sd);
            String x = String.format(STRING_FORMAT, sd);

            editText.setText(x, TextView.BufferType.EDITABLE);
        }

        public static void setPickers(Calendar cal) {
            datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
        }

        public static void updatePickers(View rootView) {
            double y = Double.parseDouble(((EditText)rootView.findViewById(R.id.editText)).getText().toString());
            int y0 = (int) y;
            long t0 = getStartOfYear(y0);
            long t1 = getStartOfYear(y0 + PLUS_YEARS);
            long millis = (long) (t0 + (t1 - t0) * (y - y0));
            Calendar cal = getSelectedCalendar();
            cal.setTimeInMillis(millis);
            setPickers(cal);
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_converter, container, false);
            datePicker = (DatePicker) rootView.findViewById(R.id.datePicker);
            datePicker.init(START_YEAR, START_MONTH, START_DAY, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    updateStardate();
                }
            });
            timePicker = (TimePicker) rootView.findViewById(R.id.timePicker);
            timePicker.setCurrentHour(START_HOUR);
            timePicker.setCurrentMinute(START_MINUTE);
            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    updateStardate();
                }
            });
            spinner = (Spinner) rootView.findViewById(R.id.spinner);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateStardate();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            editText = (EditText) rootView.findViewById(R.id.editText);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        updatePickers(v.getRootView());
                    }
                    return false;
                }
            });
            editText.setOnClickListener(new EditText.OnClickListener() {
                public void onClick(View view) {
                    InputMethodManager im = (InputMethodManager)(view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    im.toggleSoftInput(0, 0);
                }
            });

            setPickers(getSelectedCalendar());
            return rootView;
        }
    }
}
