package com.example.ecommerceapp.ui.activities.main

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.ActivityMainBinding
import com.example.ecommerceapp.ui.activities.authentication.AuthenticationActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var i = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentMain) as NavHostFragment
        val navController = navHostFragment.navController
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.menu_bottom)
        binding.bottomBar.setupWithNavController(popupMenu.menu, navController)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.setColorFilter(
            ContextCompat.getColor(this, R.color.white),
            PorterDuff.Mode.SRC_ATOP
        )

        binding.toolbar.setTitleTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> supportActionBar?.hide()
                R.id.profileFragment -> {
                    supportActionBar?.hide()
                }
                else -> {
                    supportActionBar?.show()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    binding.toolbar.overflowIcon?.setColorFilter(ContextCompat.getColor(this@MainActivity, R.color.white), PorterDuff.Mode.SRC_IN)
                    title = "My Cart"
                }
            }
        }

        binding.bottomBar.onItemSelected = {
            when(it){
                0 -> {
                    i = 0
                    navController.navigate(R.id.homeFragment)
                }
                1 -> i = 1
                2 -> i = 2
            }
        }

    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        if(i == 0){
            finish()
        }
    }

}