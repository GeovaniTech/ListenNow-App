package br.com.listennow.service

import br.com.listennow.webclient.user.model.UserRequest
import br.com.listennow.webclient.user.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

public interface UserService {
    @POST("user/add")
    suspend fun save(@Body userRequest: UserRequest): Response<UserResponse>

    @POST("user/login")
    suspend fun login(@Body userRequest: UserRequest): Response<UserResponse>
}