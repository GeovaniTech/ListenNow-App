package br.com.listennow.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import br.com.listennow.R
import br.com.listennow.databinding.FragmentErrorBinding
import br.com.listennow.viewmodel.ErrorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ErrorFragment: CommonFragment<ErrorViewModel, FragmentErrorBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shimmerList.visibility = View.GONE
        mainActivity.hideBottomMenuAndPlayButtons()
    }

    override val viewModel: ErrorViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_error

    override fun loadNavParams() {
        viewModel.navParams = ErrorFragmentArgs.fromBundle(requireArguments()).navParams
    }

    override fun setViewListeners() {
        binding.fragmentErrorRetryButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    startShimmer()
                    viewModel.navParams.retryAction.invoke()
                    delay(2000)
                } finally {
                    stopShimmer()
                }
            }
        }
    }

    private fun startShimmer() {
        binding.shimmerList.visibility = View.VISIBLE

        binding.fragmentErrorImage.visibility = View.GONE
        binding.fragmentErrorTitleTextview.visibility = View.GONE
        binding.fragmentErrorDescriptionTextview.visibility = View.GONE
        binding.fragmentErrorRetryButton.visibility = View.GONE

        binding.shimmerList.startShimmer()
    }

    private fun stopShimmer() {
        binding.shimmerList.visibility = View.GONE
        binding.fragmentErrorImage.visibility = View.VISIBLE
        binding.fragmentErrorTitleTextview.visibility = View.VISIBLE
        binding.fragmentErrorDescriptionTextview.visibility = View.VISIBLE
        binding.fragmentErrorRetryButton.visibility = View.VISIBLE

        binding.shimmerList.stopShimmer()
    }

    override fun setViewModelObservers() {}

    override fun loadData() {
        binding.fragmentErrorDescriptionTextview.text = viewModel.navParams.description
    }
}