package com.retsi.dabijhouder

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import java.util.*

class BigWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent): RemoteViewsFactory {
        return BigWidgetFactory(applicationContext, p0)
    }

    class BigWidgetFactory(val context: Context, intent: Intent) : RemoteViewsFactory {

        private var appWidgetId: Int? = null
        private val items = ArrayList<OpdrachtItem>()
        private val db = DatabaseHelper(context)

        init {
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        override fun onCreate() {

        }
        override fun onDataSetChanged() {
            items.clear()
            items.addAll(db.getAllAssignments(context))
        }

        override fun onDestroy() {
            db.close()
        }

        override fun getCount(): Int {
            return items.size
        }

        override fun getViewAt(p0: Int): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_opdracht_item)
            if (items.size > 0) {
                views.setTextViewText(R.id.widget_item_titel, items[p0].titel)
                views.setTextViewText(R.id.widget_item_datum, items[p0].datum)
                views.setTextViewText(R.id.widget_item_typeOpdracht, items[p0].typeOpdracht)
                views.setTextViewText(R.id.widget_item_vaknaam, items[p0].vakNaam)
            }
            return views
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }
    }
}