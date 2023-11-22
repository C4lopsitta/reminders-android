package cc.atomtech.todo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.NOTIFICATION_SERVICE
import java.util.Calendar


class Notifier {
   companion object {
      public final const val REMINDER_CHANNEL_ID: String = "reminders";
      public final const val LOG_TAG: String = "NOTIFIER";

      private var notifManager: NotificationManager? = null;

      public fun notify(id: Int, notification: Notification) {
         notifManager?.notify(id, notification);
      }
      public fun getNotificationService(key: String, desc: String, pkgContext: Context) {
//         if(notifManager == null)
//            throw Exception("Notification manager has been already instantiated");
         this.notifManager = getReminderNotificationChannelService(pkgContext, key, desc);
      }
      public fun registerNotification(context: Context, id: Long, title: String, desc: String, date: Calendar) {
         val pendingIntent = getPendingIntent(context, id, title, desc);


         Log.i(LOG_TAG, "Notification content: $title - $desc " +
               "; ID :: $id; TIME ${date.timeInMillis}");

         try {
            NotifReciever.setExactAlarm(date.timeInMillis, pendingIntent);
         } catch (e: SecurityException) {
            e.printStackTrace();
         }
      }
      public fun unregisterNotification(context: Context, id: Long, title: String, desc: String) {
         NotifReciever.dropExactAlarm(getPendingIntent(context, id, title, desc));
         Log.i(LOG_TAG, "DROPPED Notification ID :: $id");
      }
      public fun getPendingIntent(context: Context, id: Long, title: String, desc: String): PendingIntent {
         var intent = Intent(context, NotifReciever::class.java);
         intent.putExtra("title", title)
            .putExtra("desc", desc)
            .putExtra("id", id);
         var pendingIntent = PendingIntent
            .getBroadcast(context, id.toInt(), intent, PendingIntent.FLAG_IMMUTABLE);
         return pendingIntent;
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
