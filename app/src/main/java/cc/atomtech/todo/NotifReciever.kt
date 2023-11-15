package cc.atomtech.todo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.logging.Level
import java.util.logging.Logger

class NotifReciever : BroadcastReceiver() {
   override fun onReceive(context: Context, intent: Intent) {
      val id: Int = intent.getIntExtra("id", 0);
      val title: String? = intent.getStringExtra("title");
      val body: String? = intent.getStringExtra("desc");

      val tapIntent = Intent(context, ShowReminder::class.java).apply {
         flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK;
      }

      tapIntent.putExtra("id", id.toLong())
               .putExtra("title", title)
               .putExtra("body", body)
               .putExtra("getNotification", true);

      val tapPendingIntent = PendingIntent
         .getActivity(context, id, tapIntent, PendingIntent.FLAG_IMMUTABLE);

      val notif = NotificationCompat.Builder(context, Notifier.REMINDER_CHANNEL_ID)
         .setContentTitle(title)
         .setSmallIcon(R.drawable.checked_icon_circle)
         .setContentText(body)
         .setPriority(NotificationCompat.PRIORITY_DEFAULT)
         .setContentIntent(tapPendingIntent)
         .build();

      Log.i("NOTIFICATION_RECEIVER", "Test notification content: $title; $body; ID :: $id");

      Notifier.notify(id, notif);
   }

   companion object {
      private var alarmManager: AlarmManager? = null;

      public fun instantiateAlarmManager(manager: AlarmManager) {
         this.alarmManager = manager;
      }

      @Throws(SecurityException::class)
      public fun setExactAlarm(time: Long, pendingIntent: PendingIntent) {
         alarmManager?.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
      }

      public fun dropExactAlarm(pendingIntent: PendingIntent) {
         alarmManager?.cancel(pendingIntent);
      }
   }
}