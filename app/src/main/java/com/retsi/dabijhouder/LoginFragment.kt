package com.retsi.dabijhouder

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.retsi.dabijhouder.databinding.FragmentLoginUserBinding

class LoginFragment : NoToolBarFragment(R.layout.fragment_login_user) {

    private var _binding: FragmentLoginUserBinding? = null
    private val binding get() = _binding!!

    private val TAG = "Login"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginUserBinding.inflate(inflater, container, false)
        return binding.root
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

            val email = binding.loginFragmentEmailEdtTxt.text.toString()
            val password = binding.loginFragmentPasswordEdtTxt.text.toString()

            loginWithCredentials(email, password)

        }

        // Go to register fragment to  make an account
        binding.loginFragmentBtnCreateAcc.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            navController.navigate(action)
        }

        // Continue without logging in
        binding.loginFragmentBtnContinue.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToMainFragment()
            navController.navigate(action)
        }
    }

    private fun loginWithCredentials(email: String, password: String) {

        val auth = Firebase.auth

        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            Log.d(TAG, "sign in with email completed, email: $email")

            (activity as MainActivity).overrideDataFromFirestore(DatabaseHelper(requireContext()))
            (activity as MainActivity).importVakkenFromFirestore(DatabaseHelper(requireContext()))

            val action = LoginFragmentDirections.actionLoginFragmentToMainFragment()
            findNavController().navigate(action)
        }.addOnFailureListener {
                Log.w(TAG, "sign in with email failed: $it")
            Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
        }

    }
}