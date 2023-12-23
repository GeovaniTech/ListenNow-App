package br.com.listennow.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.databinding.LoginBinding
import br.com.listennow.database.dao.ClientDao
import br.com.listennow.model.Client
import br.com.listennow.utils.EncryptionUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    private lateinit var clientDao: ClientDao

    private val loginKey = stringPreferencesKey("login")
    private val passwordKey = stringPreferencesKey("password")

    private val Context.dataStore by preferencesDataStore(
        name = "credentials"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        val view = binding.root

        clientDao = AppDatabase.getInstance(this).clientDao()

        configLinkRegister()
        configButtonLogin()

        requestPermission()
        loginWithExistentCredentials()
    }

    private fun configButtonLogin() {
        binding.button.setOnClickListener {
            val email = binding.editTextTextEmailAddress
            val password = binding.editTextTextPassword

            val client = Client(
                0,
                email.text.toString(),
                EncryptionUtil.encryptSHA(password.text.toString())
            )

            lifecycleScope.launch {
                login(client)
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

    private fun loginWithExistentCredentials() {
        lifecycleScope.launch {
            if (verifyIfExistsCredentials()) {
                loadHomeActivity()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        loginWithExistentCredentials()
    }

    private suspend fun login(client: Client) {
        if(client != null && client.email!!.isNotEmpty()) {
            val clientReturned = clientDao.authenticateClient(client.email.toString(), client.password.toString())

            if(clientReturned != null && !client.email.isNullOrBlank()) {
                saveCredentials(client)
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
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), 3)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        }
    }

    private suspend fun verifyIfExistsCredentials(): Boolean {
        val credentials = dataStore.data.first()

        if(credentials[loginKey] != null && credentials[passwordKey] != null) {
            val client = clientDao.authenticateClient(credentials[loginKey].toString(), credentials[passwordKey].toString())

            return client != null && client.email!!.isNotBlank()
        }

        return false
    }

    private suspend fun saveCredentials(client: Client) {
        dataStore.edit { credentials ->
            credentials[loginKey] = client.email.toString()
            credentials[passwordKey] = client.password.toString()
        }
    }
}