package com.example.homemaintenancetracker

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class MyWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Iterate through each widget and update them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        // Update the widget content
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            // Create an Intent to launch MainActivity when the widget button is clicked
            val intent = Intent(context, MainActivity::class.java)

            // Create a PendingIntent to launch MainActivity
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Create a RemoteViews object for your widget's layout
            val views = RemoteViews(context.packageName, R.layout.widget_layout).apply {
                // Set the task title and description (static text for now)
                setTextViewText(R.id.widgetTaskTitle, "Upcoming Task")
                setTextViewText(R.id.widgetTaskDescription, "You have tasks to complete!")

                // Set the PendingIntent to launch the app when the button is clicked
                setOnClickPendingIntent(R.id.widgetOpenAppButton, pendingIntent)
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
