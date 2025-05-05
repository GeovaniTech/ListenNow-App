package br.com.listennow.service

import br.com.listennow.webclient.appversion.model.LastVersionAvailableAppResponse
import retrofit2.http.GET

interface AppVersionService {
    @GET("app/last/version")
    suspend fun getLatestVersion(): LastVersionAvailableAppResponse
}