package br.com.listennow.fragments

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import br.com.listennow.R
import br.com.listennow.navparams.ErrorNavParams
import br.com.listennow.viewmodel.CommonViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

abstract class CommonFragment<ViewModel: CommonViewModel, DataBinding: ViewBinding>() : DialogFragment() {
    protected abstract val viewModel: ViewModel
    protected lateinit var binding: DataBinding

    protected val mainActivity by lazy {
        activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Avoid looping if user does not exist yet
        if (getLayout() != R.layout.fragment_error) {
            loadUser()
        }

        loadNavParams()
        setViewListeners()
        configView()
        loadData()
        setViewModelObservers()
        loadBindingVariables()
    }

    private fun loadUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadUser()

            if (viewModel.user == null && !viewModel.createUser()) {
                findNavController().navigate(ErrorFragmentDirections.actionGlobalToErrorFragment(
                    ErrorNavParams(
                        description = getString(R.string.message_error_creating_user),
                        retryAction = {
                            lifecycleScope.launch {
                                createUserNavigateBackHome()
                            }
                        }
                    )
                ))
            }
        }
    }

    private suspend fun createUserNavigateBackHome() {
        if (viewModel.createUser()) {
            mainActivity.showBottomMenuAndPlayButtons()
            findNavController().navigate(ErrorFragmentDirections.actionErrroFragmentToHomeFragment())
            return
        }
    }

    fun showSoftKeyboard(view: View) {
        view.requestFocus()
        WindowCompat.getInsetsController(activity?.window!!, view).show(WindowInsetsCompat.Type.ime())
    }

    fun showSnackBar (
        messageId: Int,
        anchorView: View? = mainActivity.binding.playBackButtons,
        duration: Int = Snackbar.LENGTH_SHORT
    ) {
        Snackbar.make(requireView(), messageId, duration)
            .setAnchorView(anchorView)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.background))
            .show()
    }

    fun showSnackBar (
        message: String,
        anchorView: View? = mainActivity.binding.playBackButtons,
        duration: Int = Snackbar.LENGTH_SHORT
    ) {
        Snackbar.make(requireView(), message, duration)
            .setAnchorView(anchorView)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.background))
            .show()
    }

    fun setFullWidth() {
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    abstract fun getLayout(): Int
    abstract fun loadNavParams()
    abstract fun setViewListeners()
    abstract fun setViewModelObservers()
    abstract fun loadData()
    open fun loadBindingVariables() {}
    open fun configView() {}
}
