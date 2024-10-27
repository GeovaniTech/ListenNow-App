package br.com.listennow.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.listennow.viewmodel.CommonViewModel
import kotlinx.coroutines.launch

abstract class CommonFragment<ViewModel: CommonViewModel>() : Fragment() {
    protected abstract val viewModel: ViewModel

    protected val mainActivity by lazy {
        activity as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUser()
        loadNavParams()
        setViewListeners()
        setViewModelObservers()
        loadData()
    }

    private fun loadUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadUser()
        }
    }

    fun showSoftKeyboard(view: View) {
        view.requestFocus()
        WindowCompat.getInsetsController(activity?.window!!, view).show(WindowInsetsCompat.Type.ime())
    }

    abstract fun loadNavParams()
    abstract fun setViewListeners()
    abstract fun setViewModelObservers()
    abstract fun loadData()
}
