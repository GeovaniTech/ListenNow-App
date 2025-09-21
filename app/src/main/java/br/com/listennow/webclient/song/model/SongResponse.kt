package br.com.listennow.webclient.song.model

import br.com.listennow.converters.DateConverters
import br.com.listennow.model.Song
import java.time.LocalDateTime

class SongResponse(
    private val video_id: String,
    private val title: String,
    private val artist: String,
    private val album: String,
    private val lyrics: String,
    private val thumb: String,
    private val request_date: String
) {
    val song: Song get() = Song(
        videoId = video_id,
        name = title,
        artist = artist,
        album = album,
        thumb = thumb,
        path = "",
        lyrics = lyrics,
        requestDate = request_date
    )
}