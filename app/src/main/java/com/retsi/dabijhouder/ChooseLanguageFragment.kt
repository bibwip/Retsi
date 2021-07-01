package com.retsi.dabijhouder

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_choose_language.*
import java.util.*

class ChooseLanguageFragment : Fragment(R.layout.fragment_choose_language) {

    var PREFS_NAME = "MyPrefsFile"
    var LANGUAGE_KEY = "language"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner_talen.setSelection(getLanguage())

        startUp_language_btn_next.setOnClickListener {
            if (spinner_talen.selectedItem.toString() == getString(R.string.nederlands)) SaveLanguage("nl")
            else if (spinner_talen.selectedItem.toString() == getString(R.string.engels)) SaveLanguage("en")

            val action = ChooseLanguageFragmentDirections.actionChooseLanguageFragmentToAddSubjectsFragment()
            view.findNavController().navigate(action)
        }
    }

    fun SaveLanguage(lang : String) {
        val prefs: SharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(LANGUAGE_KEY, lang).apply()
    }
    fun getLanguage(): Int {
        val res = resources
        val lang = res.configuration.locale.toString()
        return if (lang == "en" || lang == "en_US") 0 else if (lang == "nl") 1 else 0
    }

}