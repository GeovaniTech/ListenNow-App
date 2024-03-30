package br.com.listennow.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.databinding.FragmentRegisterBinding
import br.com.listennow.model.User
import br.com.listennow.repository.user.UserRepository
import br.com.listennow.utils.EmailUtil
import br.com.listennow.utils.EncryptionUtil
import br.com.listennow.utils.StringUtil
import br.com.listennow.webclient.user.service.UserWebClient
import kotlinx.coroutines.launch
import java.util.UUID

class RegisterFragment : AbstractUserFragment() {
    private lateinit var binding: FragmentRegisterBinding

    private val repository by lazy {
        UserRepository(userDao, UserWebClient())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configLinkLogin()
        configBtnRegister()
    }

    private fun configLinkLogin() {
        binding.linkLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun configBtnRegister() {
        binding.btnRegister.setOnClickListener {
            lifecycleScope.launch {
                register()
            }
        }
    }

    private suspend fun register() {
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()
        val repeatPassword = binding.repeatInputPassword.text.toString()

        if(!StringUtil.isNotNull(email)) {
            Toast.makeText(context, R.string.email_required, Toast.LENGTH_SHORT).show()
            return
        }

        if(!EmailUtil.isEmailValid(email)) {
            Toast.makeText(context, R.string.invalid_email, Toast.LENGTH_SHORT).show()
            return
        }

        if(!StringUtil.isNotNull(password)) {
            Toast.makeText(context, R.string.password_required, Toast.LENGTH_SHORT).show()
            return
        }

        if(!StringUtil.isNotNull(repeatPassword)) {
            Toast.makeText(context, R.string.repeat_password_required, Toast.LENGTH_SHORT).show()
            return
        }

        if(password != repeatPassword) {
            Toast.makeText(context, R.string.passwords_are_not_the_same, Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(UUID.randomUUID().toString(), email, EncryptionUtil.encryptSHA(password))

        if(repository.save(user)) {
            saveCredentials(user.id)
            findNavController().navigate(R.id.homeFragment)
            return
        }

        Toast.makeText(requireContext(), R.string.email_already_in_use, Toast.LENGTH_SHORT).show()
    }
}