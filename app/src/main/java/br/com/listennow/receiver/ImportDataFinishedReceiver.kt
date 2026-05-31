package br.com.listennow.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import br.com.listennow.R
import br.com.listennow.fragments.MainActivity
import br.com.listennow.receiver.enums.IntentEnums
import br.com.listennow.utils.NotificationUtil

class ImportDataFinishedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            IntentEnums.INTENT_IMPORT_FINISHED.toString() -> {
                val notification = NotificationCompat.Builder(
                    context,
                    MainActivity.IMPORT_ALL_SONGS_FOREGROUND_SERVICE_NOTIFICATION_CHANNEl
                )
                    .setContentTitle(context.getString(R.string.import_has_finished))
                    .setContentText(context.getString(R.string.import_has_finished_message))
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .build()

                val notificationId = NotificationUtil.getUniqueNotificationId()
                val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                notificationManager.notify(notificationId, notification)
            }
        }
    }
}