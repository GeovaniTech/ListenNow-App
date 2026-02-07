package br.com.listennow.webclient.client.service

import android.util.Log
import br.com.listennow.service.UserService
import br.com.listennow.webclient.client.model.UserAddRequest
import br.com.listennow.webclient.enums.StatusMessage

class UserWebClient(
    private val userService: UserService
) {
    companion object {
        const val TAG = "UserWebClient"
    }

    suspend fun addUser(userId: String): String {
        return try {
            val response = userService.add(UserAddRequest(userId))
            response.message
        } catch (e: Exception) {
            Log.e(TAG, "addUser: Error trying to add user to Server ${e.message}")
            return StatusMessage.FAILED_TO_CONNECT_API.message
        }
    }
}