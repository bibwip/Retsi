package com.retsi.dabijhouder

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_add_subjects.*
import java.util.*

class AddSubjectsFragment : Fragment(R.layout.fragment_add_subjects), VakkenListAdapter.ItemClickListener{

    private var db: DatabaseHelper? = null
    private var adapter: VakkenListAdapter? = null
    var PREFS_NAME = "MyPrefsFile"
    var LANGUAGE_KEY = "language"

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
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("refresh", true)
            requireActivity().finish()
            startActivity(intent)
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
                .setPositiveButton(R.string.ok, ColorPickerClickListener { d, lastSelectedColor, allColors ->
                    db!!.updateVakkenData(adapter!!.getItem(position).vaknaam, "#" +
                            java.lang.Integer.toHexString(lastSelectedColor))
                    adapter!!.updateData(db!!.allData2)
                    d.cancel()
                })
                .setNegativeButton(android.R.string.cancel, DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
                .build()
                .show();
        }else if (id == R.id.tv_delete_vak || id == R.id.image_delete_vak) {
            db!!.deleteVak(adapter!!.getItem(position).vaknaam, adapter!!.getItem(position).vakColor)
            adapter!!.updateData(db!!.allData2)
        }
    }


}