package com.retsi.dabijhouder

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews

class SmallWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appwidgetId in appWidgetIds) {

            val db = DatabaseHelper(context)
            val items = db.getAllAssignments(context)

            val views = RemoteViews(context.packageName, R.layout.widget_opdracht_item)
            if (items.size > 0) {
                views.setTextViewText(R.id.widget_item_titel, items[0].titel)
                views.setTextViewText(R.id.widget_item_datum, items[0].datum)
                views.setTextViewText(R.id.widget_item_typeOpdracht, items[0].typeOpdracht)
                views.setTextViewText(R.id.widget_item_vaknaam, items[0].vakNaam)
                views.setViewVisibility(R.id.widget_item_empty_text, View.GONE)
            } else {
                views.setViewVisibility(R.id.widget_item_empty_text, View.VISIBLE)
                views.setViewVisibility(R.id.widget_item_titel, View.GONE)
                views.setViewVisibility(R.id.widget_item_datum, View.GONE)
                views.setViewVisibility(R.id.widget_item_typeOpdracht, View.GONE)
                views.setViewVisibility(R.id.widget_item_vaknaam, View.GONE)
            }
            appWidgetManager.updateAppWidget(appwidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val db = DatabaseHelper(context)
        val items = db.getAllAssignments(context)
        val views = RemoteViews(context.packageName, R.layout.widget_opdracht_item)
        if (items.size > 0) {
            views.setTextViewText(R.id.widget_item_titel, items[0].titel)
            views.setTextViewText(R.id.widget_item_datum, items[0].datum)
            views.setTextViewText(R.id.widget_item_typeOpdracht, items[0].typeOpdracht)
            views.setTextViewText(R.id.widget_item_vaknaam, items[0].vakNaam)
            views.setViewVisibility(R.id.widget_item_empty_text, View.GONE)
        } else {
            views.setViewVisibility(R.id.widget_item_empty_text, View.VISIBLE)
            views.setViewVisibility(R.id.widget_item_titel, View.GONE)
            views.setViewVisibility(R.id.widget_item_datum, View.GONE)
            views.setViewVisibility(R.id.widget_item_typeOpdracht, View.GONE)
            views.setViewVisibility(R.id.widget_item_vaknaam, View.GONE)
        }
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, BigWidgetProvider::class.java))
        appWidgetManager.updateAppWidget(ids, views)

        super.onReceive(context, intent)
    }
}