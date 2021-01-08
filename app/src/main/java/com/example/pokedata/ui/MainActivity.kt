package com.example.pokedata.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pokedata.R
import com.example.pokedata.firebase.Authentication
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.nav_drawer_header.*


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val auth = Authentication()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.pokedex
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        //Add logic to actions on the drawer.
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_logout -> {
                    auth.signOut()
                    true
                }
                else -> {
                    false
                }
            }
        }
        //If going to the pokedex destination, update the header email address.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.pokedex -> {
                    updateHeaderEmail()
                }
            }
        }
        updateHeaderEmail()
        //When auth state changes, automatically navigate to the splash screen.
        auth.authentication.addAuthStateListener { auth ->
            println(auth)
            updateHeaderEmail()
            if (!this.auth.isLoggedIn() && navController.currentDestination?.id != R.id.splashscreen) {
                drawerLayout.close()
                navController.navigate(R.id.splashscreen)
            }
        }
    }

    //Up navigation on non-top-level fragments
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun updateHeaderEmail() {
        tvHeaderEmail?.text = auth.getCurrentUser()?.email
    }
}