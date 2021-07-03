package com.retsi.dabijhouder

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.flask.colorpicker.BuildConfig
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    private var shareIntent: Intent? = null
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment
        navController = navHostFragment.findNavController()

        checkFirstRun()

        prefs = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.mainFragment, R.id.welcomeFragment))

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        shareIntent = Intent()
        shareIntent!!.action = Intent.ACTION_SEND
        shareIntent!!.putExtra(Intent.EXTRA_TEXT, getString(R.string.deelText) + apkURl)
        shareIntent!!.type = "text/plain"
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    @SuppressLint("NonConstantResourceId")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                val sendIntent = Intent.createChooser(shareIntent, null)
                startActivity(sendIntent)
                true
            }
            else -> item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        }
    }

    private fun checkFirstRun() {
        // Get current version code
        val currentVersionCode = BuildConfig.VERSION_CODE

        // Get saved version code
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedVersionCode = prefs!!.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST)

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return
        } else if (savedVersionCode == DOESNT_EXIST || currentVersionCode > savedVersionCode) {
            navController.navigate(R.id.action_mainFragment_to_welcomeFragment)
        }

        // Update the shared preferences with the current version code
        prefs!!.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply()
    }

    companion object {
        const val apkURl =
            "https://drive.google.com/drive/folders/1lVLj9Ucl-RdRX9ABggtomy_8Hi8nsf1y?usp=sharing"
        const val PREF_VERSION_CODE_KEY = "version_code"
        const val DOESNT_EXIST = -1
    }

    override fun onBackPressed() {
        if (navController.currentDestination!!.id != R.id.welcomeFragment ) {
            super.onBackPressed()
        }
    }
}