package in.aviaryan.cfbuddy.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import in.aviaryan.cfbuddy.R;
import in.aviaryan.cfbuddy.data.Contract;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProblemsFragment extends Fragment {

    RequestQueue queue;
    private final String TAG = "CFLOG_PBF";
    private final String URL = Contract.Cache.makeUIDFromRealUri("codeforces.com/api/problemset.problems");

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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
