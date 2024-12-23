package br.com.listennow.viewmodel

import br.com.listennow.navparams.ErrorNavParams
import br.com.listennow.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ErrorViewModel @Inject constructor(
    userRepository: UserRepository
) : CommonViewModel(userRepository) {
    lateinit var navParams: ErrorNavParams
}