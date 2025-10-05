package br.com.listennow.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import br.com.listennow.BuildConfig
import br.com.listennow.R
import br.com.listennow.databinding.ActivityMainBinding
import br.com.listennow.foreground.Actions
import br.com.listennow.foreground.SongPlayerService
import br.com.listennow.model.Song
import br.com.listennow.receiver.AppVersionUpdateReceiver
import br.com.listennow.receiver.HeadsetStateReceiver
import br.com.listennow.receiver.UpdateSongReceiver
import br.com.listennow.receiver.enums.IntentEnums
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.MainActivityViewModel
import br.com.listennow.webclient.appversion.model.LastVersionAvailableAppResponse
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<MainActivityViewModel>()
    private lateinit var mediaSession: MediaSessionCompat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_ListenNow)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view_listennow) as NavHostFragment

        setUpBottomNavigation(navHostFragment.navController)
        askPermissions()
        configPhoneDisconnectedReceiver()
        configUpdateSongOnViewReceiver()
        configPlayPauseListener()
        createDownloadSongNotificationChannel()
        createImportAllSongsForegroundServiceNotificationChannel()
        createSongPlayerNotificationChannel()
        setViewBindVariables()
        setViewModelObservers()
        onLoadLastSong()
        checkForAppUpdate()
        createMediaSession()
    }

    private fun createMediaSession() {
        mediaSession = MediaSessionCompat(this, "MediaSessionListenNow").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    startNotificationService(Actions.PLAY)
                }

                override fun onPause() {
                    startNotificationService(Actions.STOP)
                }

                override fun onSkipToNext() {
                    super.onSkipToNext()

                    startNotificationService(Actions.NEXT)
                }
            })

            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setActions(
                        PlaybackStateCompat.ACTION_PLAY or
                                PlaybackStateCompat.ACTION_PAUSE or
                                PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                    .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                    .build()
            )

            isActive = true
        }
    }

    private fun setViewBindVariables() {
        binding.mainActivityViewModel = viewModel
    }

    private fun onLoadLastSong() {
        viewModel.loadActualSong()
    }

    private fun setViewModelObservers() {
        viewModel.actualSong.observe(this) { song ->
            if (song != null) {
                configSongToolbar(song, SongUtil.isPlaying())
            } else {
                configEmptyToolbar()
            }
        }

        viewModel.newVersionAvailable.observe(this) { lastVersion ->
            if (lastVersion.first.get() && lastVersion.second != null) {
                showDialogInstallNewVersion(lastVersion)
            }
        }
    }

    private fun showDialogInstallNewVersion(lastVersion: Pair<AtomicBoolean, LastVersionAvailableAppResponse?>) {
        val version = lastVersion.second!!
        val dialogBuilder = AlertDialog.Builder(this)

        val positiveButtonClick = { dialog: DialogInterface, _: Int ->
            createDownloadRequest(version, dialog)
        }

        val negativeButtonClick = { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        with(dialogBuilder) {
            setTitle(getString(R.string.dialog_new_version_available_title))
            setMessage(
                getString(
                    R.string.dialog_new_version_available_message,
                    BuildConfig.VERSION_NAME,
                    lastVersion.second!!.name
                )
            )
            setPositiveButton(R.string.yes, DialogInterface.OnClickListener(positiveButtonClick))
            setNegativeButton(
                R.string.maybe_later,
                DialogInterface.OnClickListener(negativeButtonClick)
            )
            show()
        }
    }

    /**
     * Create the Download Notification Request
     */
    private fun createDownloadRequest(
        version: LastVersionAvailableAppResponse,
        dialog: DialogInterface
    ) {
        val request = DownloadManager.Request(Uri.parse(version.url))
        val subPath = "listennow-update-${version.name}.apk"

        request.setTitle(
            getString(R.string.download_new_version_apk, version.name)
        )

        request.setDescription(
            getString(R.string.new_version_is_being_donwloaded)
        )

        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, subPath)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        val receiver = AppVersionUpdateReceiver(
            context = this,
            downloadId = downloadId,
            newApkName = subPath
        )

        registerReceiver(
            receiver, IntentFilter(
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
        )

        dialog.dismiss()
    }

    private fun configPlayPauseListener() {
        binding.play.setOnClickListener {
            if(SongUtil.actualSong != null) {
                if (SongUtil.isPlaying()) {
                    startNotificationService(Actions.STOP)
                } else {
                    startNotificationService(Actions.PLAY)
                }
            }
        }
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
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }
    }

    private fun setUpBottomNavigation(navController: NavController) {
        val menu = binding.playBackBottomNavigation

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.to_right)
            .setExitAnim(R.anim.to_left)
            .setPopEnterAnim(R.anim.from_right)
            .setPopExitAnim(R.anim.from_left)
            .build()

        menu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment, null, navOptions)
                    true
                }

                R.id.searchNewSongsFragment -> {
                    navController.navigate(R.id.searchNewSongsFragment, null, navOptions)
                    true
                }

                R.id.deviceInfosFragment -> {
                    navController.navigate(R.id.deviceInfosFragment, null, navOptions)
                    true
                }

                R.id.playlistsFragment -> {
                    navController.navigate(R.id.playlistsFragment, null, navOptions)
                    true
                }

                else -> false
            }
        }
    }

    private fun configUpdateSongOnViewReceiver() {
        val receiver = UpdateSongReceiver()
        val receiverFilter = IntentFilter(IntentEnums.INTENT_UPDATE_SONG.toString())

        receiver.mainActivity = this

        registerReceiver(receiver, receiverFilter)
    }

    fun configSongToolbar(song: Song, isPlaying: Boolean) {
        binding.listSongsTitle.text = song.name
        binding.listSongsArtist.text = song.artist

        if (applicationContext != null) {
            Glide.with(applicationContext).load(song.thumb).into(binding.homeThumbSongDetails)
        }

        if (isPlaying) {
            binding.play.setBackgroundResource(R.drawable.ic_pause)
        } else {
            binding.play.setBackgroundResource(R.drawable.ic_play)
        }
    }

    fun configEmptyToolbar() {
        binding.listSongsTitle.text = getString(R.string.no_title)
        binding.listSongsArtist.text = getString(R.string.no_artist)
        binding.play.setBackgroundResource(R.drawable.ic_play)

        Glide.with(applicationContext).load(R.drawable.icon).into(binding.homeThumbSongDetails)
    }

    private fun configPhoneDisconnectedReceiver() {
        val receiver = HeadsetStateReceiver()
        val receiverFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

        receiver.button = binding.play
        receiver.executeAfterPausing = {
            startNotificationService(Actions.STOP)
        }

        registerReceiver(receiver, receiverFilter)
    }

    private fun createDownloadSongNotificationChannel() {
        val name = getString(R.string.app_name)
        val descriptionText = getString(R.string.download_started_notification)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel =
            NotificationChannel(DOWNLOAD_SONG_NOTIFICATION_CHANNEl, name, importance).apply {
                description = descriptionText
                setSound(null, null)
            }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createImportAllSongsForegroundServiceNotificationChannel() {
        val name = getString(R.string.app_name)
        val descriptionText = getString(R.string.import_all_songs_from_another_device)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel =
            NotificationChannel(IMPORT_ALL_SONGS_FOREGROUND_SERVICE_NOTIFICATION_CHANNEl, name, importance).apply {
                description = descriptionText
                setSound(null, null)
            }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createSongPlayerNotificationChannel() {
        val name = getString(R.string.app_name)
        val descriptionText = getString(R.string.song_player_notification)
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel =
            NotificationChannel(SONG_PLAYER_NOTIFICATION_CHANNEL, name, importance).apply {
                description = descriptionText
                setSound(null, null)
            }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun startNotificationService(action: Actions) {
        Intent().also {
            it.setClass(this, SongPlayerService::class.java)
            it.action = action.toString()

            startService(it)
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

    private fun checkForAppUpdate() {
        viewModel.checkAppUpdate()
    }

    companion object {
        const val DOWNLOAD_SONG_NOTIFICATION_CHANNEl = "DownloadSongNotification"
        const val IMPORT_ALL_SONGS_FOREGROUND_SERVICE_NOTIFICATION_CHANNEl = "ImportAllSongsForegroundServiceNotification"
        const val SONG_PLAYER_NOTIFICATION_CHANNEL = "SongPlayerNotificationChannel"
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaSession.release()
    }
}