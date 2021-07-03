package com.retsi.dabijhouder

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker.OnDateChangedListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_add_assignment.*
import java.util.*

class AddAssignmentFragment : NoToolBarFragment(R.layout.fragment_add_assignment) {

    private lateinit var myDb: DatabaseHelper

    lateinit var typeOpdracht : String
    var datum = ""
    lateinit var titel : String
    lateinit var beschrijving : String
    lateinit var vaknaam : String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        typeOpdracht = getString(R.string.Huiswerk_key)

        myDb = DatabaseHelper(requireContext())
        SetupRadioButtons()
        setupSpinner()

        btn_kies_datum.setOnClickListener { KiesDatum() }

        btnMakeAssignment.setOnClickListener {
            vaknaam = spinnerVakken.selectedItem.toString()
            titel = edtTxtOpdrachtNaam.text.toString()
            if (titel == "") {
                edtTxtOpdrachtNaam.error = getString(R.string.error)
            } else if (datum == "") {
                tv_gekozen_datum.text = getString(R.string.datum_error)
                tv_gekozen_datum.setTextColor(resources.getColor(R.color.red))
            } else {
                beschrijving = edtTxtBeschrijving.text.toString()
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
        datePicker.visibility = View.VISIBLE
        radioGroupTypeAssignment.visibility = View.GONE
        edtTxtBeschrijving.visibility = View.GONE
        edtTxtOpdrachtNaam.visibility = View.GONE
        spinnerVakken.visibility = View.GONE
        btnMakeAssignment.visibility = View.GONE
        btn_kies_datum.visibility = View.GONE
        btn_ok_datum.visibility = View.VISIBLE
        tv_gekozen_datum.visibility = View.GONE
        val onDateChangedListener =
            OnDateChangedListener { _, i, i1, i2 ->
                var dag = i2.toString()
                var maand = (i1 + 1).toString()
                val jaar = i.toString()
                if (dag.length == 1) dag = "0$dag"
                if (maand.length == 1) maand = "0$maand"
                datum = "$dag-$maand-$jaar"
                tv_gekozen_datum.setTextColor(resources.getColor(R.color.grey))
                tv_gekozen_datum.text = datum
            }

        btn_ok_datum.setOnClickListener {
            datePicker.visibility = View.GONE
            radioGroupTypeAssignment.visibility = View.VISIBLE
            edtTxtBeschrijving.visibility = View.VISIBLE
            edtTxtOpdrachtNaam.visibility = View.VISIBLE
            spinnerVakken.visibility = View.VISIBLE
            btnMakeAssignment.visibility = View.VISIBLE
            btn_kies_datum.visibility = View.VISIBLE
            btn_ok_datum.visibility = View.GONE
            tv_gekozen_datum.visibility = View.VISIBLE
        }
        datePicker.init(
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

        spinnerVakken.adapter = spinnerAdapter
    }

    private fun SetupRadioButtons() {
        rbnEindopdracht.setOnClickListener {
            rbnHuiswerk.isChecked = false
            rbnOverig.isChecked = false
            rbnToets.isChecked = false
            typeOpdracht = getString(R.string.Eindopdracht_key)
        }
        rbnHuiswerk.setOnClickListener {
            rbnEindopdracht.isChecked = false
            rbnOverig.isChecked = false
            rbnToets.isChecked = false
            typeOpdracht = getString(R.string.Huiswerk_key)
        }
        rbnToets.setOnClickListener {
            rbnHuiswerk.isChecked = false
            rbnOverig.isChecked = false
            rbnEindopdracht.isChecked = false
            typeOpdracht = getString(R.string.Toets_key)
        }
        rbnOverig.setOnClickListener {
            rbnHuiswerk.isChecked = false
            rbnEindopdracht.isChecked = false
            rbnToets.isChecked = false
            typeOpdracht = getString(R.string.overig_key)
        }
    }
}