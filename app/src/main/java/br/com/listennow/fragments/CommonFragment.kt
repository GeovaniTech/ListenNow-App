package br.com.listennow.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.listennow.R
import br.com.listennow.viewmodel.CommonViewModel
import com.google.android.material.snackbar.Snackbar
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

    fun showSnackBar (
        messageId: Int,
        anchorView: View? = null,
        duration: Int = Snackbar.LENGTH_SHORT
    ) {
        Snackbar.make(requireView(), messageId, duration)
            .setAnchorView(anchorView)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.background))
            .show()
    }

    abstract fun loadNavParams()
    abstract fun setViewListeners()
    abstract fun setViewModelObservers()
    abstract fun loadData()
}
