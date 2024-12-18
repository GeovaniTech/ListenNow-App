package br.com.listennow.repository

import br.com.listennow.database.dao.UserDao
import br.com.listennow.model.User
import br.com.listennow.webclient.client.service.UserWebClient
import javax.inject.Inject

class UserRepository @Inject constructor (
    private val userDao: UserDao,
    private val userWebClient: UserWebClient
) {
    suspend fun findUser(): User? {
        return userDao.existsUserInDevice()
    }

    suspend fun saveUser(user: User) {
        userDao.save(user)
        userWebClient.addUser(user.id)
    }
}