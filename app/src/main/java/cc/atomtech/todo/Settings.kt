package cc.atomtech.todo

import android.os.Bundle
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class Settings: AppCompatActivity() {
    private lateinit var topbar: MaterialToolbar;
    private lateinit var filterDropdown: Spinner;

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        topbar = findViewById(R.id.topbar)
        topbar.title = getString(R.string.settings)
        setSupportActionBar(topbar)

        filterDropdown = findViewById(R.id.settings_dropdown_default_filter);

    }
}