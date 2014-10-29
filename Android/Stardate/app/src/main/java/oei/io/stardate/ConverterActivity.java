package oei.io.stardate;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
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

        public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
        public static final Locale LOCALE = Locale.ENGLISH;

        private static long getStartOfYear(int y) {
            Calendar cal = new GregorianCalendar(UTC, LOCALE);
            cal.set(Calendar.YEAR, y);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTimeInMillis();
        }

        protected static TimeZone getSelectedTimeZone() {
            switch (spinner.getSelectedItemPosition()) {
                case 0:
                    return TimeZone.getTimeZone("America/Los_Angeles");
                case 1:
                    return TimeZone.getTimeZone("Asia/Kolkata");
                case 2:
                    return UTC;
                default:
                    throw new RuntimeException("Unknown time zone");

            }
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
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long millis = cal.getTimeInMillis();
            double y0 = getStartOfYear(year);
            double y1 = getStartOfYear(year + 1);
            double sd = year + (millis - y0) / (y1 - y0);
            Log.d("stardate", "year = " + year + ", y0 = " + y0 + ", y1 = " + y1 + ", millis = " + millis + ", sd = " + sd);
            String x = String.format("%.15f", sd);

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
            long t1 = getStartOfYear(y0 + 1);
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
            datePicker.init(2014, 0, 1, new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    updateStardate();
                }
             });
            timePicker = (TimePicker) rootView.findViewById(R.id.timePicker);
            timePicker.setCurrentHour(1);
            timePicker.setCurrentMinute(0);
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
