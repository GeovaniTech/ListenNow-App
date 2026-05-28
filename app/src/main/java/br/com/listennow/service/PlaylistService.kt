package br.com.listennow.service

import br.com.listennow.webclient.playlist.model.PlaylistCreateRequest
import br.com.listennow.webclient.playlist.model.PlaylistCreateResponse
import br.com.listennow.webclient.playlist.model.PlaylistDeleteRequest
import br.com.listennow.webclient.playlist.model.PlaylistDeleteSongsRequest
import br.com.listennow.webclient.playlist.model.PlaylistGetRequest
import br.com.listennow.webclient.playlist.model.PlaylistGetResponse
import br.com.listennow.webclient.playlist.model.PlaylistInsertSongsRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

interface PlaylistService {
    @PUT(value = "playlist/create")
    suspend fun create(@Body createRequest: PlaylistCreateRequest): Response<PlaylistCreateResponse>

    @HTTP(method = "DELETE", path = "playlist/delete", hasBody = true)
    suspend fun delete(@Body playlistDeleteRequest: PlaylistDeleteRequest): Response<Void>

    @PUT(value = "playlist/add/songs")
    suspend fun insertSongsIntoPlaylist(@Body insertSongsRequest: PlaylistInsertSongsRequest): Response<Void>

    @HTTP(method = "DELETE", path = "playlist/delete/songs", hasBody = true)
    suspend fun deleteSongsFromPlaylist(@Body deleteSongsRequest: PlaylistDeleteSongsRequest): Response<Void>

    @POST(value = "playlist/get")
    suspend fun getPlaylistsFromUser(@Body getPlaylistsRequest: PlaylistGetRequest): Response<List<PlaylistGetResponse>>
}