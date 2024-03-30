package br.com.listennow.webclient.user.service

import android.util.Log
import br.com.listennow.model.User
import br.com.listennow.webclient.RetrofitInitializer
import br.com.listennow.webclient.user.model.UserRequest

class UserWebClient {
    private val userService by lazy {
        RetrofitInitializer().userService
    }

    suspend fun save(user: User): Boolean {
        try {
            val response = userService.save(UserRequest(user.id, user.email, user.password))

            if(response.body()?.message.equals("User saved successfully")) {
                return true
            }
        } catch (e: Exception) {
            Log.e("UserWebService", "Error saving user on API", )
        }

        return false
    }
}