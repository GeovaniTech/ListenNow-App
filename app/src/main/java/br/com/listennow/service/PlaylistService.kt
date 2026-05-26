package br.com.listennow.service

import br.com.listennow.webclient.playlist.model.PlaylistCreateRequest
import br.com.listennow.webclient.playlist.model.PlaylistCreateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface PlaylistService {
    @PUT(value = "playlist/create")
    suspend fun create(@Body createRequest: PlaylistCreateRequest): Response<PlaylistCreateResponse>
}