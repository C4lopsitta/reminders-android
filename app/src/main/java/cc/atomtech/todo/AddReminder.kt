package cc.atomtech.todo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class AddReminder : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var reminderBody: TextInputEditText
    private lateinit var layoutReminderBody: TextInputLayout
    private lateinit var cancel: Button
    private lateinit var topbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)
        topbar = findViewById(R.id.topbar)
        topbar.title = getString(R.string.desc_add)
        setSupportActionBar(topbar)

        btnSave = findViewById(R.id.btn_reminder_save)
        reminderBody = findViewById(R.id.reminder_body)
        cancel = findViewById(R.id.btn_cancel)
        layoutReminderBody = findViewById(R.id.layout_reminder_body)

        btnSave.setOnClickListener {
            val body: String = reminderBody.text.toString()
            addReminder(body)
        }

        cancel.setOnClickListener {
            returnToMainActivity()
        }
        reminderBody.requestFocus()
    }


    private fun addReminder(editBody: String?) {
        if (editBody.isNullOrEmpty()){
            layoutReminderBody.error = "You must add a reminder"
            return
        }
        lifecycleScope.launch {
            val reminder = Reminder(0, editBody, false)
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