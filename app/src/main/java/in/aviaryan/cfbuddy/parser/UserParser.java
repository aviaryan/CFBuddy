package in.aviaryan.cfbuddy.parser;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import in.aviaryan.cfbuddy.model.User;
import in.aviaryan.cfbuddy.ui.UserActivity;


public class UserParser extends BaseParser implements Response.Listener<JSONObject> {
    private final String TAG = "CFLOG_UP";
    private AppCompatActivity activity;

    public UserParser(AppCompatActivity activity){
        this.activity = activity;
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d(TAG, response.toString());
        User user = parse(response);
        if (user != null){
            ((UserActivity) activity).updateDisplay(user);
            ((UserActivity) activity).updateCache(response.toString());
        }
    }

    public User parse(JSONObject jsonObject){
        User user = new User();

        try {
            JSONArray result = jsonObject.getJSONArray("result");
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
            user.smallAvatar = profileObject.getString("avatar");
            user.handle = profileObject.getString("handle");
            user.rank = profileObject.getString("rank");
            user.rating = profileObject.getInt("rating");
            user.maxRank = profileObject.getString("maxRank");
            user.maxRating = profileObject.getInt("maxRating");

            return user;
        } catch (JSONException e){
            FirebaseCrash.report(e);
            Log.d(TAG, e.toString());
            return null;
        }
    }
}
