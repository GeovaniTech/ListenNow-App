package br.com.listennow.fragments

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.listennow.BuildConfig
import br.com.listennow.R
import br.com.listennow.databinding.FragmentDeviceInfosBinding
import br.com.listennow.foreground.ImportAllSongsService
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.DeviceInfosViewModel
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

                if (songsIds == null) {
                    showSnackBar(R.string.message_it_was_not_possible_to_execute_this_action)
                    return@launch
                }

                if (songsIds.isEmpty()) {
                    showSnackBar(R.string.fragment_device_infos_no_songs_found_to_download_with_id)
                    return@launch
                }

                val dialogBuilder = AlertDialog.Builder(requireContext())

                val positiveButtonClick = { dialog: DialogInterface, _: Int ->
                    startImportSongsForegroundService(userReceiver, songsIds)
                    dialog.dismiss()
                }

                val negativeButtonClick = { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }

                with(dialogBuilder) {
                    setTitle(getString(R.string.dialog_download_songs_title))
                    setMessage(getString(R.string.dialog_download_songs_message, songsIds.size))
                    setPositiveButton(R.string.yes, DialogInterface.OnClickListener(positiveButtonClick))
                    setNegativeButton(R.string.no, DialogInterface.OnClickListener(negativeButtonClick))
                    show()
                }
            }
        }
    }

    private fun startImportSongsForegroundService(userReceiver: String, songsIds: List<String>) {
        Intent().also {
            it.setClass(requireContext(), ImportAllSongsService::class.java)

            it.putExtra(
                ImportAllSongsService.ImportAllSongsData.USER_RECEIVER.value,
                userReceiver
            )

            it.putStringArrayListExtra(ImportAllSongsService.ImportAllSongsData.SONGS_IDS.value, ArrayList(songsIds))

            mainActivity.startService(it)

            showSnackBar(getString(R.string.importing_all_songs_from_another_device, songsIds.size))
        }
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