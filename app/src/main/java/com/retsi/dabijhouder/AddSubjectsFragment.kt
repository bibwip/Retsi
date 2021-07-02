package com.retsi.dabijhouder

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_add_subjects.*
import java.util.*

class AddSubjectsFragment : NoToolBarFragment(R.layout.fragment_add_subjects), VakkenListAdapter.ItemClickListener{

    private var db: DatabaseHelper? = null
    private var adapter: VakkenListAdapter? = null

    companion object{
        const val PREFS_NAME = "MyPrefsFile"
        const val LANGUAGE_KEY = "language"
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

        Recycler_vakken.layoutManager = LinearLayoutManager(activity)
        adapter = VakkenListAdapter(db!!.allData2)
        adapter!!.setClickListener(this)
        Recycler_vakken.adapter = adapter

        btn_add_vak.setOnClickListener {
            val vaknaam = edtTxt_vaknaam.text.toString().substring(0, 1)
                .uppercase(Locale.getDefault()) + edtTxt_vaknaam.text.toString().substring(1)
            db!!.insertData(vaknaam, resources.getString(0 + R.color.background))
            adapter!!.updateData(db!!.allData2)
            edtTxt_vaknaam.setText("")
        }

        btn_save_startup.setOnClickListener{
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
                .setPositiveButton(R.string.ok) { d, lastSelectedColor, _ ->
                    db!!.updateVakkenData(
                        adapter!!.getItem(position).vaknaam, "#" +
                                Integer.toHexString(lastSelectedColor)
                    )
                    adapter!!.updateData(db!!.allData2)
                    d.cancel()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .build()
                .show()
        }else if (id == R.id.tv_delete_vak || id == R.id.image_delete_vak) {
            db!!.deleteVak(adapter!!.getItem(position).vaknaam, adapter!!.getItem(position).vakColor)
            adapter!!.updateData(db!!.allData2)
        }
    }
}