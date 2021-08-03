package com.retsi.dabijhouder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.retsi.dabijhouder.databinding.FragmentImportOpdrachtBinding

class ImportOpdrachtFragment: NoToolBarFragment(R.layout.fragment_import_opdracht) {

    private val args: ImportOpdrachtFragmentArgs by navArgs()

    private var _binding: FragmentImportOpdrachtBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportOpdrachtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myDb = DatabaseHelper(requireContext())
        Toast.makeText(requireContext(), "kaas", Toast.LENGTH_SHORT).show()
        val action = ImportOpdrachtFragmentDirections.actionImportOpdrachtFragmentToMainFragment()

        binding.importVak.text = args.vakNaam

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            myDb.vakkenNamen
        )
        binding.importSpinner.adapter = spinnerAdapter

        binding.importToevoegen.setOnClickListener {
            myDb.insertData(binding.importVak.text.toString(), "#FFF6F1")
            myDb.insertData(
                args.typeOpdrachtKey,
                args.vakNaam,
                args.titel,
                args.datum,
                args.beschrijving
            )
            view.findNavController().navigate(action)
        }
        binding.importAnder.setOnClickListener{
            binding.importSpinner.visibility = View.VISIBLE
            binding.importSave.visibility = View.VISIBLE
        }
        binding.importSave.setOnClickListener{
            myDb.insertData(
                args.typeOpdrachtKey,
                binding.importSpinner.selectedItem.toString(),
                args.titel,
                args.datum,
                args.beschrijving
            )
            view.findNavController().navigate(action)
        }
    }
}