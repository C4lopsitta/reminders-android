package cc.atomtech.todo

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import kotlin.system.exitProcess

class Settings: AppCompatActivity() {
   private lateinit var topbar: MaterialToolbar;
   private lateinit var filterDropdown: Spinner;
   private lateinit var exportButton: Button;
   private lateinit var pmsModeCheckBox: CheckBox;

   override fun onCreate(savedInstanceState: Bundle?){
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_settings);
      topbar = findViewById(R.id.topbar);
      topbar.title = getString(R.string.settings);
      setSupportActionBar(topbar);

      filterDropdown = findViewById(R.id.settings_dropdown_default_filter);
      exportButton = findViewById(R.id.settings_export);
      pmsModeCheckBox = findViewById(R.id.settings_pms_mode);

      pmsModeCheckBox.isChecked = SharedPreferences.getBoolean("pms_mode", false) ?: false;

      exportButton.setOnClickListener { Export.export(null); }

      var defaultFilter = SharedPreferences.getInt(getString(R.string.default_filter));
      filterDropdown.setSelection(defaultFilter ?: 0);

      filterDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
         override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            SharedPreferences.putInt(getString(R.string.default_filter), filterDropdown.selectedItemPosition);
         }
         override fun onNothingSelected(p0: AdapterView<*>?) {}
      }

      pmsModeCheckBox.setOnCheckedChangeListener { compoundButton, b ->
         run {
            if (pmsModeCheckBox.isChecked) {
               SharedPreferences.putBoolean("pms_mode", true);
               Toast.makeText(this, getString(R.string.reopen_to_pms), Toast.LENGTH_LONG);
               exitProcess(0);
            } else {
               SharedPreferences.putBoolean("pms_mode", false);
               Toast.makeText(this, getString(R.string.reopen_to_exit_pms), Toast.LENGTH_LONG);
               exitProcess(0);
            }
         }
      }
   }
}

private class Export{
   companion object {
      public fun export(reminders: List<Reminder>?) {

      }

      private fun toJson() {

      }

      private fun toXML() {

      }
   }
}
