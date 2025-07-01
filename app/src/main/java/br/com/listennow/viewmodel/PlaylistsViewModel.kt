package br.com.listennow.viewmodel

import br.com.listennow.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    userRepository: UserRepository
): CommonViewModel(userRepository) {

}