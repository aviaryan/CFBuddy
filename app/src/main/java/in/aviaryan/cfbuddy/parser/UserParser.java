package in.aviaryan.cfbuddy.parser;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import in.aviaryan.cfbuddy.model.User;


public class UserParser extends BaseParser implements Response.Listener<JSONObject> {
    private final String TAG = "CFLOG_PP";
    private Fragment fragment;

    public UserParser(Fragment fragment){
        this.fragment = fragment;
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d(TAG, response.toString());
        User user = new User();

        try {
            JSONArray result = response.getJSONArray("result");
            JSONObject profileObject = result.getJSONObject(0);
            // set name
            user.name = "";
            if (profileObject.has("firstName")) {
                user.name = profileObject.getString("firstName") + " ";
            }
            if (profileObject.has("lastName")){
                user.name += profileObject.getString("lastName");
            }
            user.country = safeGetItem(profileObject, "country");
            user.organisation = safeGetItem(profileObject, "organization");
            user.avatar = profileObject.getString("titlePhoto");
            user.handle = profileObject.getString("handle");
            user.rank = profileObject.getString("rank");
            user.rating = profileObject.getInt("rating");
            user.maxRank = profileObject.getString("maxRank");
            user.maxRating = profileObject.getInt("maxRating");

        } catch (JSONException e){
            Log.d(TAG, e.toString());
        }
    }
}
