package com.retsi.dabijhouder

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

class BigWidgetProvider : AppWidgetProvider() {
    val ACTION_REFRESH = "actionrefresh"

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appwidgetId in appWidgetIds) {
            val serviceIntent = Intent(context, BigWidgetService::class.java)
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appwidgetId)
            serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))

            val views = RemoteViews(context.packageName, R.layout.big_widget)
            views.setRemoteAdapter(R.id.big_widget_stack_view, serviceIntent)
            views.setEmptyView(R.id.big_widget_stack_view, R.id.big_widget_empty_view)

            appWidgetManager.updateAppWidget(appwidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appwidgetId, R.id.big_widget_stack_view)
        }
    }
    override fun onReceive(context: Context, intent: Intent) {

        val appWidgetManager = AppWidgetManager.getInstance(context)

        val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, BigWidgetProvider::class.java))
        appWidgetManager.notifyAppWidgetViewDataChanged(
            ids,
            R.id.big_widget_stack_view
        )

        super.onReceive(context, intent)
    }
}