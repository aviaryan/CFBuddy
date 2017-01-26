package in.aviaryan.cfbuddy.ui;


import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import in.aviaryan.cfbuddy.data.Contract;
import in.aviaryan.cfbuddy.model.Blog;
import in.aviaryan.cfbuddy.parser.RecentBlogsParser;
import in.aviaryan.cfbuddy.utils.VolleyErrorListener;


public class RecentBlogsFragment extends Fragment {

    RequestQueue queue;
    private final String TAG = "CFLOG_RBF";
    private final String URL = Contract.Cache.makeUIDFromRealUri("codeforces.com/api/recentActions");

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
        fetchRecentBlogs();
    }

    public void fetchRecentBlogs(){
        VolleyErrorListener vel = new VolleyErrorListener(getContext());
        RecentBlogsParser rbp = new RecentBlogsParser(this);
        String url = "http://codeforces.com/api/recentActions?maxCount=50";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, rbp, vel);
        queue.add(jsonObjectRequest);
        queue.start();
    }

    public void updateData(ArrayList<Blog> blogs) {
        Log.d(TAG, blogs.toString());
        for (Blog b: blogs) {
            Log.d(TAG, b.title);
        }
    }

    public void updateCache(String data){
        Uri uri = Contract.Cache.URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Cache.COLUMN_UID, URL);
        contentValues.put(Contract.Cache.COLUMN_DATA, data);
        contentValues.put(Contract.Cache.COLUMN_TIME, "");
        getContext().getContentResolver().insert(uri, contentValues);
    }
}
