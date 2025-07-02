package br.com.listennow.decorator

data class PlaylistItemDecorator (
    val title: String,
    val artists: String? = null,
    val totalSongs: Int,
    val playlistId: String
)