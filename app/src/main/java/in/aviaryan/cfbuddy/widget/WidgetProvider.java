package in.aviaryan.cfbuddy.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.widget.RemoteViews;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.ui.BlogDetailActivity;
import in.aviaryan.cfbuddy.ui.MainActivity;


public class WidgetProvider extends AppWidgetProvider {
    public static final String ACTION_DETAIL = "in.aviaryan.cfbuddy.widget.ACTION_DETAIL";
    public static final String EXTRA_STRING = "in.aviaryan.cfbuddy.widget.EXTRA_STRING";
    public static final String UPDATE_WIDGET = "in.aviaryan.cfbuddy.widget.UPDATE";
    public static String APP_OPEN = "in.aviaryan.cfbuddy.widget.APP_OPEN";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("WDP", "widget provider recieve" + intent.getAction());
        if (intent.getAction().equals(ACTION_DETAIL)) {
            Parcelable item = intent.getExtras().getParcelable(EXTRA_STRING);
            // launch detail
            // http://stackoverflow.com/questions/11554085/start-activity-by-clicking-on-widget
            Intent detailIntent = new Intent(context, BlogDetailActivity.class);
            detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            detailIntent.putExtra("blog", item);
            context.startActivity(detailIntent);
            // end launch detail
        } else if (intent.getAction().equals(APP_OPEN)) {
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        } else if (intent.getAction().equals(UPDATE_WIDGET)) {
            // update widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int [] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.widgetCollectionList);
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews mView = initViews(context, appWidgetManager, widgetId);

            // Adding collection list item handler
            Intent onItemClick = new Intent(context, WidgetProvider.class);
            onItemClick.setAction(ACTION_DETAIL);
            onItemClick.setData(Uri.parse(onItemClick.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent onClickPendingIntent = PendingIntent
                    .getBroadcast(context, 0, onItemClick, PendingIntent.FLAG_UPDATE_CURRENT);
            mView.setPendingIntentTemplate(R.id.widgetCollectionList, onClickPendingIntent);

            // Add title app open event handler
            // http://stackoverflow.com/questions/14798073/button-click-event-for-android-widget
            Intent intent = new Intent(APP_OPEN);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mView.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, mView);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews initViews(Context context, AppWidgetManager widgetManager, int widgetId) {
        RemoteViews mView = new RemoteViews(context.getPackageName(), R.layout.widget_provider_layout);

        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        mView.setRemoteAdapter(widgetId, R.id.widgetCollectionList, intent);

        // The empty view is displayed when the collection has no items. It should be a sibling
        // of the collection view.
        mView.setEmptyView(R.id.widgetCollectionList, R.id.empty_view);

        return mView;
    }
}
