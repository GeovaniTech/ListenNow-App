package br.com.listennow.service

import br.com.listennow.webclient.song.model.SearchDownloadSongRequest
import br.com.listennow.webclient.song.model.SearchYTSongRequest
import br.com.listennow.webclient.song.model.SearchYTSongResponse
import br.com.listennow.webclient.song.model.SongDownloadRequest
import br.com.listennow.webclient.song.model.SongDownloadResponse
import br.com.listennow.webclient.song.model.SongRequest
import br.com.listennow.webclient.song.model.SongResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SongService {
    @POST("user/songs")
    suspend fun getAll(@Body songRequest: SongRequest): List<SongResponse>

    @POST("songs/file")
    suspend fun getSongFile(@Body songDownloadRequest: SongDownloadRequest): SongDownloadResponse

    @POST("songs/search")
    suspend fun getYTSongs(@Body searchYTSongRequest: SearchYTSongRequest): List<SearchYTSongResponse>

    @POST("download/song")
    suspend fun downloadSong(@Body searchDownloadSongRequest: SearchDownloadSongRequest): Response<Void>

    @POST("songs/find")
    suspend fun findSongById(@Body searchSongDownloadRequest: SongDownloadRequest): SongResponse?
}