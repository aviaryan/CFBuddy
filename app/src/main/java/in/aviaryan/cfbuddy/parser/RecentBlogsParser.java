package in.aviaryan.cfbuddy.parser;

import android.util.Log;
import com.android.volley.Response;
import org.json.JSONObject;


public class RecentBlogsParser implements Response.Listener<JSONObject> {
    private final String LOG_TAG = "CFLOG_RBP";

    @Override
    public void onResponse(JSONObject response) {
        Log.d(LOG_TAG, response.toString());
    }
}
