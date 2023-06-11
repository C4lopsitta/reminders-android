package cc.atomtech.todo

import android.content.ClipboardManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: ReminderAdapter
    private lateinit var viewList: RecyclerView
    private lateinit var topbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        topbar = findViewById(R.id.topbar)
        setSupportActionBar(topbar)
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders")
            .allowMainThreadQueries()
            .addMigrations(migrationV1_2, migrationV1_3, migrationV2_3)
            .build()

        //add in case of db crash fallbackToDestructiveMigration().
        reminderDao = (db as AppDatabase).reminderDao()

        clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        fabAdd = findViewById(R.id.fab_new)
        viewList = findViewById(R.id.view_main)

        viewList.layoutManager = LinearLayoutManager(this)

        getAndShowRemindersFromDb()

        fabAdd.setOnClickListener {
            val intent = Intent(this, AddReminder::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> {
                Snackbar.make(viewList, getString(R.string.snack_deleteAll), Snackbar.LENGTH_LONG).setAction(getString(R.string.snack_undo)){
                }.addCallback(object: Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if(event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT){
                            reminderDao.deleteAll()
                            getAndShowRemindersFromDb()
                        }
                    }
                }).show()
            }
            R.id.open_settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getAndShowRemindersFromDb() {
        lifecycleScope.launch {
            val reminders = reminderDao.getReminders()
            showRemindersFromReminderList(reminders)
        }
    }
    private fun showRemindersFromReminderList(reminders: List<Reminder>) {
        adapter = ReminderAdapter(reminders, this)
        viewList.adapter = adapter
    }
}
