package com.retsi.dabijhouder

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_login_user.*

class LoginFragment : NoToolBarFragment(R.layout.fragment_login_user) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        // Use credentials to login to account
        loginFragment_btn_login.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToAddSubjectsFragment()
            navController.navigate(action)
        }

        // Go to register fragment to  make an account
        loginFragment_btn_create_acc.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            navController.navigate(action)
        }

        // Continue without logging in
        loginFragment_btn_continue.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToAddSubjectsFragment()
            navController.navigate(action)
        }
    }
}