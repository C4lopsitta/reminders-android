package cc.atomtech.todo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

@SuppressLint("UseSwitchCompatOrMaterialCode")
class AddReminder : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var reminderTitle: TextInputEditText
    private lateinit var layoutReminderTitle: TextInputLayout
    private lateinit var reminderBody: TextInputEditText
    private lateinit var layoutReminderBody: TextInputLayout
    private lateinit var cancel: Button
    private lateinit var topbar: Toolbar
    private lateinit var getNotifSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)
        topbar = findViewById(R.id.topbar)
        topbar.title = getString(R.string.desc_add)
        setSupportActionBar(topbar)

        reminderTitle = findViewById(R.id.reminder_title)
        layoutReminderTitle = findViewById(R.id.layout_reminder_title)
        reminderBody = findViewById(R.id.reminder_body)
        layoutReminderBody = findViewById(R.id.layout_reminder_body)
        btnSave = findViewById(R.id.btn_reminder_save)
        cancel = findViewById(R.id.btn_cancel)
        getNotifSwitch = findViewById(R.id.getnotif_switch)

        btnSave.setOnClickListener {
            val title: String = reminderTitle.text.toString()
            val body: String = reminderBody.text.toString()
            addReminder(title, body, getNotifSwitch.isChecked)
        }

        cancel.setOnClickListener {
            returnToMainActivity()
        }
        reminderTitle.isFocusableInTouchMode = true
        reminderTitle.requestFocus()
    }


    private fun addReminder(editTitle: String?, editBody: String?, getNotification: Boolean) {
        if (editTitle.isNullOrEmpty()){
            layoutReminderTitle.error = getString(R.string.err_title_required)
            return
        }
        if (editTitle.length > 128){
            layoutReminderTitle.error = getString(R.string.err_title_too_long)
            return
        }
        lifecycleScope.launch {
            val reminder = Reminder(0, editBody, false, editTitle, getNotification)
            reminderDao.addReminder(reminder)
        }
        returnToMainActivity()
    }

    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}