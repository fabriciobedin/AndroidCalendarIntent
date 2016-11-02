package io.github.fabriciobedin.androidcalendarintent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePicker.OnTimeChangedListener, DialogInterface.OnCancelListener {

    int year_x, month_x, day_x, hour_x, minute_x;
    static final int DIALOG_ID = 0;
    TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        txtView = (TextView) findViewById(R.id.txtViewDataTime);


    }

    public void novoEvento(View view) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("title", "Some title");
        intent.putExtra("description", "health lifestyle");
        intent.putExtra("beginTime", System.currentTimeMillis());
        intent.putExtra("endTime", System.currentTimeMillis() + 100000);
        startActivity(intent);
    }

    public void chamaGoogleCalendar(View view) {
        long startMillis = System.currentTimeMillis();

        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        startActivity(intent);

    }

    public void calendarView(View view) {
        Intent intent = new Intent(this, ViewCalendario.class);
        startActivity(intent);
    }

    public void showDatePickerDialog(View view) {
        showDialog(DIALOG_ID);

    }

    @Override
    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_ID){
            return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);
        }
        return null;

    }

    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;

            Toast.makeText(getApplicationContext(), day_x + "/" + month_x + "/" + year_x, Toast.LENGTH_SHORT).show();


        }
    };

    private void novoEventoDatePicker(int day, int month, int year){

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("title", "Some title");
        intent.putExtra("description", "health lifestyle");
        intent.putExtra("beginTime", System.currentTimeMillis());
        intent.putExtra("endTime", System.currentTimeMillis() + 100000);
        startActivity(intent);
    }

    public void showDateTimePickerDialog(View view) {

        Calendar cDefault = Calendar.getInstance();
        initDateTimeData();
        cDefault.set(year_x, month_x, day_x);

        com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                (com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener) this,
                cDefault.get(Calendar.YEAR),
                cDefault.get(Calendar.MONTH),
                cDefault.get(Calendar.DAY_OF_MONTH)
        );

        Calendar cMin = Calendar.getInstance();
        Calendar cMax = Calendar.getInstance();

        cMax.set(Calendar.YEAR, 11, 31);
        datePickerDialog.setMinDate(cMin);
        datePickerDialog.setMaxDate(cMax);

        List<Calendar> daysList = new LinkedList<>();
        Calendar[] daysArray;
        Calendar cAux = Calendar.getInstance();

        while (cAux.getTimeInMillis() <= cMax.getTimeInMillis()){
            if(cAux.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cAux.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(cAux.getTimeInMillis());
                daysList.add(c);
            }
            cAux.setTimeInMillis(cAux.getTimeInMillis() + (24*60*60*1000));
        }
        daysArray = new Calendar[daysList.size()];
        for(int i = 0; i< daysArray.length; i++){
            daysArray[i] = daysList.get(i);
        }
        datePickerDialog.setSelectableDays(daysArray);
        datePickerDialog.setOnCancelListener(this);
        datePickerDialog.show(getFragmentManager(), "datePickerDialog");
    }

    private void initDateTimeData(){
        if(year_x ==0){
            Calendar c = Calendar.getInstance();
            year_x = c.get(Calendar.YEAR);
            month_x = c.get(Calendar.MONTH);
            day_x = c.get(Calendar.DAY_OF_MONTH);
            hour_x = c.get(Calendar.HOUR_OF_DAY);
            minute_x = c.get(Calendar.MINUTE);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar tDefault = Calendar.getInstance();
        tDefault.set(year_x, month_x, day_x, hour_x, minute_x);

        year_x = year;
        month_x = month;
        day_x = dayOfMonth;

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
            (TimePickerDialog.OnTimeSetListener) this,
            tDefault.get(Calendar.HOUR_OF_DAY),
            tDefault.get(Calendar.MINUTE),
            true
        );
        timePickerDialog.setOnCancelListener(this);
        timePickerDialog.show(getFragmentManager(), "timePickerDialog");

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        year_x = month_x = day_x = hour_x = minute_x = 0;
        txtView.setText("");

    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if( hourOfDay < 9 || hourOfDay > 18){
            onDateSet(null, year_x, month_x, day_x);
            Toast.makeText(this, "Somente entre 9 e 18 horas", Toast.LENGTH_LONG).show();
            return;
        }

        hour_x = hourOfDay;
        minute_x = minute;

        txtView.setText((day_x < 10 ? "0" + day_x : day_x)+"/"+
                ((month_x+1) < 10 ? "0" + (month_x+1) : (month_x+1))+"/"+
                year_x + " às " +
                (hour_x < 10 ? "0" + hour_x : hour_x)+"h"+
                (minute_x < 10 ? "0" + minute_x : minute_x)
        );

    }
}
