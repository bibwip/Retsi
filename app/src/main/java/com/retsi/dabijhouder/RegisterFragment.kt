package com.retsi.dabijhouder

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_register_user.*

class RegisterFragment : NoToolBarFragment(R.layout.fragment_register_user) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        // Use credentials to create account and login
        registerFragment_btn_register.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToAddSubjectsFragment()
            navController.navigate(action)
        }

        // Go to login fragment to login with existing account
        registerFragment_btn_goto_login.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            navController.navigate(action)
        }

        // Continue without using an account
        registerFragment_btn_continue.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToAddSubjectsFragment()
            navController.navigate(action)
        }
    }
}