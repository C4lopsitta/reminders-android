package cc.atomtech.todo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.logging.Level
import java.util.logging.Logger

class NotifReciever : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notif = NotificationCompat.Builder(context, Notifier.REMINDER_CHANNEL_ID)
            .setContentTitle(intent.getStringExtra("title"))
            .setSmallIcon(R.drawable.checked_icon_circle)
            .setContentText(intent.getStringExtra("desc"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build();

        Log.i("NOTIFICATION_RECIEVER", "Test notification " +
              "content: ${intent.getStringExtra("title")}; ${intent.getStringExtra("desc")} " +
              "; ID :: ${intent.getIntExtra("id", 0)}")

        Notifier.notify(intent.getIntExtra("id", -0), notif)
    }
}