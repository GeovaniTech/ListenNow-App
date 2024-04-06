package br.com.listennow.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import br.com.listennow.BuildConfig
import br.com.listennow.R
import br.com.listennow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_ListenNow)

        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view_listennow) as NavHostFragment
        navController = navHostFragment.navController

        setUpBottomNavigation()
        //askPermissions()
    }

    private fun askPermissions() {
        val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")

        startActivity(
            Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                uri
            )
        )
    }

    private fun setUpBottomNavigation() {
        val menu = binding.playBackBottomNavigation
        menu.setupWithNavController(navController)
    }
}