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
            val res: Cursor = db.allData

            while (res.moveToNext()) {
                var typeOpdracht = res.getString(1)
                val vak = res.getString(2)
                val titel = res.getString(3)
                val datum = res.getString(4)
                val bescrhijving = res.getString(5)
                val typeKey = res.getString(1)

                when (typeOpdracht) {
                    "Toets_key" -> typeOpdracht = context.getString(R.string.Toets)
                    "eindopdracht_key" -> typeOpdracht = context.getString(R.string.Eindopdracht)
                    "Huiswerk_key" -> typeOpdracht = context.getString(R.string.Huiswerk)
                    "overig_key" -> typeOpdracht = context.getString(R.string.overig)
                }

                val sList = datum.split("-").toTypedArray()
                val datumKey = (sList[2] + sList[1] + sList[0]).toInt()
                val opdracht = OpdrachtItem(typeOpdracht, vak, titel, datum, bescrhijving, datumKey,
                    typeKey)

                items.add(opdracht)
            }


            Collections.sort(items, object : Comparator<OpdrachtItem> {
                override fun compare(opdrachtItem: OpdrachtItem, t1: OpdrachtItem): Int {
                    return opdrachtItem.datumTagSorter.compareTo(t1.datumTagSorter)
                }
            })
        }

        override fun onDestroy() {
            db.close()
        }

        override fun getCount(): Int {
            return items.size
        }

        override fun getViewAt(p0: Int): RemoteViews {
            val views: RemoteViews =
                RemoteViews(context.packageName, R.layout.widget_opdracht_item)
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