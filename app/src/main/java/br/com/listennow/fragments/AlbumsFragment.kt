package br.com.listennow.fragments

import androidx.fragment.app.viewModels
import br.com.listennow.R
import br.com.listennow.databinding.FragmentAlbumsBinding
import br.com.listennow.viewmodel.AlbumsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumsFragment : CommonFragment<AlbumsViewModel, FragmentAlbumsBinding>() {

    override val viewModel: AlbumsViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_albums

    override fun loadNavParams() = Unit

    override fun setViewListeners() {
        TODO("Not yet implemented")
    }

    override fun setViewModelObservers() {
        TODO("Not yet implemented")
    }

    override fun loadData() {
        TODO("Not yet implemented")
    }
}