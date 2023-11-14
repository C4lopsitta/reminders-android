package cc.atomtech.todo

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.Calendar

// TODO: Check if time/date is valid (in future)
// TODO: Visualize error if date is invalid
// TODO: Add predefined timers for reminders (in x minutes; today/tomorrow at hh:mm)

class AddReminder : AppCompatActivity() {
    private lateinit var btnSave: Button;
    private lateinit var reminderTitle: TextInputEditText;
    private lateinit var layoutReminderTitle: TextInputLayout;
    private lateinit var reminderBody: TextInputEditText;
    private lateinit var layoutReminderBody: TextInputLayout;
    private lateinit var cancel: Button;
    private lateinit var topbar: Toolbar;
    private lateinit var getNotifSwitch: Switch;
    private lateinit var reminderDate: AppCompatEditText;
    private lateinit var reminderTime: AppCompatEditText;

    private var calendar = Calendar.getInstance();

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        topbar = findViewById(R.id.topbar);
        topbar.title = getString(R.string.desc_add);
        topbar.navigationIcon = getDrawable(com.google.android.material.R.drawable.ic_arrow_back_black_24);
        topbar.navigationContentDescription = getString(R.string.back);
        setSupportActionBar(topbar);

        topbar.setNavigationOnClickListener { returnToMainActivity(); };

        reminderTitle = findViewById(R.id.reminder_title);
        layoutReminderTitle = findViewById(R.id.layout_reminder_title);
        reminderBody = findViewById(R.id.reminder_body);
        layoutReminderBody = findViewById(R.id.layout_reminder_body);
        btnSave = findViewById(R.id.btn_reminder_save);
        cancel = findViewById(R.id.btn_cancel);
        getNotifSwitch = findViewById(R.id.getnotif_switch);
        reminderDate = findViewById(R.id.reminder_date);
        reminderTime = findViewById(R.id.reminder_time);

        btnSave.setOnClickListener {
            val title: String = reminderTitle.text.toString();
            val body: String = reminderBody.text.toString();
            addReminder(title, body, getNotifSwitch.isChecked, calendar);
        }

        cancel.setOnClickListener { returnToMainActivity(); };

        setupTimeAndDateInputs();

        reminderTitle.isFocusableInTouchMode = true;
        reminderTitle.requestFocus();
    }



    private fun addReminder(editTitle: String?, editBody: String?, getNotification: Boolean, calendar: Calendar) {
        if (editTitle.isNullOrEmpty()){
            layoutReminderTitle.error = getString(R.string.err_title_required)
            return
        }
        if (editTitle.length > 128){
            layoutReminderTitle.error = getString(R.string.err_title_too_long)
            return
        }

        var timeInMillis: Long = 0;


        lifecycleScope.launch {
            var dbId: Long;
            val reminder = Reminder(0, editBody, false, editTitle, getNotification,
                                    timeInMillis.toInt(), Calendar.getInstance().timeInMillis);
            dbId = reminderDao.addReminder(reminder)

            timeInMillis = calendar.timeInMillis;
            if(getNotification) {
                Notifier.registerNotification(dbId, editTitle, editBody ?: "", calendar);
                Log.i("NOTIFICATION_ADD_REMINDER", "Notificaiton set with id $dbId for time ${calendar.timeInMillis}");
            }
        }



        returnToMainActivity()
    }

    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun prettifyMinute(minute: Int): String {
        var str = minute.toString();
        if(str.length == 1)
            str = "0${str}"
        return str;
    }

    private fun setupTimeAndDateInputs() {
        val currentTime = Calendar.getInstance();

        val dateString =
            "${currentTime.get(Calendar.DAY_OF_MONTH)}/" +
            "${currentTime.get(java.util.Calendar.MONTH) + 1}/" +
            "${currentTime.get(java.util.Calendar.YEAR)}";
        val timeString =
            "${currentTime.get(Calendar.HOUR_OF_DAY)}:" + prettifyMinute(calendar.get(Calendar.MINUTE));

        reminderDate.setText(dateString);
        reminderTime.setText(timeString);

        reminderDate.setOnClickListener {
            DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    calendar.set(year, monthOfYear, dayOfMonth);
                    reminderDate.setText("${dayOfMonth}/${monthOfYear + 1}/${year}");
                    getNotifSwitch.isChecked = true;
                },
                currentTime.get(Calendar.YEAR),
                currentTime.get(Calendar.MONTH),
                currentTime.get(Calendar.DAY_OF_MONTH)).show();
        }
        reminderTime.setOnClickListener {
            TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener {view, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    reminderTime.setText("${hourOfDay}:${prettifyMinute(minute)}");
                    getNotifSwitch.isChecked = true;
                },
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                true).show();
        }
    }
}