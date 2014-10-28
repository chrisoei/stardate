package oei.io.stardate;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;
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

        public static class DateChangedListener implements DatePicker.OnDateChangedListener {
            public void onDateChanged(DatePicker dp, int year, int monthOfYear, int dayOfMonth) {
                Log.d("stardate", year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
            }
        }

        public static class TimeChangedListener implements TimePicker.OnTimeChangedListener {
            public void onTimeChanged(TimePicker tp, int hourOfDay, int minute) {
                Log.d("stardate", hourOfDay + ":" + minute);
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
                    Log.d("stardate", "Time zone " + position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            return rootView;
        }
    }
}
