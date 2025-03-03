package br.com.listennow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import br.com.listennow.foreground.Actions
import br.com.listennow.fragments.MainActivity
import br.com.listennow.receiver.enums.IntentEnums
import br.com.listennow.utils.SongUtil

class UpdateSongReceiver : BroadcastReceiver() {
    lateinit var mainActivity: MainActivity

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            IntentEnums.INTENT_UPDATE_SONG.toString() -> {
                mainActivity.configSongToolbar(SongUtil.actualSong!!, intent.getBooleanExtra(Actions.IS_PLAYING.toString(), true))
            }
            else -> {}
        }
    }
}