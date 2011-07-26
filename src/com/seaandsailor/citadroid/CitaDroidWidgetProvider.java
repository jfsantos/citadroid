/*
 * Baseado no exemplo SimpleWiktionary, que é disponibilizado com a SDK sob a licença Apache.
 *  
 */

package com.seaandsailor.citadroid;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.widget.RemoteViews;

public class CitaDroidWidgetProvider extends AppWidgetProvider {
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// To prevent any ANR timeouts, we perform the update in a service
        context.startService(new Intent(context, UpdateService.class));
    }
	
	public static class UpdateService extends Service {
        @Override
        public void onStart(Intent intent, int startId) {
            // Build the widget update for now
            RemoteViews updateViews = buildUpdate(this);

            // Push update for this widget to the home screen
            ComponentName thisWidget = new ComponentName(this, CitaDroidWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, updateViews);
        }

        /**
         * Build a widget update to show a new quotation
         */
        public RemoteViews buildUpdate(Context context) {
            // Pick out month names from resources
            Resources res = context.getResources();
            QuoteDb quotes = new QuoteDb(res.openRawResource(R.raw.batema));
            // Build an update that holds the updated widget contents
            RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.citadroid_widget);
            updateViews.setTextViewText(R.id.linktoapp, quotes.getQuote());
                
                // When user clicks on widget, do nothing
               /* String definePage = res.getString(R.string.template_define_url,
                        Uri.encode(wordTitle));
                Intent defineIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(definePage));
                PendingIntent pendingIntent = PendingIntent.getActivity(context,
                        0  no requestCode , defineIntent, 0  no flags );
                updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);*/
            return updateViews;
        }

        @Override
        public IBinder onBind(Intent intent) {
            // We don't need to bind to this service
            return null;
        }
    }
	
}
