package br.com.listennow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import br.com.listennow.receiver.enums.IntentEnums

class ImportPlaylistSongsFinishedReceiver(
    private val onSyncComplete: () -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == IntentEnums.INTENT_IMPORT_PLAYLIST_SONGS_FINISHED.toString()) {
            onSyncComplete.invoke()
        }
    }
}