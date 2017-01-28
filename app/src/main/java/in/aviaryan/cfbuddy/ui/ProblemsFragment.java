package in.aviaryan.cfbuddy.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import in.aviaryan.cfbuddy.service.MyIntentService;
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
    SearchView searchView;
    Boolean inSearch = false;
    Boolean blockUpdate = false;
    private static String query;
    private static Parcelable rvParcel;
    public ResponseReceiver receiver;
    private LinearLayoutManager mLinearLayoutManager;
    // used to store unfiltered list
    public static ArrayList<Problem> fullProblems;
    // used to store filtered list
    public static ArrayList<Problem> newProblems;

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
        fullProblems = new ArrayList<>();
        mAdapter = new ProblemsAdapter(getContext(), new ArrayList<Problem>());
        // recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.pb_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        getContext().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(receiver);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Activity created");
        // in the end so that adapter is created
        fetchProblems();
    }

    @Override
    public void onPause() {
        super.onPause();
        rvParcel = mLinearLayoutManager.onSaveInstanceState();
        // getActivity().overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
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
//        Log.d(TAG, problems.toString());
        if (blockUpdate && inSearch)  // happens when async call of data request
            return;
        if (!blockUpdate)  // happens only when async call
            fullProblems = problems;
        mAdapter.problems = problems;
        mAdapter.notifyDataSetChanged();
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
        // Already updated actually
        updateDisplayFromCache(Helper.getCacheFromCursor(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_problems, menu);
        super.onCreateOptionsMenu(menu, inflater);

        // Thanks
        // http://stackoverflow.com/questions/34291453/adding-searchview-in-fragment
        MenuItem item = menu.findItem(R.id.action_filter_problem);
        SearchView searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setQueryHint(getString(R.string.problem_search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, query);
                filterProblems(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // http://stackoverflow.com/questions/9327826/searchviews-oncloselistener-doesnt-work
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "closed");
                inSearch = false;
                blockUpdate = false;
                query = null;
                updateDisplay(fullProblems);
                return true;
            }
        });

        if (query != null){ // restore state
            searchView.setQuery(query, true);
            MenuItemCompat.expandActionView(item);
            searchView.setQuery(query, false);
        } else {
            restoreScroll();  // normal restores
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.action_filter_problem){
//        }
        return super.onOptionsItemSelected(item);
    }

    private void restoreScroll(){
        if (rvParcel != null){
            mLinearLayoutManager.onRestoreInstanceState(rvParcel);
            rvParcel = null;
        }
    }

    private void filterProblems(String query){
        if (query.equals(""))
            return;
        ProblemsFragment.query = query;
        blockUpdate = true;
        inSearch = true;
        Intent msgIntent = new Intent(getContext(), MyIntentService.class);
        msgIntent.putExtra("query", query);
        msgIntent.setAction(MyIntentService.ACTION_PROBLEM_FILTER);
        getContext().startService(msgIntent);
    }

    // intent service
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "in.aviaryan.cfbuddy.intent.action.PROBLEM_FILTER";

        @Override
        public void onReceive(Context context, Intent intent) {
            inSearch = false;
            updateDisplay(newProblems);
            restoreScroll();  // in case search, then detail, then restore
            inSearch = true;
        }
    }
}
