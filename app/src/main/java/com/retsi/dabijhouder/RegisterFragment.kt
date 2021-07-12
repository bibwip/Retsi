package com.retsi.dabijhouder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.retsi.dabijhouder.databinding.FragmentRegisterUserBinding

class RegisterFragment : NoToolBarFragment(R.layout.fragment_register_user) {

    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    val TAG = "Registration"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        navController = findNavController()

        // Use credentials to create account and login
        binding.registerFragmentBtnRegister.setOnClickListener {

            val username = binding.registerFragmentUsername.text.toString()
            val email = binding.registerFragmentEmail.text.toString()
            val password = binding.registerFragmentPassword.text.toString()
            if (password.length < 6){
                // TODO: error text maken
                binding.registerFragmentPassword.error = "password should be at least 6 characters!"

            }else {
                createUser(email, password,username)
            }
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

    private fun createUser(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Created user with email: $email")

                    val user = auth.currentUser
                    Log.d(TAG, "Logged in with user email: ${user?.email.toString()}")
                    user!!.updateProfile(userProfileChangeRequest {
                        displayName = username
                    }).addOnSuccessListener { Log.d(TAG, "username is now: ${user.displayName}") }

                    val db = Firebase.firestore

                    val newUser = User(user.email.toString(), username)

                    db.collection("users").document(user.uid).set(newUser).addOnSuccessListener {
                        Log.d(TAG, "successfully added user to database")
                    }.addOnFailureListener { Log.d(TAG, "failed to add to database:" +it.message.toString()) }

                    val action = RegisterFragmentDirections.actionRegisterFragmentToAddSubjectsFragment()
                    navController.navigate(action)

                } else {
                    Log.w(TAG, "Failed to make user", task.exception)
                    // TODO: make error message
                    Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}