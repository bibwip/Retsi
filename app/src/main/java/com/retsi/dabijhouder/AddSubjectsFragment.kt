package com.retsi.dabijhouder

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.retsi.dabijhouder.databinding.FragmentAddSubjectsBinding
import java.util.*

class AddSubjectsFragment : NoToolBarFragment(R.layout.fragment_add_subjects), VakkenListAdapter.ItemClickListener{

    private var db: DatabaseHelper? = null
    private var adapter: VakkenListAdapter? = null

    companion object{
        const val PREFS_NAME = "MyPrefsFile"
        const val LANGUAGE_KEY = "language"
    }

    private var _binding: FragmentAddSubjectsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddSubjectsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs: SharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedLanguage = prefs.getString(LANGUAGE_KEY, "en")
        val context: Context = CustomContextWrapper.wrap(activity, savedLanguage)
        resources.updateConfiguration(
            context.resources.configuration,
            context.resources.displayMetrics
        )
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(activity)

        binding.RecyclerVakken.layoutManager = LinearLayoutManager(activity)
        adapter = VakkenListAdapter(db!!.allData2())
        adapter!!.setClickListener(this)
        binding.RecyclerVakken.adapter = adapter

        binding.btnAddVak.setOnClickListener {
            val vaknaam = binding.edtTxtVaknaam.text.toString().substring(0, 1)
                .uppercase(Locale.getDefault()) + binding.edtTxtVaknaam.text.toString().substring(1)
            db!!.insertData(vaknaam, resources.getString(0 + R.color.background))
            adapter!!.updateData(db!!.allData2())
            binding.edtTxtVaknaam.setText("")
        }

        binding.btnSaveStartup.setOnClickListener{
            val action = AddSubjectsFragmentDirections.actionAddSubjectsFragmentToMainFragment()
            view.findNavController().navigate(action)
        }
    }

    override fun onItemClick(view: View, position: Int) {
        val id = view.id
        if (id == R.id.tv_color_picker || id == R.id.image_color_picker) {
            ColorPickerDialogBuilder
                .with(requireActivity())
                .setTitle("Choose color")
                .initialColor(R.color.white)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton(android.R.string.ok) { d, lastSelectedColor, _ ->
                    db!!.updateVakkenData(
                        adapter!!.getItem(position).vaknaam, "#" +
                                Integer.toHexString(lastSelectedColor)
                    )
                    adapter!!.updateData(db!!.allData2())
                    d.cancel()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .build()
                .show()
        }else if (id == R.id.tv_delete_vak || id == R.id.image_delete_vak) {
            db!!.deleteVak(adapter!!.getItem(position).vaknaam, adapter!!.getItem(position).vakColor)
            adapter!!.updateData(db!!.allData2())
        }
    }
}