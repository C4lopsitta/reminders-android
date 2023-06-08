package cc.atomtech.todo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ShowReminder: AppCompatActivity() {
    private lateinit var topbar: Toolbar
    private lateinit var editBody: TextInputEditText
    private lateinit var editBodyWrapper: TextInputLayout
    private lateinit var save: Button
    private lateinit var close: Button
    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        topbar = findViewById(R.id.topbar)
        setSupportActionBar(topbar)

        editBody = findViewById(R.id.reminder_body)
        editBodyWrapper = findViewById(R.id.reminder_body_layout)
        save = findViewById(R.id.btn_save_edits)
        close = findViewById(R.id.btn_cancel_edit)
        id = intent.getLongExtra("reminderID", 0)

        editBody.setText(intent.getStringExtra("body"))
        close.setOnClickListener {returnToMainActivity()}
        save.setOnClickListener{saveEdits()}
    }

    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
    private fun saveEdits(){
        val reminderBody = editBody.text.toString();
        if(reminderBody.isNullOrEmpty()) editBodyWrapper.error = "You must set a title"
        reminderDao.updateBody(reminderBody, id)

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
