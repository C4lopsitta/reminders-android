package cc.atomtech.todo

import android.Manifest
import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.DownloadManager.Request
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {
   private lateinit var fabAdd: FloatingActionButton
   private lateinit var adapter: ReminderAdapter
   private lateinit var viewList: RecyclerView
   private lateinit var topbar: Toolbar
   private lateinit var chips: ChipGroup

   @TargetApi(Build.VERSION_CODES.KITKAT)
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)

      //init top bar and relative menu
      topbar = findViewById(R.id.topbar)
      setSupportActionBar(topbar)

      //setup database
      db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders")
         .allowMainThreadQueries()
         .addMigrations(migrationV1_2, migrationV1_3, migrationV2_3)
         .build()
      reminderDao = (db as AppDatabase).reminderDao()

      //init services
      clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
      notifManager = getReminderNotificationChannelService(getString(R.string.notif_reminder_key), getString(R.string.notif_reminder_desc))
      alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager


      //TEST NOTIFICATION\\
      val i = Intent(this, NotifReciever::class.java)
      val pendingIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_IMMUTABLE)
      val calendar = Calendar.getInstance()
      calendar.set(2023, Calendar.JULY,17, 11, 16, 0)
      i.putExtra("title", "What a fancy reminder")
      i.putExtra("desc", "Yeah what a fancy reminder notification")
      i.putExtra("id", 69420)

      alarmManager.setExact(
         AlarmManager.RTC_WAKEUP,
         calendar.timeInMillis,
         pendingIntent
      )


      //get shared preferences
      sharedPref = getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE)

      //prompt for notification permission
      val requestPermissionLauncher =
         registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
               Toast.makeText(this, "Got permission", Toast.LENGTH_LONG).show()
            } else {
               Toast.makeText(this, "Didnt get permission", Toast.LENGTH_LONG).show()
            }
         }

      when {
         ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
            //got permission
         }
         shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
            //TODO: Show view that explains why notifications are needed
            Toast.makeText(this, "Bish gimmie permission", Toast.LENGTH_LONG).show()
         }
         else -> {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
         }
      }

      //check if the app has already been launched before
      checkIfAppHasRunBefore()

      //init view elements
      fabAdd = findViewById(R.id.fab_new)
      viewList = findViewById(R.id.view_main)
      chips = findViewById(R.id.chips)

      //setup list
      viewList.layoutManager = LinearLayoutManager(this)
      getAndShowRemindersFromDb()

      //add listener for floating action button
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

   private fun getReminderNotificationChannelService(name:String, desc:String): NotificationManager{
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val mChannel = NotificationChannel(reminderNotificationChannelID, name, importance)
      mChannel.description = desc
      val notifManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
      notifManager.createNotificationChannel(mChannel)
      return notifManager
   }


   private fun checkIfAppHasRunBefore() {
      if(sharedPref.getBoolean("isFirstLaunch", true)) {
         with(sharedPref.edit()) {
            putBoolean("isFirstLaunch", false)
            apply()
         }
         //start first launch experience UI
         val intent = Intent(this, FirstInstallExperience::class.java)
         startActivity(intent)

//            val notif = NotificationCompat.Builder(this, reminderNotificationChannelID)
//                .setSmallIcon(R.drawable.add_icon)
//                .setContentTitle("Hello world")
//                .setContentText("This is a notification!")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setStyle(NotificationCompat.InboxStyle())
//
//            if (notifManager.areNotificationsEnabled()) {
//                with(NotificationManagerCompat.from(this)) {
//                    notify(1, notif.build())
//                }
//            }
      }
   }


}
