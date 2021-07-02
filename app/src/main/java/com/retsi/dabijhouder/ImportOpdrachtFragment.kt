package com.retsi.dabijhouder

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_import_opdracht.*

class ImportOpdrachtFragment: NoToolBarFragment(R.layout.fragment_import_opdracht) {

    private val args: ImportOpdrachtFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myDb = DatabaseHelper(requireContext())
        Toast.makeText(requireContext(), "kaas", Toast.LENGTH_SHORT).show()
        val action = ImportOpdrachtFragmentDirections.actionImportOpdrachtFragmentToMainFragment()

        import_vak.text = args.vakNaam

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            myDb.vakkenNamen
        )
        import_spinner.adapter = spinnerAdapter

        import_toevoegen.setOnClickListener(View.OnClickListener {
            myDb.insertData(import_vak.text.toString(), "#FFF6F1")
            myDb.insertData(
                args.typeOpdrachtKey,
                args.vakNaam,
                args.titel,
                args.datum,
                args.beschrijving
            )
            view.findNavController().navigate(action)
        })
        import_ander.setOnClickListener(View.OnClickListener {
            import_spinner.visibility = View.VISIBLE
            import_save.visibility = View.VISIBLE
        })
        import_save.setOnClickListener(View.OnClickListener {
            myDb.insertData(
                args.typeOpdrachtKey,
                import_spinner.selectedItem.toString(),
                args.titel,
                args.datum,
                args.beschrijving
            )
            view.findNavController().navigate(action)
        })
    }
}