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
         with(sharedPreferences?.edit()) {
            putBoolean(key, value);
            this?.apply();
         }
      }
   }
}