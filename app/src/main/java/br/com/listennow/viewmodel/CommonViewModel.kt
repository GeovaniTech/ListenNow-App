package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.listennow.model.User
import br.com.listennow.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

abstract class CommonViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private var _exceptionMessage: MutableLiveData<String?> = MutableLiveData(null)
    val exceptionMessage: LiveData<String?> get() = _exceptionMessage

    private var _exceptionResMessage: MutableLiveData<Int?> = MutableLiveData(null)
    val exceptionResMessage: LiveData<Int?> get() = _exceptionResMessage

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

    /**
     * Sets or clear the exceptionMessage from UI
     */
     fun updateExceptionMessage(message:  String? = null) {
        _exceptionMessage.postValue(message)
    }

    /**
     * Sets or clear the exceptionMessage from UI sung in resource string
     */
    fun updateExceptionResMessage(resMessage:  Int? = null) {
        _exceptionResMessage.postValue(resMessage)
    }
}