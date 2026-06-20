package br.com.listennow.fragments

import androidx.fragment.app.viewModels
import br.com.listennow.R
import br.com.listennow.databinding.FragmentTunerBinding
import br.com.listennow.viewmodel.TunerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TunerFragment : CommonFragment<TunerViewModel, FragmentTunerBinding>() {
    override val viewModel: TunerViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_tuner

    override fun loadNavParams() = Unit

    override fun setViewListeners() {
    }

    override fun setViewModelObservers() {
    }

    override fun loadData() {
    }
}