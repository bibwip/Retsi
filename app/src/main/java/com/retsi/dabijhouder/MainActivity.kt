package com.retsi.dabijhouder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
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

    private var importMenuItem: MenuItem? = null
    private var exportMenuItem: MenuItem? = null

    private var shareIntent: Intent? = null
    private lateinit var prefs: SharedPreferences

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        importVakkenFromFirestore(DatabaseHelper(this))

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment
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

        importMenuItem = menu.findItem(R.id.menu_action_dowload)
        exportMenuItem = menu.findItem(R.id.menu_action_upload)

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
            R.id.menu_action_upload -> {
                if (Firebase.auth.currentUser != null) {
                    updateFirestoreData(DatabaseHelper(this).opdrachten())
                } else navController.navigate(R.id.action_mainFragment_to_loginFragment)
                true
            }
            R.id.menu_action_dowload -> {
                if (Firebase.auth.currentUser != null) {
                    val dwldPref = prefs.getString(IMPORT_PREF_KEY, "none")
                    if (dwldPref == IMPORT_PREF) {
                        importDataFromFirestore(DatabaseHelper(this))
                        Log.d(TAG, "imported data from firestore using pref: $dwldPref")
                    } else if (dwldPref == OVERRIDE_PREF) {
                        overrideDataFromFirestore(DatabaseHelper(this))
                        Log.d(TAG, "overrode data from firestore using pref: $dwldPref")
                    } else {
                        createDownloadPopupWindow()
                        Log.d(TAG, "created popupWindow using pref key: $dwldPref")
                    }
                } else navController.navigate(R.id.action_mainFragment_to_loginFragment)
                true
            }
            else -> item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        }
    }

    private fun createDownloadPopupWindow() {
        val dialogBuilder = AlertDialog.Builder(this)
        val dwnldPopup = layoutInflater.inflate(R.layout.import_data_popup_window, null)

        val importBtn = dwnldPopup.findViewById(R.id.popup_import_changes_btn) as Button
        val overrideBtn = dwnldPopup.findViewById(R.id.popup_override_changes_btn) as Button
        val dialogCheckbox = dwnldPopup.findViewById(R.id.popup_set_default_checkbox) as CheckBox

        dialogBuilder.setView(dwnldPopup)
        val dialog = dialogBuilder.create()
        dialog.show()
        


        importBtn.setOnClickListener {
            importDataFromFirestore(DatabaseHelper(this))
            if (navController.currentDestination?.id == R.id.mainFragment) {
                val frag = supportFragmentManager.findFragmentById(R.id.mainFragment)
                frag!!.javaClass.getMethod("updateAdapData")
            }
            if (dialogCheckbox.isSelected) {
                prefs.edit().putString(IMPORT_PREF_KEY, IMPORT_PREF).apply()
                Log.d(TAG, "saved pref: $IMPORT_PREF")
            }
        }

        overrideBtn.setOnClickListener {
            overrideDataFromFirestore(DatabaseHelper(this))
            if (dialogCheckbox.isSelected) {
                prefs.edit().putString(IMPORT_PREF_KEY, OVERRIDE_PREF).apply()
                Log.d(TAG, "saved pref: $OVERRIDE_PREF")
            }
        }
    }

    private fun checkFirstRun() {
        // Get current version code
        val currentVersionCode = BuildConfig.VERSION_CODE

        // Get saved version code
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST)

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return
        } else if (savedVersionCode == DOESNT_EXIST || currentVersionCode > savedVersionCode) {
            navController.navigate(R.id.action_mainFragment_to_welcomeFragment)
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply()
    }

    override fun onPause() {
        super.onPause()
        if (Firebase.auth.currentUser != null) {
            val db = DatabaseHelper(this)
            updateFirestoreData(db.opdrachten())
        } else Log.d(TAG, "User not signed in")
    }


    private fun updateFirestoreData(items: ArrayList<OpdrachtItem>) {
        val uid = Firebase.auth.currentUser!!.uid

        val fireStoreDb = Firebase.firestore
        for (item in items) {
            fireStoreDb.collection("users").document(uid).collection("items")
                .document(item.id.toString()).set(item)
                .addOnFailureListener {
                    Log.w(
                        TAG,
                        "Failed to upload data to firebase: ${it.message}"
                    )
                }
                .addOnSuccessListener { Log.d(TAG, "succesfully uploaded data to firebase") }
        }
    }

    companion object {
        const val apkURl =
            "https://drive.google.com/drive/folders/1lVLj9Ucl-RdRX9ABggtomy_8Hi8nsf1y?usp=sharing"
        const val PREF_VERSION_CODE_KEY = "version_code"
        const val DOESNT_EXIST = -1
        const val TAG = "Main"
        const val IMPORT_PREF_KEY = "importPrefKey"
        const val IMPORT_PREF = "import"
        const val OVERRIDE_PREF = "override"
    }

    override fun onBackPressed() {
        if (navController.currentDestination!!.id != R.id.welcomeFragment) {
            super.onBackPressed()
        }
    }

    fun importVakkenFromFirestore(myDb: DatabaseHelper) {
        if (Firebase.auth.currentUser != null) {
            Log.d(TAG, "user is not null")
            Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid)
                .collection("subjects").get().addOnSuccessListener { documents ->
                    Log.d(TAG, "succesfully collected subjects from firestore")
                    for (document in documents) {
                        val vak = document.toObject<VakItem>()
                        myDb.updateVakkenData(vak.id, vak.vaknaam, vak.vakColor)
                    }
                }
                .addOnFailureListener { Log.w(TAG, "failed to collect subjects from firestore") }

        }
    }


    fun importDataFromFirestore(myDb:DatabaseHelper) {
        if (Firebase.auth.currentUser != null) {
            Log.d(TAG, "user is not null")

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

    fun overrideDataFromFirestore(myDb: DatabaseHelper) {
        if (Firebase.auth.currentUser != null) {

            Firebase.firestore.collection("users").document(Firebase.auth.currentUser!!.uid)
                .collection("items").get().addOnSuccessListener { opdrachten ->
                    Log.d(TAG,"succesfully collected items from firestore")
                    for (opdrachtRaw in opdrachten) {
                        val opdracht = opdrachtRaw.toObject<OpdrachtItem>()
                        val db = myDb.writableDatabase
                        db.execSQL("delete from "+ "opdracht_tabel")
                        myDb.insertData(opdracht.typeOpdracht,
                            opdracht.vakNaam, opdracht.titel, opdracht.datum, opdracht.beschrijving)
                    }
                }
                .addOnFailureListener { Log.w(TAG,"failed to collect items from firestore") }
        }
    }
}