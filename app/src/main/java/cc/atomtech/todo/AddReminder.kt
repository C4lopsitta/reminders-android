package cc.atomtech.todo

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.Calendar

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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        topbar = findViewById(R.id.topbar);
        topbar.title = getString(R.string.desc_add);
        setSupportActionBar(topbar);


        val currentTime = Calendar.getInstance();

        var calendar = Calendar.getInstance();
        calendar.set(1970, 0, 1, 0, 0);

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

        cancel.setOnClickListener {
            returnToMainActivity();
        }

        reminderDate.setText("${currentTime.get(Calendar.DAY_OF_MONTH)}/" +
              "${currentTime.get(java.util.Calendar.MONTH)}/${currentTime.get(java.util.Calendar.YEAR)}");

        reminderTime.setText("${currentTime.get(Calendar.HOUR_OF_DAY)}:${currentTime.get(Calendar.MINUTE)}");

        reminderDate.setOnClickListener {
            DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    calendar.set(year, monthOfYear, dayOfMonth);
                    reminderDate.setText("${dayOfMonth}/${monthOfYear}/${year}");
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
                    reminderTime.setText("${hourOfDay}:${minute}");
                },
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                true).show();
        }

        reminderTitle.isFocusableInTouchMode = true
        reminderTitle.requestFocus()
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

        var timeInMillis: Long = 0

        lifecycleScope.launch {
            val reminder = Reminder(0, editBody, false, editTitle, getNotification, timeInMillis.toInt());
            reminderDao.addReminder(reminder)
        }

        if(calendar.get(Calendar.YEAR) != 1970) {
            timeInMillis = calendar.timeInMillis;
            if(getNotification) {
                Notifier.setNotification(this,0, editTitle, editBody?: "", calendar);
                Log.i("NOTIFICATION_ADD_REMINDER", "Notificaiton set");
            }
        } else {
            timeInMillis = -1;
        }

        returnToMainActivity()
    }

    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


}