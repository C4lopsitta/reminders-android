package cc.atomtech.todo

import android.content.SharedPreferences

class SharedPreferences {
   companion object {
      private var sharedPreferences: SharedPreferences? = null;

      public fun instantiate(sharedPreferences: SharedPreferences) {
         this.sharedPreferences = sharedPreferences;
      }

      public fun getBoolean(key: String, default: Boolean): Boolean? {
         return sharedPreferences?.getBoolean(key, default);
      }

      public fun getNotNullBoolean(key: String, default: Boolean): Boolean {
         val ret = this.getBoolean(key, default);
         if(ret == null) return false;
         return ret;
      }

      public fun putBoolean(key: String, value: Boolean) {
         with(sharedPreferences!!.edit()) {
            putBoolean(key, value);
            this.apply();
         }
      }

      public fun getString(key: String): String? {
         return sharedPreferences?.getString(key, "");
      }

      public fun putString(key: String, value: String) {
         with(sharedPreferences!!.edit()) {
            Companion.putString(key, value);
            this.apply();
         }
      }

      public fun getInt(key: String): Int? {
         return sharedPreferences?.getInt(key, -1);
      }

      public fun putInt(key: String, int: Int) {
         if(sharedPreferences == null) return;
         with(sharedPreferences!!.edit()) {
            putInt(key, int);
            apply();
         }
      }
   }
}