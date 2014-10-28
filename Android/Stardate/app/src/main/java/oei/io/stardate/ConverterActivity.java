package oei.io.stardate;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.inputmethod.EditorInfo;
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

        public static int year;
        public static int monthOfYear;
        public static int dayOfMonth;
        public static int hourOfDay;
        public static int minute;
        public static int timeZone;

        private static long getStartOfYear(int y) {
            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
            cal.set(Calendar.YEAR, y);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTimeInMillis();
        }

        public static void updateStardate(View rootView) {
            TimeZone tz;
            switch (PlaceholderFragment.timeZone) {
                case 0:
                    tz = TimeZone.getTimeZone("America/Los_Angeles");
                    break;
                case 1:
                    tz = TimeZone.getTimeZone("Asia/Kolkata");
                    break;
                case 2:
                    tz = TimeZone.getTimeZone("UTC");
                    break;
                default:
                    throw new RuntimeException("Unknown time zone");

            }
            Calendar cal = new GregorianCalendar(tz, Locale.ENGLISH);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long millis = cal.getTimeInMillis();
            double y0 = getStartOfYear(year);
            double y1 = getStartOfYear(year + 1);
            double sd = year + (millis - y0) / (y1 - y0);
            Log.d("stardate", "year = " + year + ", y0 = " + y0 + ", y1 = " + y1 + ", millis = " + millis + ", sd = " + sd);
            String x = String.format("%.15f", Double.valueOf(sd));

            ((EditText)rootView.findViewById(R.id.editText)).setText(x, TextView.BufferType.EDITABLE);
        }

        public static void updatePickers(View rootView) {
            TimeZone tz;
            switch (PlaceholderFragment.timeZone) {
                case 0:
                    tz = TimeZone.getTimeZone("America/Los_Angeles");
                    break;
                case 1:
                    tz = TimeZone.getTimeZone("Asia/Kolkata");
                    break;
                case 2:
                    tz = TimeZone.getTimeZone("UTC");
                    break;
                default:
                    throw new RuntimeException("Unknown time zone");

            }
            double y = Double.parseDouble(((EditText)rootView.findViewById(R.id.editText)).getText().toString());
            int y0 = (int) y;
            long t0 = getStartOfYear(y0);
            long t1 = getStartOfYear(y0 + 1);
            long millis = (long) (t0 + (t1 - t0) * (y - y0));
            Calendar cal = new GregorianCalendar(tz, Locale.ENGLISH);
            cal.setTimeInMillis(millis);
            DatePicker dp = (DatePicker) rootView.findViewById(R.id.datePicker);
            dp.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            TimePicker tp = (TimePicker) rootView.findViewById(R.id.timePicker);
            tp.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            tp.setCurrentMinute(cal.get(Calendar.MINUTE));
            PlaceholderFragment.year = cal.get(Calendar.YEAR);
            PlaceholderFragment.monthOfYear = cal.get(Calendar.MONTH);
            PlaceholderFragment.dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            PlaceholderFragment.hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
            PlaceholderFragment.minute = cal.get(Calendar.MINUTE);
        }

        public static class DateChangedListener implements DatePicker.OnDateChangedListener {
            public void onDateChanged(DatePicker dp, int year, int monthOfYear, int dayOfMonth) {
                PlaceholderFragment.year = year;
                PlaceholderFragment.monthOfYear = monthOfYear;
                PlaceholderFragment.dayOfMonth = dayOfMonth;
                Log.d("stardate", year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                updateStardate(dp.getRootView());
            }
        }

        public static class TimeChangedListener implements TimePicker.OnTimeChangedListener {
            public void onTimeChanged(TimePicker tp, int hourOfDay, int minute) {
                PlaceholderFragment.hourOfDay = hourOfDay;
                PlaceholderFragment.minute = minute;
                Log.d("stardate", hourOfDay + ":" + minute);
                updateStardate(tp.getRootView());
            }
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_converter, container, false);
            DatePicker dp = (DatePicker) rootView.findViewById(R.id.datePicker);
            dp.init(2014, 9, 28, new DateChangedListener());
            TimePicker tp = (TimePicker) rootView.findViewById(R.id.timePicker);
            tp.setCurrentHour(13);
            tp.setCurrentMinute(47);
            tp.setOnTimeChangedListener(new TimeChangedListener());
            Spinner sp = (Spinner) rootView.findViewById(R.id.spinner);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    PlaceholderFragment.timeZone = position;
                    Log.d("stardate", "Time zone " + position);
                    updateStardate(view.getRootView());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            EditText et = (EditText) rootView.findViewById(R.id.editText);
            et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        updatePickers(v.getRootView());
                    }
                    return false;
                }
            });
            return rootView;
        }
    }
}
