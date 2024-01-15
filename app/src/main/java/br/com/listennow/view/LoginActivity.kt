package br.com.listennow.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.database.dao.UserDao
import br.com.listennow.databinding.LoginBinding
import br.com.listennow.model.User
import br.com.listennow.preferences.dataStore
import br.com.listennow.preferences.userKey
import br.com.listennow.utils.EncryptionUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        supportActionBar?.hide()

        userDao = AppDatabase.getInstance(this).userDao()

        configLinkRegister()
        configButtonLogin()

        requestPermission()
    }

    private fun configButtonLogin() {
        binding.button.setOnClickListener {
            val email = binding.editTextTextEmailAddress
            val password = binding.editTextTextPassword

            val user = User(
                0,
                email.text.toString(),
                EncryptionUtil.encryptSHA(password.text.toString())
            )

            CoroutineScope(Dispatchers.IO).launch {
                login(user)
            }
        }
    }

    private fun configLinkRegister() {
        binding.goToRegister.setOnClickListener {
            startRegisterActivity()
        }
    }

    private fun startRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private suspend fun login(user: User) {
        if(user != null && user.email!!.isNotEmpty()) {
            val userReturned = userDao.authenticateUser(user.email.toString(), user.password.toString())

            if(userReturned != null && !user.email.isNullOrBlank()) {
                saveCredentials(user)
                loadHomeActivity()
            }
        } else {
            Toast.makeText(this, R.string.invalid_email_password, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), 3)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        }
    }

    private suspend fun saveCredentials(user: User) {
        dataStore.edit { credentials ->
            credentials[userKey] = user.id
        }
    }
}