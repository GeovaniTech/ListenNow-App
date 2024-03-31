package br.com.listennow.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import br.com.listennow.R
import br.com.listennow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_ListenNow)

        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_listennow) as NavHostFragment
        navController = navHostFragment.navController

        setUpBottomNavigation()
    }

    private fun setUpBottomNavigation() {
        val menu = binding.playBackBottomNavigation
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
                menu.visibility = View.GONE
            } else {
                menu.visibility = View.VISIBLE
            }
        }

        menu.setupWithNavController(navController)
    }
}