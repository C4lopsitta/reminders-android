package cc.atomtech.todo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotifReciever : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notif = NotificationCompat.Builder(context, reminderNotificationChannelID)
            .setContentTitle(intent.getStringExtra("title"))
            .setSmallIcon(R.drawable.checked_icon_circle)
            .setContentText(intent.getStringExtra("desc"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notifManager.notify(intent.getIntExtra("id", -0), notif)
    }
}