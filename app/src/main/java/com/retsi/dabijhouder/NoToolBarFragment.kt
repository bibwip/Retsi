package com.retsi.dabijhouder

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class NoToolBarFragment(layoutId:Int) : Fragment(layoutId) {

    override fun onResume() {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}