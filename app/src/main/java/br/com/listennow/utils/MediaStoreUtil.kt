package br.com.listennow.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore

/**
 * Class to control the audio files on user's device
 */
class MediaStoreUtil(val contentResolver: ContentResolver) {
    fun writeSong(fileName: String, bytes: ByteArray): String {
        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Audio.Media.MIME_TYPE, MIME_TYPE_SONGS)
            put(
                MediaStore.Audio.Media.RELATIVE_PATH,
                Environment.DIRECTORY_MUSIC + APP_DIRECTORY_SONGS
            )
        }

        val uri = contentResolver.insert(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            values
        ) ?: return ""


        contentResolver.openOutputStream(uri)?.use {
            it.write(bytes)
        }

        return uri.toString()
    }

    fun existsSong(fileName: String): Boolean {
        val projection = arrayOf(MediaStore.Audio.Media._ID)

        val selection = "${MediaStore.Audio.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            return cursor.count > 0
        }

        return false
    }

    fun getSongUri(fileName: String): Uri? {
        val projection = arrayOf(MediaStore.Audio.Media._ID)

        val selection = "${MediaStore.Audio.Media.DISPLAY_NAME} = ?"

        val selectionArgs = arrayOf(fileName)

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->

            if (cursor.moveToFirst()) {
                val id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(
                        MediaStore.Audio.Media._ID
                    )
                )

                return ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
            }
        }

        return null
    }

    companion object {
        const val APP_DIRECTORY_SONGS = "/listennow"
        const val MIME_TYPE_SONGS = "audio/mpeg"
    }
}