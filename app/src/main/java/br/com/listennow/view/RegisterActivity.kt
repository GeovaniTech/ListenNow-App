package br.com.listennow.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.database.dao.UserDao
import br.com.listennow.databinding.RegisterBinding
import br.com.listennow.model.User
import br.com.listennow.utils.EmailUtil
import br.com.listennow.utils.EncryptionUtil
import br.com.listennow.utils.StringUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity: AppCompatActivity() {
    private lateinit var binding: RegisterBinding
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        userDao = AppDatabase.getInstance(this).userDao()

        configButtonRegister()
    }

    private fun configButtonRegister() {
        binding.register.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
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

        CoroutineScope(Dispatchers.IO).launch {
            val userReturned = userDao.existsAccount(email)

            Log.i("RegisterActivity", "User returned: " + userReturned?.email)

            if(userReturned != null && userReturned.email!!.isNotEmpty()) {
                Toast.makeText(this@RegisterActivity, R.string.email_already_in_use, Toast.LENGTH_SHORT).show()
            } else {
                val user = User(0, email, EncryptionUtil.encryptSHA(password))
                userDao.save(user)
            }

            finish()
        }
    }
}