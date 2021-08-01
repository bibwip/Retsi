package com.retsi.dabijhouder

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val PREFS_NAME = "MyPrefsFile"
        const val LANGUAGE_KEY = "language"
        const val THEME_KEY = "theme"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val taal = prefs.getString(LANGUAGE_KEY, "en-us")

        val taalPref = findPreference<ListPreference>("taal")
        taalPref!!.setDefaultValue(taal)

        taalPref.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->

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

        val vakkenPref = findPreference<Preference>("vakken")
        vakkenPref!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToAddSubjectsFragment()
            findNavController().navigate(action)
            true
        }

        val darkPref = findPreference<ListPreference>("dark_key")

        when (prefs.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> darkPref?.setDefaultValue(resources.getStringArray(R.array.themes)[2])
            AppCompatDelegate.MODE_NIGHT_YES -> darkPref?.setDefaultValue(resources.getStringArray(R.array.themes)[1])
            AppCompatDelegate.MODE_NIGHT_NO -> darkPref?.setDefaultValue(resources.getStringArray(R.array.themes)[0])
        }

        darkPref?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue){
                "donker" -> {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    prefs.edit().putInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_YES).apply()}
                "licht" -> {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    prefs.edit().putInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_NO).apply()}
                "systeem" -> {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    prefs.edit().putInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM).apply()}
            }
            true
        }

    }
}