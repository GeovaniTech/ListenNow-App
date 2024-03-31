package br.com.listennow.webclient.song.model

import br.com.listennow.model.Song

class SongResponse(
    val song_id: String,
    val video_id: String,
    val title: String,
    val artist: String,
    val album: String,
    val lyrics: String,
    val large_thumb: String,
    val small_thumb: String
) {
    val song: Song get() = Song(
        id = song_id,
        videoId = video_id,
        name = title,
        artist = artist,
        album = album,
        smallThumb = small_thumb,
        largeThumb = large_thumb,
        path = "",
        lyrics = lyrics,
        userId = null
    )
}