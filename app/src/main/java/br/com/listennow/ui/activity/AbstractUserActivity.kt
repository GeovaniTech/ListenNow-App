package br.com.listennow.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import br.com.listennow.database.AppDatabase
import br.com.listennow.model.User
import br.com.listennow.preferences.dataStore
import br.com.listennow.preferences.userKey
import br.com.listennow.utils.SongUtil
import br.com.listennow.view.LoginActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

abstract class AbstractUserActivity : AppCompatActivity() {
    protected val userDao by lazy {
        AppDatabase.getInstance(this).userDao()
    }

    private var _user: MutableStateFlow<User?>  = MutableStateFlow(null)
    protected var user: StateFlow<User?>  = _user
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            verifyUserLogged()
        }
    }

    protected suspend fun findUserById(id: Long) {
        _user.value = userDao.findById(id).firstOrNull()
    }

    protected suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(userKey)
        }

        SongUtil.pause()
        startLoginActivity()
    }

    protected fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Close all activities
        startActivity(intent)
    }

    protected suspend fun verifyUserLogged() {
        dataStore.data.collect { preferences ->
            preferences[userKey]?.let {
                userId -> findUserById(userId)
            } ?: startLoginActivity()
        }
    }
}