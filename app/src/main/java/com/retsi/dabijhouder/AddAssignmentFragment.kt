package com.retsi.dabijhouder

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker.OnDateChangedListener
import androidx.navigation.findNavController
import com.retsi.dabijhouder.databinding.FragmentAddAssignmentBinding
import java.util.*

class AddAssignmentFragment : NoToolBarFragment(R.layout.fragment_add_assignment) {

    private lateinit var myDb: DatabaseHelper

    lateinit var typeOpdracht : String
    var datum = ""
    lateinit var titel : String
    lateinit var beschrijving : String
    lateinit var vaknaam : String

    private var _binding: FragmentAddAssignmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAssignmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        typeOpdracht = getString(R.string.Huiswerk_key)

        myDb = DatabaseHelper(requireContext())
        SetupRadioButtons()
        setupSpinner()

        binding.btnKiesDatum.setOnClickListener { KiesDatum() }

        binding.btnMakeAssignment.setOnClickListener {
            vaknaam = binding.spinnerVakken.selectedItem.toString()
            titel = binding.edtTxtOpdrachtNaam.text.toString()
            if (titel == "") {
                binding.edtTxtOpdrachtNaam.error = getString(R.string.error)
            } else if (datum == "") {
                binding.tvGekozenDatum.text = getString(R.string.datum_error)
                binding.tvGekozenDatum.setTextColor(resources.getColor(R.color.red))
            } else {
                beschrijving = binding.edtTxtBeschrijving.text.toString()
                myDb.insertData(typeOpdracht, vaknaam, titel, datum, beschrijving)
                val updateWidgetIntent = Intent()
                updateWidgetIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                requireActivity().sendBroadcast(updateWidgetIntent)
                val action =
                    AddAssignmentFragmentDirections.actionAddAssignmentFragmentToMainFragment()
                view.findNavController().navigate(action)
            }
        }

    }

    private fun KiesDatum() {
        binding.datePicker.visibility = View.VISIBLE
        binding.radioGroupTypeAssignment.visibility = View.GONE
        binding.edtTxtBeschrijving.visibility = View.GONE
        binding.edtTxtOpdrachtNaam.visibility = View.GONE
        binding.spinnerVakken.visibility = View.GONE
        binding.btnMakeAssignment.visibility = View.GONE
        binding.btnKiesDatum.visibility = View.GONE
        binding.btnOkDatum.visibility = View.VISIBLE
        binding.tvGekozenDatum.visibility = View.GONE
        val onDateChangedListener =
            OnDateChangedListener { _, i, i1, i2 ->
                var dag = i2.toString()
                var maand = (i1 + 1).toString()
                val jaar = i.toString()
                if (dag.length == 1) dag = "0$dag"
                if (maand.length == 1) maand = "0$maand"
                datum = "$dag-$maand-$jaar"
                binding.tvGekozenDatum.setTextColor(resources.getColor(R.color.grey))
                binding.tvGekozenDatum.text = datum
            }

        binding.btnOkDatum.setOnClickListener {
            binding.datePicker.visibility = View.GONE
            binding.radioGroupTypeAssignment.visibility = View.VISIBLE
            binding.edtTxtBeschrijving.visibility = View.VISIBLE
            binding.edtTxtOpdrachtNaam.visibility = View.VISIBLE
            binding.spinnerVakken.visibility = View.VISIBLE
            binding.btnMakeAssignment.visibility = View.VISIBLE
            binding.btnKiesDatum.visibility = View.VISIBLE
            binding.btnOkDatum.visibility = View.GONE
            binding.tvGekozenDatum.visibility = View.VISIBLE
        }
        binding.datePicker.init(
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH],
            onDateChangedListener
        )
    }

    private fun setupSpinner() {
        val spinnerAdapter = ArrayAdapter(
            requireContext(), 
            R.layout.support_simple_spinner_dropdown_item,
            myDb.vakkenNamen)

        binding.spinnerVakken.adapter = spinnerAdapter
    }

    private fun SetupRadioButtons() {
        binding.rbnEindopdracht.setOnClickListener {
            binding.rbnHuiswerk.isChecked = false
            binding.rbnOverig.isChecked = false
            binding.rbnToets.isChecked = false
            typeOpdracht = getString(R.string.Eindopdracht_key)
        }
        binding.rbnHuiswerk.setOnClickListener {
            binding.rbnEindopdracht.isChecked = false
            binding.rbnOverig.isChecked = false
            binding.rbnToets.isChecked = false
            typeOpdracht = getString(R.string.Huiswerk_key)
        }
        binding.rbnToets.setOnClickListener {
            binding.rbnHuiswerk.isChecked = false
            binding.rbnOverig.isChecked = false
            binding.rbnEindopdracht.isChecked = false
            typeOpdracht = getString(R.string.Toets_key)
        }
        binding.rbnOverig.setOnClickListener {
            binding.rbnHuiswerk.isChecked = false
            binding.rbnEindopdracht.isChecked = false
            binding.rbnToets.isChecked = false
            typeOpdracht = getString(R.string.overig_key)
        }
    }
}