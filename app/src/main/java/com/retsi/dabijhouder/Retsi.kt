package com.retsi.dabijhouder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class Retsi : Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val huiswerkChannel = NotificationChannel(
                getString(R.string.channel_huiswerk_id),
                getString(R.string.channel_name_huiswerk),
                NotificationManager.IMPORTANCE_HIGH)
            huiswerkChannel.description = getString(R.string.channel_description_huiswerk)

//            val eindopdrachtChannel = NotificationChannel(
//                getString(R.string.channel_eindopdracht_id),
//                getString(R.string.channel_name_eindopdracht),
//                NotificationManager.IMPORTANCE_HIGH)
//            eindopdrachtChannel.description = getString(R.string.channel_description_eindopdracht)
//
//            val toetsChannel = NotificationChannel(
//                getString(R.string.channel_toets_id),
//                getString(R.string.channel_name_toets),
//                NotificationManager.IMPORTANCE_HIGH)
//            toetsChannel.description = getString(R.string.channel_description_toets)
//
//            val overigChannel = NotificationChannel(
//                getString(R.string.channel_overig_id),
//                getString(R.string.channel_name_overig),
//                NotificationManager.IMPORTANCE_HIGH)
//            overigChannel.description = getString(R.string.channel_description_overig)

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(huiswerkChannel)
//            manager.createNotificationChannel(eindopdrachtChannel)
//            manager.createNotificationChannel(toetsChannel)
//            manager.createNotificationChannel(overigChannel)
        }
    }
}