package br.com.listennow.navparams

import java.io.Serializable

data class AlbumSongsNavParams(
    val album: String,
    val artist: String
): Serializable