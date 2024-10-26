package br.com.listennow.webclient.user.service

import android.util.Log
import br.com.listennow.model.User
import br.com.listennow.service.UserService
import br.com.listennow.webclient.user.model.UserRequest

class UserWebClient(
    private val userService: UserService
) {
    companion object {
        const val TAG = "UserWebClient"
    }

    suspend fun save(user: User): Boolean {
        try {
            val response = userService.save(UserRequest(user.id, user.email, user.password))

            if(response.body()?.message.equals("User saved successfully")) {
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user on API", )
        }

        return false
    }

    suspend fun login(user: User): Boolean {
        try {
            val response = userService.login(UserRequest(user.id, user.email, user.password))

            if(response.body()?.message.toString() != "Login is not valid") {
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error trying to authenticate user  $e" )
        }

        return false
    }
}