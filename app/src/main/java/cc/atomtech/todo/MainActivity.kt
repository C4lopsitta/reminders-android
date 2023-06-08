package cc.atomtech.todo

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
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders").allowMainThreadQueries().build()
        //add in case of db crash fallbackToDestructiveMigration().
        reminderDao = (db as AppDatabase).reminderDao()

        fabAdd = findViewById(R.id.fab_new)
        viewList = findViewById(R.id.view_main)

        viewList.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val reminders = reminderDao.getReminders()
            showRemindersFromReminderList(reminders)
        }

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
                reminderDao.deleteAll()
            }
            R.id.open_settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showRemindersFromReminderList(reminders: List<Reminder>) {
        adapter = ReminderAdapter(reminders, this)
        viewList.adapter = adapter
    }
}
