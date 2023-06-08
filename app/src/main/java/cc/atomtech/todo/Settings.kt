package cc.atomtech.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class Settings: AppCompatActivity() {
    private lateinit var topbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        topbar = findViewById(R.id.topbar)
        topbar.title = getString(R.string.settings)
        setSupportActionBar(topbar)
    }
}