package cc.atomtech.todo

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.NOTIFICATION_SERVICE
import java.util.Calendar

//lateinit var notifManager: NotificationManager

class Notifier {
   companion object {
      public const val REMINDER_CHANNEL_ID: String = "reminders";

      private var notifManager: NotificationManager? = null;
      private lateinit var context: Context;


      public fun notify(id: Int, notification: Notification) {
         notifManager?.notify(id, notification);
      }

      public fun getNotificationService(key: String, desc: String, pkgContext: Context) {
//         if(notifManager == null)
//            throw Exception("Notification manager has been already instantiated");
         this.notifManager = getReminderNotificationChannelService(pkgContext, key, desc);
         context = pkgContext;
      }
      public fun registerNotification(id: Int, title: String, desc: String, date: Calendar) {
         registerNotification(this.context, id, title, desc, date);
      }
      public fun registerNotification(context: Context, id: Int, title: String, desc: String, date: Calendar) {
         var intent = Intent(context, NotifReciever::class.java);
         intent.putExtra("title", title)
               .putExtra("desc", desc)
               .putExtra("id", id);
         var pendingIntent = PendingIntent
            .getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

         Log.i("NOTIFIER", "Test notification content: $title - $desc " +
               "; ID :: ${id}; TIME ${date.timeInMillis}");

         try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.timeInMillis, pendingIntent);
         } catch (e: SecurityException) {
            e.printStackTrace();
         }
      }

      private fun getReminderNotificationChannelService(context: Context, name: String, desc: String): NotificationManager {
         val importance = NotificationManager.IMPORTANCE_DEFAULT
         val mChannel = NotificationChannel(Notifier.REMINDER_CHANNEL_ID, name, importance)
         mChannel.description = desc
         val notifManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
         notifManager.createNotificationChannel(mChannel)
         return notifManager
      }
   }


}
