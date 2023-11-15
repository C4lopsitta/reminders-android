package cc.atomtech.todo

import android.Manifest
import android.app.AlarmManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {
   private lateinit var fabAdd: FloatingActionButton
   private lateinit var adapter: ReminderAdapter
   private lateinit var viewList: RecyclerView
   private lateinit var topbar: Toolbar
   private lateinit var chips: ChipGroup

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      //init top bar and relative menu
      topbar = findViewById(R.id.topbar);
      setSupportActionBar(topbar);

      //setup database
      db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders")
         .allowMainThreadQueries()
         .addMigrations(migrationV1_2, migrationV1_3, migrationV2_3, migrationV3_4, migrationV4_5)
         .build();
      reminderDao = (db as AppDatabase).reminderDao();

      //init services
      Clipboard.instantiate(getSystemService(CLIPBOARD_SERVICE) as ClipboardManager);
      NotifReciever.instantiateAlarmManager(getSystemService(Context.ALARM_SERVICE) as AlarmManager);
      SharedPreferences.instantiate(getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE));

      if(SharedPreferences.getBoolean("pms_mode", false) == true) {
         val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("pms");
         AppCompatDelegate.setApplicationLocales(appLocale);
         Toast.makeText(this, getString(R.string.reopen_to_pms), Toast.LENGTH_LONG);
      } else {
         val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("en, it, fr");
         AppCompatDelegate.setApplicationLocales(appLocale);
      }

      Notifier.getNotificationService(getString(R.string.notif_reminder_key),
         getString(R.string.notif_reminder_desc), this)

      //prompt for notification permission
      val requestPermissionLauncher =
         registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
               Toast.makeText(this, "Got permission", Toast.LENGTH_LONG).show();
            } else {
               Toast.makeText(this, "Didnt get permission", Toast.LENGTH_LONG).show();
            }
         }

      when {
         ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
            //got permission
         }
         shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
            // TODO: Show view that explains why notifications are needed
            Toast.makeText(this, "gimmie permission", Toast.LENGTH_LONG).show();
         }
         else -> {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
         }
      }

      //check if the app has already been launched before
      checkIfAppHasRunBefore();

      //init view elements
      fabAdd = findViewById(R.id.fab_new);
      viewList = findViewById(R.id.view_main);
      chips = findViewById(R.id.chips);

      //setup list
      viewList.layoutManager = LinearLayoutManager(this);

      var defaultChip: Filters = Filters.getEnumFromOrdinal(SharedPreferences.getInt(getString(R.string.default_filter)) ?: 0);
      findViewById<Chip>(defaultChip.getChipId()).isChecked = true;
      showRemindersBySelectedChip(defaultChip.getChipId());

      //add listener for floating action button
      fabAdd.setOnClickListener {
         val intent = Intent(this, AddReminder::class.java);
         startActivity(intent);
      }

      chips.setOnCheckedStateChangeListener(ChipGroup.OnCheckedStateChangeListener() { chipGroup: ChipGroup, ints: MutableList<Int> ->
         if(ints.size != 0) {
            val chip: Chip = chipGroup.findViewById(ints[0]);
            showRemindersBySelectedChip(chip.id);
         }
      })
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
   private fun showRemindersList(list: List<Reminder>) {
      lifecycleScope.launch {
         showRemindersFromReminderList(list);
      }
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
   private fun checkIfAppHasRunBefore() {
      if(SharedPreferences.getNotNullBoolean("isFirstLaunch", true)) {
         SharedPreferences.putBoolean("isFirstLaunch", false);
         //start first launch experience UI
         val intent = Intent(this, FirstInstallExperience::class.java)
         startActivity(intent)
      }
   }
   private fun showRemindersBySelectedChip(chip: Int) {
      when(chip) {
         R.id.showall -> lifecycleScope.launch {
            showRemindersList(reminderDao.getReminders());
         }
         R.id.showcompleted -> lifecycleScope.launch {
            showRemindersList(reminderDao.getCompletedReminders());
         }
         R.id.showtocomplete -> lifecycleScope.launch {
            showRemindersList(reminderDao.getUncompletedReminders());
         }
         R.id.showwithnotification -> lifecycleScope.launch {
            showRemindersList(reminderDao.getRemindersWithNotification());
         }
         R.id.showwithoutnotification -> lifecycleScope.launch {
            showRemindersList(reminderDao.getRemindersWithoutNotification());
         }
      }
   }
}









