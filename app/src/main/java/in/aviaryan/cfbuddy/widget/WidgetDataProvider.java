package in.aviaryan.cfbuddy.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.parceler.Parcels;

import java.util.ArrayList;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.model.Blog;
import in.aviaryan.cfbuddy.parser.RecentBlogsParser;
import in.aviaryan.cfbuddy.ui.RecentBlogsFragment;
import in.aviaryan.cfbuddy.utils.Helper;


public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<Blog> mCollections = new ArrayList<>();
    private final String LOG_TAG = "WDP";
    Context mContext = null;

    public WidgetDataProvider(Context context, Intent intent){
        mContext = context;
        Log.v(LOG_TAG, "in wdp");
    }

    @Override
    public int getCount(){
        return mCollections.size();
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {
    }

    private void initData() {
        mCollections.clear();
        Log.v(LOG_TAG, "init data");
        Log.v(LOG_TAG, "cr done");
        String cache = Helper.getCache(mContext.getContentResolver(), RecentBlogsFragment.URL);
        RecentBlogsParser rb = new RecentBlogsParser(null);
        if (cache != null){
            mCollections = rb.parse(rb.stringToJson(cache));
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews mView = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item_quote);
        Log.v(LOG_TAG, "get view" + mCollections.size());
        Blog temp = mCollections.get(position);
        Log.d(LOG_TAG, temp.title);
        mView.setTextViewText(R.id.widget_blog_title, temp.title);
        mView.setTextViewText(R.id.widget_blog_author, temp.handle);
        mView.setTextViewText(R.id.widget_blog_time, Helper.humanizeTimeAgo(temp.time));

        // onClickFillIn
        Intent fillInIntent = new Intent();
        fillInIntent.setAction(WidgetProvider.ACTION_DETAIL);
        Bundle bundle = new Bundle();
        bundle.putParcelable(WidgetProvider.EXTRA_STRING, Parcels.wrap(mCollections.get(position)));
        fillInIntent.putExtras(bundle);
        mView.setOnClickFillInIntent(R.id.widget_listing, fillInIntent);

        return mView;
    }
}
