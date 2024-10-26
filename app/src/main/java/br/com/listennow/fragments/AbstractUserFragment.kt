package br.com.listennow.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import br.com.listennow.database.AppDatabase
import br.com.listennow.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class AbstractUserFragment : Fragment() {
    private val Context.dataStore by preferencesDataStore(name = "credentials")
    private val userKey = stringPreferencesKey("userEmailKey")

    protected val userDao by lazy {
        AppDatabase.getInstance(requireContext()).userDao()
    }

    private val _user: MutableStateFlow<User?>  = MutableStateFlow(null)
    protected val user: StateFlow<User?>  = _user

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadNavParams()
        setViewListeners()
        setViewModelObservers()
        loadData()
    }

    abstract fun loadNavParams()
    abstract fun setViewListeners()
    abstract fun setViewModelObservers()
    abstract fun loadData()

}
