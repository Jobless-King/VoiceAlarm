package com.galaxy.voicealarm;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.naverspeech.kfgd_naver.IManagerCommand;
import com.naver.naverspeech.kfgd_naver.NaverSpeechManager;
import com.stacktips.view.CalendarListener;
import com.stacktips.view.CustomCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Member of Data
    List<StringBuilder> memoList = new ArrayList<>();

    //Member of Widgets
    CustomCalendarView calendarView;
    EditText memoEdit;
    ImageButton memoBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize CustomCalendarView from layout
        calendarView = (CustomCalendarView) findViewById(R.id.calendar_view);
        InitializeCalender();

        //Memo
        memoEdit = (EditText)findViewById(R.id.memo_edit);
        memoBtn = (ImageButton)findViewById(R.id.memo_btn);
        //SetMemoFocus(false);
    }

    public void RunAlarmList(View view){
        Intent intent=new Intent(MainActivity.this, AlarmList.class);
        startActivity(intent);
    }

    public void OnResetFocus(View v){
        //SetMemoFocus(false);
        Log.i("msg", "OnResetFocus call");
    }

    private void SetMemoFocus(boolean bValue){
/*        if(bValue){
            memoEdit.setFocusable(true);
            memoBtn.setVisibility(View.VISIBLE);
            memoEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } else{
            memoEdit.setFocusable(false);
            memoBtn.setVisibility(View.INVISIBLE);
            InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        }*/
    }

    private void InitializeCalender() {

        //Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());

        //Show monday as first date of week
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        //Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false);

        //call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar);

        //Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                Toast.makeText(MainActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
                //SetMemoFocus(true);
            }

            @Override
            public void onMonthChanged(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
                Toast.makeText(MainActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
