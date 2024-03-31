package br.com.listennow.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.model.User
import br.com.listennow.utils.SongUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

abstract class AbstractUserFragment : Fragment() {
    private val Context.dataStore by preferencesDataStore(name = "credentials")
    private val userKey = stringPreferencesKey("userEmailKey")

    protected val userDao by lazy {
        AppDatabase.getInstance(requireContext()).userDao()
    }

    private val _user: MutableStateFlow<User?>  = MutableStateFlow(null)
    protected val user: StateFlow<User?>  = _user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
             //verifyUserLogged()
        }
    }

    private suspend fun findUserById(id: String): User? {
        return userDao.findById(id).firstOrNull().also {
            _user.value = it
        }
    }

    protected suspend fun logout() {
        requireContext().dataStore.edit { preferences ->
            preferences.remove(userKey)
        }

        SongUtil.pause()
        startLoginFragment()
    }

    private fun startLoginFragment() {
        findNavController().navigate(R.id.loginFragment)
    }

    protected suspend fun verifyUserLogged() {
        requireContext().dataStore.data.collect { preferences ->
            preferences[userKey]?.let {
                userId ->
                findUserById(userId.toString())
            } ?: startLoginFragment()
        }
    }

    protected suspend fun saveCredentials(id: String) {
        requireContext().dataStore.edit { credentials ->
            credentials[userKey] = id
        }
    }
}