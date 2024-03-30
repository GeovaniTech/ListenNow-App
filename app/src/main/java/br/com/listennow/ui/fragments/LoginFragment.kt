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
import br.com.listennow.databinding.FragmentLoginBinding
import br.com.listennow.model.User
import br.com.listennow.repository.user.UserRepository
import br.com.listennow.utils.EncryptionUtil
import br.com.listennow.webclient.user.service.UserWebClient
import kotlinx.coroutines.launch

class LoginFragment : AbstractUserFragment() {
    private lateinit var binding: FragmentLoginBinding

    private val repository by lazy {
        UserRepository(userDao, UserWebClient())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configLinkRegister()
        configBtnLogin()
    }

    private fun configLinkRegister() {
        binding.linkRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }

    private fun configBtnLogin() {
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch {
                login()
            }
        }
    }

    private suspend fun login() {
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()

        val user = User("", email, EncryptionUtil.encryptSHA(password))

        if(repository.login(user)) {
            saveCredentials(user.email)
            findNavController().navigate(R.id.homeFragment)
        } else {
            Toast.makeText(requireContext(), R.string.invalid_email_password, Toast.LENGTH_SHORT).show()
        }
    }
}