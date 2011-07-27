/*
 * 
 *  
 */

package com.seaandsailor.citadroid;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

import android.widget.RemoteViews;

public class CitaDroidWidgetProvider extends AppWidgetProvider {
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		QuoteDb quotes = new QuoteDb(context.getResources().openRawResource(R.raw.batema));
		for (int id : appWidgetIds) {
			String quote = quotes.getQuote();
			updateAppWidget(context, appWidgetManager, id, quote);
		}
	}

	private void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int id, String quote) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.citadroid_widget);
		views.setTextViewText(R.id.linktoapp, quote);
		appWidgetManager.updateAppWidget(id, views);
	}

}
