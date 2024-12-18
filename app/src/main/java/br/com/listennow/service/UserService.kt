package br.com.listennow.service

import br.com.listennow.webclient.client.model.UserAddRequest
import br.com.listennow.webclient.common.model.CommonResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("user/add")
    suspend fun add(@Body userAddRequest: UserAddRequest): CommonResponse
}