package in.aviaryan.cfbuddy.ui;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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
import in.aviaryan.cfbuddy.adapter.ContestsAdapter;
import in.aviaryan.cfbuddy.data.Contract;
import in.aviaryan.cfbuddy.model.Contest;
import in.aviaryan.cfbuddy.parser.ContestsParser;
import in.aviaryan.cfbuddy.utils.Helper;
import in.aviaryan.cfbuddy.utils.VolleyErrorListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContestsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    RequestQueue queue;
    private final String TAG = "CFLOG_CSF";
    private final String URL = Contract.Cache.makeUIDFromRealUri("codeforces.com/api/contest.list");
    RecyclerView mRecyclerView;
    ContestsParser cp;
    ContestsAdapter mAdapter;
    private static Parcelable rvParcel;
    private LinearLayoutManager mLinearLayoutManager;

    public ContestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(getContext());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contests, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.title_contests);
        // adapter
        mAdapter = new ContestsAdapter(getContext(), new ArrayList<Contest>());
        // recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.cs_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Activity created");
        // in the end so that adapter is created
        fetchContests();
    }

    @Override
    public void onPause() {
        super.onPause();
        rvParcel = mLinearLayoutManager.onSaveInstanceState();
    }

    public void fetchContests(){
        VolleyErrorListener vel = new VolleyErrorListener(getContext());
        cp = new ContestsParser(this);
        // cache
        updateDisplayFromCache(Helper.getCache(getContext().getContentResolver(), URL));
        // request
        String url = "http://codeforces.com/api/contest.list";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, cp, vel);
        queue.add(jsonObjectRequest);
    }

    public void updateDisplayFromCache(String cache){
        if (cache != null){
            updateDisplay( cp.parse(cp.stringToJson(cache)) );
        }
    }

    public void updateDisplay(ArrayList<Contest> contests) {
        Log.d(TAG, contests.toString());
        mAdapter.contests = contests;
        mAdapter.notifyDataSetChanged();

        restoreScroll();  // useful when user navigates to other fragment
        // and then comes back
    }

    public void updateCache(String data){
        if (getContext() == null)
            return;
        Helper.updateCache(data, getContext().getContentResolver(), URL);
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

    private void restoreScroll(){
        if (rvParcel != null){
            mLinearLayoutManager.onRestoreInstanceState(rvParcel);
            // rvParcel = null;  // view doesnt change so not needed
        }
    }
}
