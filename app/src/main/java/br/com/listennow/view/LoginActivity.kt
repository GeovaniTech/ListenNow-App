package br.com.listennow.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.database.dao.UserDao
import br.com.listennow.databinding.LoginBinding
import br.com.listennow.model.User
import br.com.listennow.preferences.dataStore
import br.com.listennow.preferences.userKey
import br.com.listennow.ui.activity.AbstractUserActivity
import br.com.listennow.utils.EncryptionUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        LoginBinding.inflate(layoutInflater)
    }

    private val userDao by lazy {
        AppDatabase.getInstance(this).userDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root

        setContentView(view)

        supportActionBar?.hide()

        configLinkRegister()
        configButtonLogin()
        requestPermission()
    }

    private fun configButtonLogin() {
        binding.button.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            val password =  EncryptionUtil.encryptSHA(binding.editTextTextPassword.text.toString())

            authenticateUser(email, password)
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

    private fun authenticateUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            showInvalidCredentialsMessage()
            return
        }

        lifecycleScope.launch {
            val userReturned = userDao.authenticateUser(email, password).firstOrNull()

            if(userReturned != null) {
                saveCredentials(userReturned.id)
                startHomeActivity()
            } else {
                showInvalidCredentialsMessage()
            }
        }
    }

    private fun showInvalidCredentialsMessage() {
        Toast.makeText(this, R.string.invalid_email_password, Toast.LENGTH_SHORT).show()
    }

    private fun startHomeActivity() {
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

    private suspend fun saveCredentials(id: Long) {
        dataStore.edit { credentials ->
            credentials[userKey] = id
        }
    }
}