package com.retsi.dabijhouder

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_edit_opdracht.*
import java.util.*

class EditOpdrachtFragment : NoToolBarFragment(R.layout.fragment_edit_opdracht) {

    private val args: EditOpdrachtFragmentArgs by navArgs()
    private lateinit var editItem: OpdrachtItem
    private lateinit var myDb: DatabaseHelper


    lateinit var vak: String
    lateinit var titel: String
    lateinit var type: String
    lateinit var beschrijving: String
    lateinit var datum: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myDb = DatabaseHelper(requireContext())

        editItem = myDb.getOpdracht(args.id)

        vak = editItem.vakNaam
        titel = editItem.titel
        type = editItem.typeOpdracht
        beschrijving = editItem.beschrijving
        datum = editItem.datum

        setupViews(view)
    }

    private fun setupViews(main_view: View) {

        editOpTitel.setText(editItem.titel)
        editOpBeschrijving.setText(editItem.beschrijving)
        editOpDatumtv.text = editItem.datum

        val vakSpinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.typeOpdrachten, android.R.layout.simple_spinner_item
        )
        editOpTypeopdracht.adapter = vakSpinnerAdapter

        val list = myDb.vakkenNamen

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_vak_item,
            R.id.tvsimplevaknaamitemfrancaishierook,
            list)

        spinnerAdapter.setDropDownViewResource(R.layout.simple_vak_item)
        editOpVakSpinner.adapter = spinnerAdapter
        var index = -1

        when (editItem.typeOpdracht) {
            getString(R.string.Toets_key) -> index = 1
            getString(R.string.Eindopdracht_key) -> index = 2
            getString(R.string.Huiswerk_key) -> index = 0
        }

        editOpTypeopdracht.setSelection(index)
        editOpVakSpinner.setSelection(getIndex(list, editItem.vakNaam))

        val dateSplit = editItem.datum.split("-").toTypedArray()
        editOpDatePicker.init(dateSplit[2].toInt(), dateSplit[1].toInt() - 1, dateSplit[0].toInt(), null)


        editOpbtnKiesDate.setOnClickListener {
            scrollView2.visibility = View.GONE
            editOpSave.visibility = View.GONE
            editOpDatePicker.visibility = View.VISIBLE
            editOpok.visibility = View.VISIBLE
        }
        editOpok.setOnClickListener {
            scrollView2.visibility = View.VISIBLE
            editOpSave.visibility = View.VISIBLE
            editOpDatePicker.visibility = View.GONE
            editOpok.visibility = View.GONE

            var dag: String = editOpDatePicker.dayOfMonth.toString()
            var maand: String = editOpDatePicker.month.toString()
            if (dag.length == 1) dag = "0" + editOpDatePicker.dayOfMonth.toString()
            if (maand.length == 1) maand = "0" + (editOpDatePicker.month + 1).toString()
            datum = dag + "-" + maand + "-" + editOpDatePicker.year.toString()
            editOpDatumtv.text = datum
        }
        editOpSave.setOnClickListener{
            val keys = resources.getStringArray(R.array.opdrachtkeys)
            type = keys[editOpTypeopdracht.selectedItemPosition]
            vak = editOpVakSpinner.selectedItem.toString()
            titel = editOpTitel.text.toString()
            beschrijving = editOpBeschrijving.text.toString()
            myDb.updateOpdracht(args.id.toString(), type, vak, titel, datum, beschrijving)
            val updateWidgetIntent = Intent()
            updateWidgetIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            requireActivity().sendBroadcast(updateWidgetIntent)
            val action = EditOpdrachtFragmentDirections.actionEditOpdrachtFragmentToMainFragment()
            main_view.findNavController().navigate(action)
        }
    }

    private fun getIndex(list: ArrayList<String>, item: String): Int {
        var index = -1
        for (i in list.indices) {
            if (list[i] == item) {
                index = i
                return index
            }
        }
        return index
    }
}