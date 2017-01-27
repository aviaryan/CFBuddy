package in.aviaryan.cfbuddy.ui;


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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.adapter.ProblemsAdapter;
import in.aviaryan.cfbuddy.data.Contract;
import in.aviaryan.cfbuddy.model.Problem;
import in.aviaryan.cfbuddy.parser.ProblemsParser;
import in.aviaryan.cfbuddy.utils.Helper;
import in.aviaryan.cfbuddy.utils.VolleyErrorListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProblemsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    RequestQueue queue;
    private final String TAG = "CFLOG_PBF";
    private final String URL = Contract.Cache.makeUIDFromRealUri("codeforces.com/api/problemset.problems");
    RecyclerView mRecyclerView;
    ProblemsParser pp;
    ProblemsAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    public ProblemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(getContext());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_problems, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.title_problems);
        // adapter
        mAdapter = new ProblemsAdapter(getContext(), new ArrayList<Problem>());
        // recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.pb_recycler_view);
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
        fetchProblems();
    }

    public void fetchProblems(){
        VolleyErrorListener vel = new VolleyErrorListener(getContext());
        pp = new ProblemsParser(this);
        // cache
        updateDisplayFromCache(Helper.getCache(getContext().getContentResolver(), URL));
        // request
        String url = "http://codeforces.com/api/problemset.problems";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, pp, vel);

        //Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions.
        //Volley does retry for you if you have specified the policy.
        //http://stackoverflow.com/questions/17094718/change-volley-timeout-duration
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        queue.add(jsonObjectRequest);
    }

    public void updateDisplayFromCache(String cache){
        if (cache != null){
            updateDisplay( pp.parse(pp.stringToJson(cache)) );
        }
    }

    public void updateDisplay(ArrayList<Problem> problems) {
        Log.d(TAG, problems.toString());
        mAdapter.problems = problems;
        mAdapter.notifyDataSetChanged();
    }

    public void updateCache(String data){
        if (getContext() == null)
            return;
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
