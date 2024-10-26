package br.com.listennow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.listennow.viewmodel.CommonViewModel
import kotlinx.coroutines.launch

abstract class CommonFragment<ViewModel: CommonViewModel>() : Fragment() {
    protected abstract val viewModel: ViewModel

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

    abstract fun loadNavParams()
    abstract fun setViewListeners()
    abstract fun setViewModelObservers()
    abstract fun loadData()
}
