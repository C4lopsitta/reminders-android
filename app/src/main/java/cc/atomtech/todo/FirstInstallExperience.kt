package cc.atomtech.todo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.ListResourceBundle

class FirstInstallExperience : AppCompatActivity() {
   lateinit var start: Button

   override fun onCreate(savedInstanceState: Bundle?){
      super.onCreate(savedInstanceState);
      setContentView(R.layout.first_launch_experience);

      start = findViewById(R.id.first_launch_start);

      SharedPreferences.putInt(getString(R.string.default_filter), Filters.ALL.ordinal);

      start.setOnClickListener {
         returnToMainActivity();
      }
   }

   private fun returnToMainActivity() {
      val intent = Intent(this, MainActivity::class.java);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
   }
}