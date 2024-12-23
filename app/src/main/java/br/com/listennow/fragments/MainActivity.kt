package br.com.listennow.fragments

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import br.com.listennow.BuildConfig
import br.com.listennow.R
import br.com.listennow.databinding.ActivityMainBinding
import br.com.listennow.receiver.HeadsetStateReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_ListenNow)

        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_listennow) as NavHostFragment

        setUpBottomNavigation(navHostFragment.navController)
        askPermissions()
        configPhoneDisconnectedReceiver()
        createNotificationChannel()
    }

    private fun askPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.setData(
                        Uri.parse(
                            String.format(
                                "package:%s",
                                applicationContext.packageName
                            )
                        )
                    )
                    startActivity(intent)
                } catch (ex: Exception) {
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivity(intent)
                }
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100
                )
            }
        }
    }

    private fun setUpBottomNavigation(navController: NavController) {
        val menu = binding.playBackBottomNavigation
        menu.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.searchNewSongsFragment -> {
                    navController.navigate(R.id.searchNewSongsFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun configPhoneDisconnectedReceiver() {
        val receiver = HeadsetStateReceiver()
        val receiverFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

        receiver.button = binding.play

        registerReceiver(receiver, receiverFilter)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.download_started)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(DOWNLAOD_SONG_NOTIFICATION_CHANNEl, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showBottomMenuAndPlayButtons() {
        binding.playBackButtons.visibility = View.VISIBLE
        binding.playBackBottomNavigation.visibility = View.VISIBLE
    }

    fun hideBottomMenuAndPlayButtons() {
        binding.playBackButtons.visibility = View.GONE
        binding.playBackBottomNavigation.visibility = View.GONE
    }

    companion object {
        const val DOWNLAOD_SONG_NOTIFICATION_CHANNEl = "DownloadSongNotification"
    }
}