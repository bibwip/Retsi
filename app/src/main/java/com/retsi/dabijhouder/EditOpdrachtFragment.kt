package com.retsi.dabijhouder

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.retsi.dabijhouder.databinding.FragmentEditOpdrachtBinding
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

    private var _binding: FragmentEditOpdrachtBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditOpdrachtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


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

        binding.editOpTitel.setText(editItem.titel)
        binding.editOpBeschrijving.setText(editItem.beschrijving)
        binding.editOpDatumtv.text = editItem.datum

        val vakSpinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.typeOpdrachten, android.R.layout.simple_spinner_item
        )
        binding.editOpTypeopdracht.adapter = vakSpinnerAdapter

        val list = myDb.vakkenNamen()

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_vak_item,
            R.id.tvsimplevaknaamitemfrancaishierook,
            list)

        spinnerAdapter.setDropDownViewResource(R.layout.simple_vak_item)
        binding.editOpVakSpinner.adapter = spinnerAdapter
        var index = -1

        when (editItem.typeOpdracht) {
            getString(R.string.Toets_key) -> index = 1
            getString(R.string.Eindopdracht_key) -> index = 2
            getString(R.string.Huiswerk_key) -> index = 0
        }

        binding.editOpTypeopdracht.setSelection(index)
        binding.editOpVakSpinner.setSelection(getIndex(list, editItem.vakNaam))

        val dateSplit = editItem.datum.split("-").toTypedArray()
        binding.editOpDatePicker.init(dateSplit[2].toInt(), dateSplit[1].toInt() - 1, dateSplit[0].toInt(), null)


        binding.editOpbtnKiesDate.setOnClickListener {
            binding.scrollView2.visibility = View.GONE
            binding.editOpSave.visibility = View.GONE
            binding.editOpDatePicker.visibility = View.VISIBLE
            binding.editOpok.visibility = View.VISIBLE
        }
        binding.editOpok.setOnClickListener {
            binding.scrollView2.visibility = View.VISIBLE
            binding.editOpSave.visibility = View.VISIBLE
            binding.editOpDatePicker.visibility = View.GONE
            binding.editOpok.visibility = View.GONE

            var dag: String = binding.editOpDatePicker.dayOfMonth.toString()
            var maand: String = binding.editOpDatePicker.month.toString()
            if (dag.length == 1) dag = "0" + binding.editOpDatePicker.dayOfMonth.toString()
            if (maand.length == 1) maand = "0" + (binding.editOpDatePicker.month + 1).toString()
            datum = dag + "-" + maand + "-" + binding.editOpDatePicker.year.toString()
            binding.editOpDatumtv.text = datum
        }
        binding.editOpSave.setOnClickListener{
            val keys = resources.getStringArray(R.array.opdrachtkeys)
            type = keys[binding.editOpTypeopdracht.selectedItemPosition]
            vak = binding.editOpVakSpinner.selectedItem.toString()
            titel = binding.editOpTitel.text.toString()
            beschrijving = binding.editOpBeschrijving.text.toString()
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