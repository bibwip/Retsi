package com.retsi.dabijhouder

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.flask.colorpicker.BuildConfig
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.retsi.dabijhouder.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    private var shareIntent: Intent? = null
    private var prefs: SharedPreferences? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        importDataFromFirestore(DatabaseHelper(this))

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment
        navController = navHostFragment.findNavController()

        checkFirstRun()

        prefs = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.mainFragment, R.id.welcomeFragment))

        setSupportActionBar(binding.toolbar)
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
                navController.navigate(R.id.action_mainFragment_to_welcomeFragment)
//                val sendIntent = Intent.createChooser(shareIntent, null)
//                startActivity(sendIntent)
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

    override fun onPause() {
        super.onPause()
        if (Firebase.auth.currentUser != null){
            val db = DatabaseHelper(this)
            updateFirestoreData(db.opdrachten())
        } else  Log.d(TAG, "User not signed in")
    }


    private fun updateFirestoreData(items : ArrayList<OpdrachtItem>) {
        val uid = Firebase.auth.currentUser!!.uid

        val fireStoreDb = Firebase.firestore
        for (item in items){
            fireStoreDb.collection("users").document(uid).collection("items").document(item.id.toString()).set(item)
                .addOnFailureListener { Log.w(TAG, "Failed to upload data to firebase: ${it.message}") }
                .addOnSuccessListener { Log.d(TAG, "succesfully uploaded data to firebase") }
        }
    }

    companion object {
        const val apkURl =
            "https://drive.google.com/drive/folders/1lVLj9Ucl-RdRX9ABggtomy_8Hi8nsf1y?usp=sharing"
        const val PREF_VERSION_CODE_KEY = "version_code"
        const val DOESNT_EXIST = -1
        const val TAG = "registration"
    }

    override fun onBackPressed() {
        if (navController.currentDestination!!.id != R.id.welcomeFragment ) {
            super.onBackPressed()
        }
    }


    fun importDataFromFirestore(myDb:DatabaseHelper) {
        if (Firebase.auth.currentUser != null) {
            Log.d(TAG, "user is not null")
            Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid)
                .collection("subjects").get().addOnSuccessListener { documents ->
                    Log.d(TAG,"succesfully collected subjects from firestore")
                    val vakken = ArrayList<VakItem>()
                    for (document in documents){
                        Log.d(TAG, "before")
                        val vak = document.toObject<VakItem>()
                        Log.d(TAG, "after")
                        vakken.add(vak)
                    }

                    myDb.replaceVakken(vakken)

                }
                .addOnFailureListener { Log.w(TAG,"failed to collect subjects from firestore") }

            Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid)
                .collection("items").get().addOnSuccessListener { opdrachten ->
                    Log.d(TAG,"succesfully collected items from firestore")
                    for (opdrachtRaw in opdrachten) {
                        val opdracht = opdrachtRaw.toObject<OpdrachtItem>()
                        myDb.updateOpdracht(opdracht.id.toString(), opdracht.typeOpdracht,
                            opdracht.vakNaam, opdracht.titel, opdracht.datum, opdracht.beschrijving)
                    }
                }
                .addOnFailureListener { Log.w(TAG,"failed to collect items from firestore") }
        }
    }
}