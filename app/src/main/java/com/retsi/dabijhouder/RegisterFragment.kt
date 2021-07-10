package com.retsi.dabijhouder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import com.retsi.dabijhouder.databinding.FragmentRegisterUserBinding
import android.view.ViewGroup as ViewGroup

class RegisterFragment : NoToolBarFragment(R.layout.fragment_register_user) {

    private var _binding: FragmentRegisterUserBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterUserBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        // Use credentials to create account and login
        binding.registerFragmentBtnRegister.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToAddSubjectsFragment()
            navController.navigate(action)
        }

        // Go to login fragment to login with existing account
        binding.registerFragmentBtnGotoLogin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            navController.navigate(action)
        }

        // Continue without using an account
        binding.registerFragmentBtnContinue.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToAddSubjectsFragment()
            navController.navigate(action)
        }
    }
}