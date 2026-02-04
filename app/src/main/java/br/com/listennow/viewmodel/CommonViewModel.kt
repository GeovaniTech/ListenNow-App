package br.com.listennow.viewmodel

import androidx.lifecycle.ViewModel
import br.com.listennow.model.User
import br.com.listennow.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

abstract class CommonViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    var user: User? = null

    suspend fun loadUser() {
        user = userRepository.findUser()

        user?.let {
            Firebase.crashlytics.setUserId(it.id)
        }
    }

    suspend fun createUser(): Boolean {
        return userRepository.saveUser(User())
    }
}