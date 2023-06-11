package cc.atomtech.todo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ShowReminder: AppCompatActivity() {
    private lateinit var topbar: Toolbar
    private lateinit var editTitle: TextInputEditText
    private lateinit var editTitleWrapper: TextInputLayout
    private lateinit var editBody: TextInputEditText
    private lateinit var editBodyWrapper: TextInputLayout
    private lateinit var save: Button
    private lateinit var close: Button
    private lateinit var getNotifSwitch: Switch

    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        topbar = findViewById(R.id.topbar)
        setSupportActionBar(topbar)

        editTitle = findViewById(R.id.reminder_title)
        editTitleWrapper = findViewById(R.id.layout_reminder_title)
        editBody = findViewById(R.id.reminder_body)
        editBodyWrapper = findViewById(R.id.reminder_body_layout)
        save = findViewById(R.id.btn_save_edits)
        close = findViewById(R.id.btn_cancel_edit)
        getNotifSwitch = findViewById(R.id.getnotif_switch)
        id = intent.getLongExtra("reminderID", 0)

        editTitle.setText(intent.getStringExtra("title"))
        editBody.setText(intent.getStringExtra("body"))
        getNotifSwitch.isChecked = intent.getBooleanExtra("getNotification", false)
        close.setOnClickListener {returnToMainActivity()}
        save.setOnClickListener{saveEdits()}
    }

    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
    private fun saveEdits(){
        val reminderTitle = editTitle.text.toString();
        val reminderBody = editBody.text.toString();
        if(reminderTitle.isNullOrEmpty()){
            editTitleWrapper.error = getString(R.string.err_title_required)
            return
        }
        if(reminderTitle.length > 128){
            editTitleWrapper.error = getString(R.string.err_title_too_long)
            return
        }
        reminderDao.updateBodyAndTitle(reminderTitle, reminderBody, id)
        reminderDao.updateGetNotification(getNotifSwitch.isChecked, id)

        returnToMainActivity()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                reminderDao.deleteById(id)
                returnToMainActivity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
