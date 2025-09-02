package com.example.cryptoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.example.cryptoapp.databinding.ActivityMainBinding

//Super Call: Calls the superclass's onCreate method.
//Binding Inflation: Inflates the ActivityMainBinding object using the layout inflater.
//Set Content View: Sets the content view to the root of the binding.
//Navigation Setup:
//Find Fragment: Finds the navHostFragment by its ID.
//NavController: Retrieves the NavController from the navHostFragment.
//PopupMenu: Creates a PopupMenu anchored to the SmoothBottomBar.
//Inflate Menu: Inflates the bottom navigation menu.
//Setup BottomBar: Sets up the SmoothBottomBar with the NavController using the PopupMenu

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null // Declare the binding object

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the binding object
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Set the content view to the root of the binding
        setContentView(binding!!.root)

        // Set up navigation with SmoothBottomBar
        // Find the navHostFragment by its ID
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        // Get the NavController from the navHostFragment
        val navController = navHostFragment!!.findNavController()

        // Create a PopupMenu and inflate the bottom navigation menu
        val popupMenu = PopupMenu(this, binding!!.bottomBar)
        popupMenu.inflate(R.menu.bottom_nav_menu)

        // Set up the SmoothBottomBar with the NavController
        binding!!.bottomBar.setupWithNavController(popupMenu.getMenu(), navController)
    }
}