package in.aviaryan.cfbuddy.ui;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.adapter.RecentBlogsAdapter;
import in.aviaryan.cfbuddy.data.Contract;
import in.aviaryan.cfbuddy.model.Blog;
import in.aviaryan.cfbuddy.parser.RecentBlogsParser;
import in.aviaryan.cfbuddy.utils.Helper;
import in.aviaryan.cfbuddy.utils.VolleyErrorListener;


public class RecentBlogsFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    RequestQueue queue;
    private final String TAG = "CFLOG_RBF";
    private final String URL = Contract.Cache.makeUIDFromRealUri("codeforces.com/api/recentActions");
    RecyclerView mRecyclerView;
    RecentBlogsAdapter mAdapter;
    RecentBlogsParser rbp;
    private LinearLayoutManager mLinearLayoutManager;

    public RecentBlogsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(getContext());
        return inflater.inflate(R.layout.fragment_recent_blogs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.recent_blogs);
        // adapter
        mAdapter = new RecentBlogsAdapter(getContext(), new ArrayList<Blog>());
        // recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rb_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        // in the end so that adapter is created
        fetchRecentBlogs();

        getLoaderManager().initLoader(0, null, this);
    }

    public void fetchRecentBlogs(){
        VolleyErrorListener vel = new VolleyErrorListener(getContext());
        rbp = new RecentBlogsParser(this);
        // cache
        updateDisplayFromCache(Helper.getCache(getContext().getContentResolver(), URL));
        // request
        String url = "http://codeforces.com/api/recentActions?maxCount=100";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, rbp, vel);
        queue.add(jsonObjectRequest);
//        queue.start();
    }

    public void updateDisplayFromCache(String cache){
        if (cache != null){
            updateDisplay( rbp.parse(rbp.stringToJson(cache)) );
        }
    }

    public void updateDisplay(ArrayList<Blog> blogs) {
        Log.d(TAG, blogs.toString());
        mAdapter.blogs = blogs;
        mAdapter.notifyDataSetChanged();
    }

    public void updateCache(String data){
        Uri uri = Contract.Cache.URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Cache.COLUMN_UID, URL);
        contentValues.put(Contract.Cache.COLUMN_DATA, data);
        contentValues.put(Contract.Cache.COLUMN_TIME, "");
        getContext().getContentResolver().insert(uri, contentValues);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return Helper.getCacheLoader(getContext(), URL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        updateDisplayFromCache(Helper.getCacheFromCursor(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
