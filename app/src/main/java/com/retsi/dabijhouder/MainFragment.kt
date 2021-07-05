package com.retsi.dabijhouder

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment(R.layout.fragment_main) {
    
    private var myDb: DatabaseHelper? = null
    private var mAdapter: RecyclerAdapter? = null
    private var chosenFilters = ArrayList<String>()
    var opdrachtbackup: OpdrachtItem? = null
    var clicked = false
    private val toBeDeleted = ArrayList<Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        myDb = DatabaseHelper(requireContext())

        requireActivity().getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE)


        btn_goto_add_assignment.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddAssignmentFragment()
            view.findNavController().navigate(action)
        }

        val recyclerView: RecyclerView = main_recycler_view
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        mAdapter = RecyclerAdapter(setData(), myDb, this.context)
        recyclerView.adapter = mAdapter

        if (requireActivity().intent.hasExtra("refresh")) {
            if (requireActivity().intent.getBooleanExtra("refresh", false)) mAdapter!!.notifyDataSetChanged()
        }

        checkBoxesSetup()

        mAdapter!!.setClickListener { v, position ->
            if (v.id == R.id.img_btn_check_opdracht) {
                if (opdrachtbackup != null) {
                    toBeDeleted.add(opdrachtbackup!!.id)
                }
                opdrachtbackup = mAdapter!!.getItem(position)
                val snackbar = Snackbar.make(
                    coordinatorLayoutForMain,
                    R.string.deleted_opdracht, BaseTransientBottomBar.LENGTH_LONG
                )
                snackbar.setAction(android.R.string.cancel) {
                    clicked = true
                    mAdapter!!.InsertItem(opdrachtbackup, position)
                    toBeDeleted.remove(opdrachtbackup!!.id)
                    opdrachtbackup = null
                }
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (clicked) {
                            clicked = false
                        } else {
                            toBeDeleted.add(opdrachtbackup!!.id)
                        }
                    }
                })
                snackbar.show()
                mAdapter!!.RemoveItem(position)
            } else {
                if (mAdapter!!.getItem(position).beschrijving != "") {
                    mAdapter!!.getItem(position).isExpanded =
                        !mAdapter!!.getItem(position).isExpanded
                    mAdapter!!.notifyItemChanged(position)
                }
            }
        }

        mAdapter!!.setLongClickListener { v, position ->
            val popup = PopupMenu(context, v)
            popup.menuInflater.inflate(R.menu.opdracht_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                val item: OpdrachtItem = mAdapter!!.getItem(position)
                when (menuItem.itemId) {
                    R.id.action_edit_opdracht -> {
                        val action = MainFragmentDirections.actionMainFragmentToEditOpdrachtFragment(item.id)
                        view.findNavController().navigate(action)
                        true
                    }
                    R.id.action_share_opdracht -> {
                        val builder = Uri.Builder()
                        builder.scheme("https")
                            .authority("www.github.com")
                            .path("/winkelkar/Retsi")
                            .appendQueryParameter(
                                getString(R.string.TypeOpdracht),
                                mAdapter!!.getItem(position).typeOpdracht_key
                            )
                            .appendQueryParameter(
                                getString(R.string.Titel),
                                mAdapter!!.getItem(position).titel
                            )
                            .appendQueryParameter(
                                getString(R.string.vaknaam),
                                mAdapter!!.getItem(position).vakNaam
                            )
                            .appendQueryParameter(
                                getString(R.string.datum),
                                mAdapter!!.getItem(position).datum
                            )
                            .appendQueryParameter(
                                getString(R.string.beschrijving),
                                mAdapter!!.getItem(position).beschrijving
                            )
                        val url = builder.build().toString()
                        val shareItem = Intent()
                        shareItem.setAction(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_TEXT, url).type = "text/plain"
                        val sendIntent = Intent.createChooser(shareItem, null)
                        startActivity(sendIntent)
                        true
                    }
                    R.id.menu_action_set_priority -> {
                        if (item.belangerijk == 1){
                            myDb!!.SetBelangerijk(item.id, 0)
                        } else{
                            myDb!!.SetBelangerijk(item.id, 1)
                        }
                        mAdapter!!.UpdateItems(setData())
                        true
                    }
                    else -> true
                }
            }
            popup.show()
        }

        val data: Uri? = requireActivity().intent.data
        if (data != null) {
            val vak = data.getQueryParameter(getString(R.string.vaknaam))!!
            val type = data.getQueryParameter(getString(R.string.TypeOpdracht))!!
            val titel = data.getQueryParameter(getString(R.string.Titel))!!
            val datum = data.getQueryParameter(getString(R.string.datum))!!
            val bes = data.getQueryParameter(getString(R.string.beschrijving))!!
            requireActivity().intent.data = null
            var inlist = false
            for (item in myDb!!.allData2) {
                if (vak == item.vaknaam) {
                    inlist = true
                    myDb!!.insertData(type, vak, titel, datum, bes)
                    mAdapter!!.UpdateItems(setData())
                }
            }
            if (!inlist) {
                val action = MainFragmentDirections.actionMainFragmentToImportOpdrachtFragment(type,
                    titel, vak, datum, bes)
                findNavController().navigate(action)
            }
        }
    }

    private fun setData(): ArrayList<OpdrachtItem> {
        val filters: ArrayList<String> = chosenFilters
        if (filters.size == 0) {
            filters.add(getString(R.string.Eindopdracht_key))
            filters.add(getString(R.string.Huiswerk_key))
            filters.add(getString(R.string.Toets_key))
            filters.add(getString(R.string.overig_key))
        }
        val items = ArrayList<OpdrachtItem>()
        val res: Cursor = myDb!!.allData
        if (res.count == 0) {
            return items
        }
        while (res.moveToNext()) {
            var typeOpdracht = res.getString(1)
            if (filters.contains(typeOpdracht)) {
                val vak = res.getString(2)
                val titel = res.getString(3)
                val datum = res.getString(4)
                val bescrhijving = res.getString(5)
                val typeKey = res.getString(1)
                val itemId = res.getInt(0)
                val belangerijk = res.getInt(6)
                when (typeOpdracht) {
                    "Toets_key" -> typeOpdracht = getString(R.string.Toets)
                    "eindopdracht_key" -> typeOpdracht = getString(R.string.Eindopdracht)
                    "Huiswerk_key" -> typeOpdracht = getString(R.string.Huiswerk)
                    "overig_key" -> typeOpdracht = getString(R.string.overig)
                    else -> {
                    }
                }
                val sList = datum.split("-").toTypedArray()
                val datumKey = (sList[2] + sList[1] + sList[0]).toInt()
                val opdracht =
                    OpdrachtItem(itemId, typeOpdracht, vak, titel, datum, bescrhijving, belangerijk, datumKey,
                        typeKey)
                items.add(opdracht)
            }
        }
        items.sortWith {opdrachtItem, t1 -> t1.belangerijk.compareTo(opdrachtItem.belangerijk)}
        items.sortWith { opdrachtItem, t1 ->
            if(opdrachtItem.belangerijk == t1.belangerijk){
                opdrachtItem.datumTagSorter!!.compareTo(t1.datumTagSorter!!)
            } else {
                0
            }
        }
        return items
    }

    private fun checkBoxesSetup() {
        checkbox_all.isChecked = true
        checkbox_all.setOnCheckedChangeListener { _, b ->
            if (b) {
                checkBox_eindopdracht.isChecked = false
                checkBox_toets.isChecked = false
                checkBox_huiswerk.isChecked = false
                checkBox_overig.isChecked = false
                chosenFilters.clear()
                mAdapter!!.UpdateItems(setData())
            } else {
                chosenFilters.clear()
            }
        }
        checkBox_eindopdracht.setOnCheckedChangeListener { _, b ->
            if (b) {
                if (checkbox_all.isChecked) checkbox_all.isChecked = false
                chosenFilters.add(getString(R.string.Eindopdracht_key))
                mAdapter!!.UpdateItems(setData())
            } else {
                chosenFilters.remove(getString(R.string.Eindopdracht_key))
                if (chosenFilters.size == 0) {
                    checkbox_all.isChecked = true
                }
                mAdapter!!.UpdateItems(setData())
            }
        }
        checkBox_toets.setOnCheckedChangeListener { _, b ->
            if (b) {
                if (checkbox_all.isChecked) checkbox_all.isChecked = false
                chosenFilters.add(getString(R.string.Toets_key))
                mAdapter!!.UpdateItems(setData())
            } else {
                chosenFilters.remove(getString(R.string.Toets_key))
                if (chosenFilters.size == 0) {
                    checkbox_all.isChecked = true
                }
                mAdapter!!.UpdateItems(setData())
            }
        }
        checkBox_huiswerk.setOnCheckedChangeListener { _, b ->
            if (b) {
                if (checkbox_all.isChecked) checkbox_all.isChecked = false
                chosenFilters.add(getString(R.string.Huiswerk_key))
                mAdapter!!.UpdateItems(setData())
            } else {
                chosenFilters.remove(getString(R.string.Huiswerk_key))
                if (chosenFilters.size == 0) {
                    checkbox_all.isChecked = true
                }
                mAdapter!!.UpdateItems(setData())
            }
        }
        checkBox_overig.setOnCheckedChangeListener { _, b ->
            if (b) {
                if (checkbox_all.isChecked) checkbox_all.isChecked = false
                chosenFilters.add(getString(R.string.overig_key))
                mAdapter!!.UpdateItems(setData())
            } else {
                chosenFilters.remove(getString(R.string.overig_key))
                if (chosenFilters.size == 0) {
                    checkbox_all.isChecked = true
                }
                mAdapter!!.UpdateItems(setData())
            }
        }
        img_btn_close_menu.setOnClickListener {
            main_filters.visibility = View.GONE
            img_btn_open_menu.visibility = View.VISIBLE
            _____lijn2.visibility = View.GONE
            _____lijn3.visibility = View.VISIBLE
        }
        img_btn_open_menu.setOnClickListener {
            main_filters.visibility = View.VISIBLE
            img_btn_open_menu.visibility = View.GONE
            _____lijn2.visibility = View.VISIBLE
            _____lijn3.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        if (toBeDeleted.isNotEmpty()){
            for (id in toBeDeleted){
                myDb!!.deleteOpdracht(id)
            }
            val updateWidgetIntent = Intent()
            updateWidgetIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            requireActivity().sendBroadcast(updateWidgetIntent)
        }
    }
}

