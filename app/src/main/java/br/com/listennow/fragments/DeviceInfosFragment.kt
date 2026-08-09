package br.com.listennow.fragments

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.DialogInterface
import android.content.IntentFilter
import android.os.Build
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import br.com.listennow.BuildConfig
import br.com.listennow.R
import br.com.listennow.databinding.FragmentDeviceInfosBinding
import br.com.listennow.receiver.ImportDataFinishedReceiver
import br.com.listennow.receiver.enums.IntentEnums
import br.com.listennow.utils.NetworkUtil
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.DeviceInfosViewModel
import br.com.listennow.workmanager.DataSyncFromUserWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeviceInfosFragment : CommonFragment<DeviceInfosViewModel, FragmentDeviceInfosBinding>() {
    override val viewModel: DeviceInfosViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_device_infos

    override fun loadNavParams() {}

    override fun setViewListeners() {
        mainActivity.binding.playBackButtons.setOnClickListener {
            if(SongUtil.actualSong != null && SongUtil.actualSong!!.videoId.isNotEmpty()) {
                findNavController().navigate(DeviceInfosFragmentDirections.actionDeviceInfosFragmentToSongDetailsFragment(
                    SongUtil.actualSong!!.videoId
                ))
            }
        }

        binding.fragmentDeviceInfosCopyImageview.setOnClickListener {
            val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(getString(R.string.copied_text), viewModel.userId.value)

            clipboardManager.setPrimaryClip(clipData)

            showSnackBar(R.string.fragment_device_infos_id_copied_successfully)
        }

        binding.fragmentDeviceInfosDownloadSongsImageview.setOnClickListener {
            if (NetworkUtil.isInternetAvailable(requireContext())) {
                val userReceiver = viewModel.userId.value!!
                val userWithSongs= binding.downloadDeviceId

                if (userWithSongs.isNullOrEmpty()) {
                    showSnackBar(R.string.fragment_device_infos_device_id_must_not_be_null)
                    return@setOnClickListener
                }

                if (userWithSongs == viewModel.userId.value) {
                    showSnackBar(R.string.fragment_Device_infos_device_id_must_be_different_than_yours)
                    return@setOnClickListener
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    val songsIds = viewModel.getSongIdsSongsByUser(userReceiver, userWithSongs)
                    val countPlaylistToImport = viewModel.getCountPlaylistsToImport(userWithSongs)

                    if (songsIds == null) {
                        showSnackBar(R.string.message_it_was_not_possible_to_execute_this_action)
                        return@launch
                    }

                    if (songsIds.isEmpty() && countPlaylistToImport == 0) {
                        showSnackBar(R.string.fragment_device_infos_no_songs_found_to_download_with_id)
                        return@launch
                    }

                    val dialogBuilder = AlertDialog.Builder(requireContext())

                    val positiveButtonClick = { dialog: DialogInterface, _: Int ->
                        startDataSyncFromUserWorker(userReceiver, userWithSongs, songsIds)
                        dialog.dismiss()
                    }

                    val negativeButtonClick = { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                    }

                    configImportFinishedReceiver()

                    with(dialogBuilder) {
                        setTitle(getString(R.string.dialog_download_songs_title))
                        setMessage(getString(R.string.dialog_download_songs_message, songsIds.size, countPlaylistToImport))
                        setPositiveButton(R.string.yes, DialogInterface.OnClickListener(positiveButtonClick))
                        setNegativeButton(R.string.no, DialogInterface.OnClickListener(negativeButtonClick))
                        show()
                    }
                }
            } else {
                viewModel.updateExceptionMessage(getString(R.string.check_internet_connection))
            }
        }
    }

    private fun configImportFinishedReceiver() {
        val receiver = ImportDataFinishedReceiver()
        val filter = IntentFilter(
            IntentEnums.INTENT_IMPORT_FINISHED.toString()
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            requireContext().registerReceiver(receiver, filter)
        }
    }

    private fun startDataSyncFromUserWorker(userReceiver: String, userWithData: String, songsIds: List<String>) {
        val request = OneTimeWorkRequestBuilder<DataSyncFromUserWorker>()
            .setInputData(
                workDataOf(
                    DataSyncFromUserWorker.ID_USER_RECEIVER to userReceiver,
                    DataSyncFromUserWorker.ID_USER_WITH_DATA to userWithData,
                    DataSyncFromUserWorker.SONGS_IDS to songsIds.toTypedArray()
                )
            )
            .setConstraints(
                Constraints(
                    requiredNetworkType = NetworkType.CONNECTED,
                    requiresBatteryNotLow = true
                )
            )
            .build()

        val workManager = WorkManager.getInstance(requireContext())
        workManager.enqueue(request)

        showSnackBar(getString(R.string.importing_all_songs_from_another_device))
    }


    override fun setViewModelObservers() {
        viewModel.userId.observe(viewLifecycleOwner) {
            binding.userId = it
        }
    }

    override fun loadData() {
        binding.fragmentDeviceInfosAppVersionTextview.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadUserId()
        }
    }
}