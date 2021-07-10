package com.retsi.dabijhouder

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import androidx.navigation.findNavController
import com.retsi.dabijhouder.databinding.FragmentChooseLanguageBinding

class ChooseLanguageFragment : NoToolBarFragment(R.layout.fragment_choose_language) {

    var PREFS_NAME = "MyPrefsFile"
    var LANGUAGE_KEY = "language"

    private var _binding: FragmentChooseLanguageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseLanguageBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.spinnerTalen.setSelection(getLanguage())

        binding.startUpLanguageBtnNext.setOnClickListener {
            if (binding.spinnerTalen.selectedItem.toString() == getString(R.string.nederlands)) SaveLanguage("nl")
            else if (binding.spinnerTalen.selectedItem.toString() == getString(R.string.engels)) SaveLanguage("en")

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