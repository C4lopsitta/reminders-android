package cc.atomtech.todo

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class Settings: AppCompatActivity() {
    private lateinit var topbar: MaterialToolbar;
    private lateinit var filterDropdown: Spinner;
    private lateinit var exportButton: Button;

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        topbar = findViewById(R.id.topbar);
        topbar.title = getString(R.string.settings);
        setSupportActionBar(topbar);

        filterDropdown = findViewById(R.id.settings_dropdown_default_filter);
        exportButton = findViewById(R.id.settings_export);

        exportButton.setOnClickListener { Export.export(null); }

        var defaultFilter = Filters.getEnumFromChipId(SharedPreferences.getInt(getString(R.string.default_filter))).ordinal;
        filterDropdown.setSelection(defaultFilter);

        filterDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                SharedPreferences.putInt(getString(R.string.default_filter), Filters.getEnumFromOrdinal(filterDropdown.selectedItemPosition).getChipId());
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
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
