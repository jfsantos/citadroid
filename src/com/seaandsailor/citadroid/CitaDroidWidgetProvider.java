/*
 * Widget para mostrar quotes na homescreen.
 * A configuração padrão é de um update a cada 30 minutos (mínimo permitido).
 */

package com.seaandsailor.citadroid;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

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
		Intent intent = new Intent(context, CitaDroid.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.citadroid_widget);
		views.setTextViewText(R.id.linktoapp, quote);
		
		views.setOnClickPendingIntent(R.id.linktoapp, pendingIntent);
		appWidgetManager.updateAppWidget(id, views);
	}

}
