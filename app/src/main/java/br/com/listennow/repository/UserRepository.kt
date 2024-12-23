package br.com.listennow.repository

import br.com.listennow.database.dao.UserDao
import br.com.listennow.model.User
import br.com.listennow.webclient.client.service.UserWebClient
import br.com.listennow.webclient.enums.StatusMessage
import javax.inject.Inject

class UserRepository @Inject constructor (
    private val userDao: UserDao,
    private val userWebClient: UserWebClient
) {
    suspend fun findUser(): User? {
        return userDao.existsUserInDevice()
    }

    suspend fun saveUser(user: User): Boolean {
        val statusMessage = userWebClient.addUser(user.id)

        if (statusMessage == StatusMessage.FAILED_TO_CONNECT_API.message
            || statusMessage == StatusMessage.FAILED_TO_SAVE_USER.message) {
            return false
        }

        userDao.save(user)
        return true
    }
}