package br.com.listennow.view

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import br.com.listennow.databinding.ProfileBinding
import br.com.listennow.ui.fragments.AbstractUserFragment
import kotlinx.coroutines.launch

class ProfileActivity: AbstractUserFragment() {
    private val binding by lazy {
        ProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = binding.root
        //setContentView(view)

        lifecycleScope.launch {
            configUserInformation()
        }

        configButtonLogout()
    }

    private suspend fun configUserInformation() {
        user.collect {user ->
            binding.vlUserId.text = user?.id.toString()
            binding.vlUserEmail.text = user?.email
        }
    }

    private fun configButtonLogout() {
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                logout()
            }
        }
    }
}