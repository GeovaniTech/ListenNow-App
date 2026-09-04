package br.com.listennow.service

import br.com.listennow.webclient.log.model.InsertLogRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface LogService {
    @PUT(value = "log/create")
    suspend fun createLog(@Body createLogRequest: InsertLogRequest): Response<Void>
}