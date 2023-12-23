package br.com.listennow.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.database.dao.ClientDao
import br.com.listennow.databinding.RegisterBinding
import br.com.listennow.model.Client
import br.com.listennow.utils.EmailUtil
import br.com.listennow.utils.EncryptionUtil
import br.com.listennow.utils.StringUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity: AppCompatActivity() {
    private lateinit var binding: RegisterBinding
    private lateinit var clientDao: ClientDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        clientDao = AppDatabase.getInstance(this).clientDao()

        configButtonRegister()
    }

    private fun configButtonRegister() {
        binding.register.setOnClickListener {
            lifecycleScope.launch {
                register()
            }
        }
    }

    private fun register() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val repeatPassword = binding.repeatPassword.text.toString()

        if(!StringUtil.isNotNull(email)) {
            Toast.makeText(this, R.string.email_required, Toast.LENGTH_SHORT).show()
            return
        }

        if(!EmailUtil.isEmailValid(email)) {
            Toast.makeText(this, R.string.invalid_email, Toast.LENGTH_SHORT).show()
            return
        }

        if(!StringUtil.isNotNull(password)) {
            Toast.makeText(this, R.string.password_required, Toast.LENGTH_SHORT).show()
            return
        }

        if(!StringUtil.isNotNull(repeatPassword)) {
            Toast.makeText(this, R.string.repeat_password_required, Toast.LENGTH_SHORT).show()
            return
        }

        if(password != repeatPassword) {
            Toast.makeText(this, R.string.passwords_are_not_the_same, Toast.LENGTH_SHORT).show()
            return
        }

        val clientReturned = clientDao.existsAccount(email)

        Log.i("RegisterActivity", "Client returned: " + clientReturned?.email)

        if(clientReturned != null && clientReturned.email!!.isNotEmpty()) {
            Toast.makeText(this, R.string.email_already_in_use, Toast.LENGTH_SHORT).show()
            return
        }

        val client = Client(0, email, EncryptionUtil.encryptSHA(password))

        CoroutineScope(Dispatchers.IO).launch {
            clientDao.save(client)
        }

        finish()
    }
}