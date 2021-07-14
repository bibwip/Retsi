package com.retsi.dabijhouder

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val PREFS_NAME = "MyPrefsFile"
        const val LANGUAGE_KEY = "language"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val taal = prefs.getString(LANGUAGE_KEY, "en-us")

        val loginPref = findPreference<Preference>("pref_login")
        val logoutPref = findPreference<Preference>("pref_logout")

        if (Firebase.auth.currentUser != null){
            if (logoutPref != null && loginPref != null) {
                logoutPref.isVisible = true
                loginPref.isVisible = false
            }
        } else {
            if (logoutPref != null && loginPref != null) {
                logoutPref.isVisible = false
                loginPref.isVisible = true
            }
        }

        loginPref?.setOnPreferenceClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToLoginFragment()
            findNavController().navigate(action)
            true
        }

        logoutPref?.setOnPreferenceClickListener {
            Firebase.auth.signOut()
            val action = SettingsFragmentDirections.actionSettingsFragmentToMainFragment2()
            findNavController().navigate(action)
            true
        }

        val taalPref = findPreference<ListPreference>("pref_taal")
        taalPref!!.setDefaultValue(taal)

        taalPref.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->

                var lang = "en-us"
                when (newValue) {
                    getString(R.string.nederlands) -> lang = "nl"
                    getString(R.string.engels) -> lang = "en"
                }

                prefs.edit().putString(LANGUAGE_KEY, lang).apply()
                val context: Context = CustomContextWrapper.wrap(activity, lang)
                resources.updateConfiguration(
                    context.resources.configuration,
                    context.resources.displayMetrics
                )

                true }

        val vakkenPref = findPreference<Preference>("pref_vakken")
        vakkenPref!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToAddSubjectsFragment()
            findNavController().navigate(action)
            true
        }

    }
}