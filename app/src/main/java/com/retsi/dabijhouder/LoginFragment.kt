package com.retsi.dabijhouder

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.retsi.dabijhouder.databinding.FragmentLoginUserBinding

class LoginFragment : NoToolBarFragment(R.layout.fragment_login_user) {

    private var _binding: FragmentLoginUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginUserBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs: SharedPreferences = requireActivity().getSharedPreferences(AddSubjectsFragment.PREFS_NAME, Context.MODE_PRIVATE)
        val savedLanguage = prefs.getString(AddSubjectsFragment.LANGUAGE_KEY, "en")
        val context: Context = CustomContextWrapper.wrap(activity, savedLanguage)
        resources.updateConfiguration(
            context.resources.configuration,
            context.resources.displayMetrics
        )
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        // Use credentials to login to account
        binding.loginFragmentBtnLogin.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToAddSubjectsFragment()
            navController.navigate(action)
        }

        // Go to register fragment to  make an account
        binding.loginFragmentBtnCreateAcc.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            navController.navigate(action)
        }

        // Continue without logging in
        binding.loginFragmentBtnContinue.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToAddSubjectsFragment()
            navController.navigate(action)
        }
    }
}