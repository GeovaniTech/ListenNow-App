package br.com.listennow.service

import br.com.listennow.webclient.song.model.SongRequest
import br.com.listennow.webclient.song.model.SongResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SongService {
    @POST("user/songs")
    suspend fun getAll(@Body songRequest: SongRequest): List<SongResponse>
}