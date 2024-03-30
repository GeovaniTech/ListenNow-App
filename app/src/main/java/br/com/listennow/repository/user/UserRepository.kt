package br.com.listennow.repository.user

import br.com.listennow.database.dao.UserDao
import br.com.listennow.model.User
import br.com.listennow.webclient.user.service.UserWebClient

class UserRepository(private val userDao: UserDao, private val webClient: UserWebClient) {
    suspend fun save(user: User): Boolean {
        return webClient.save(user)
    }
}