package br.com.listennow.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.listennow.R
import br.com.listennow.databinding.FragmentLoginBinding

class LoginFragment : AbstractUserFragment() {
    private lateinit var binding: FragmentLoginBinding
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
    }

    private fun configLinkRegister() {
        binding.linkRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }
}