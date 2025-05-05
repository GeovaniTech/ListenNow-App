package br.com.listennow.webclient.appversion.service

import android.util.Log
import br.com.listennow.service.AppVersionService
import br.com.listennow.webclient.appversion.model.LastVersionAvailableAppResponse

class AppVersionWebClient(
    private val appVersionService: AppVersionService
) {

    suspend fun getLatestVersion(): LastVersionAvailableAppResponse? {
        return try {
            appVersionService.getLatestVersion()
        } catch (e: Exception) {
            Log.e(TAG, "Error trying to get Latest Version App from API ${e.message}")
            null
        }
    }

    companion object {
        const val TAG = "AppVersionWebClient"
    }

}