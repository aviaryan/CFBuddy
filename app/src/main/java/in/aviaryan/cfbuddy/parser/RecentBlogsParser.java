package in.aviaryan.cfbuddy.parser;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.android.volley.Response;
import org.json.JSONObject;

import in.aviaryan.cfbuddy.ui.RecentBlogsFragment;


public class RecentBlogsParser implements Response.Listener<JSONObject> {
    private final String LOG_TAG = "CFLOG_RBP";
    private Fragment fragment;

    public RecentBlogsParser(Fragment fragment){
        this.fragment = fragment;
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d(LOG_TAG, response.toString());

        ((RecentBlogsFragment) fragment).updateData(response.toString());
    }
}
