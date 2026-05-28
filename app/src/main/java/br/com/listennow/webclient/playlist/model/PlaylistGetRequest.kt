package br.com.listennow.webclient.playlist.model

data class PlaylistGetRequest (
    val clientId: String,
    val ignoreIds: List<String>?
)