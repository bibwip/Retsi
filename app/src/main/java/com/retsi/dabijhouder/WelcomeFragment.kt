package com.retsi.dabijhouder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.retsi.dabijhouder.databinding.FragmentWelcomeBinding

class WelcomeFragment : NoToolBarFragment(R.layout.fragment_welcome) {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.welcomeFragmentWelcomeNextButton.setOnClickListener{
            val action = WelcomeFragmentDirections
                .actionWelcomeFragmentToChooseLanguageFragment()
            view.findNavController().navigate(action)
        }
    }
}