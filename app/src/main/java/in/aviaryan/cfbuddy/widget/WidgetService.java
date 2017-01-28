package in.aviaryan.cfbuddy.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;


public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        WidgetDataProvider dataProvider = new WidgetDataProvider(
                getApplicationContext(), intent);
        return dataProvider;
    }
}
