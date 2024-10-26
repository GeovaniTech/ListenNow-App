package br.com.listennow.viewmodel

import androidx.lifecycle.ViewModel
import br.com.listennow.model.User
import br.com.listennow.repository.UserRepository

abstract class CommonViewModel constructor(
    private val userRepository: UserRepository
): ViewModel() {
    lateinit var user: User

    suspend fun loadUser() {
        val userDB = userRepository.findUser()
        user = if (userDB == null) {
            userRepository.saveUser(User())
            userRepository.findUser()!!
        } else {
            userDB
        }
    }
}