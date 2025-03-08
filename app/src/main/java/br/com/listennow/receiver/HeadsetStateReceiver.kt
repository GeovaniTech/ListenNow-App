package br.com.listennow.receiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.widget.Button
import br.com.listennow.R
import br.com.listennow.model.Song
import br.com.listennow.utils.SongUtil

class HeadsetStateReceiver : BroadcastReceiver() {
    lateinit var button: Button
    lateinit var executeAfterPausing: (() -> Unit)
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                SongUtil.pause()
                button.setBackgroundResource(R.drawable.ic_play)
                executeAfterPausing.invoke()
            }
        }
    }
}