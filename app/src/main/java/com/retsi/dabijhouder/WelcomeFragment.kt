package com.retsi.dabijhouder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        welcomeFragment_welcome_next_button.setOnClickListener{
            val action = WelcomeFragmentDirections
                .actionWelcomeFragmentToChooseLanguageFragment()
            view.findNavController().navigate(action)
        }
    }
}