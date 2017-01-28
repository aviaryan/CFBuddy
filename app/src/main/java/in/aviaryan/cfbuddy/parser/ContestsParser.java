package in.aviaryan.cfbuddy.parser;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.volley.Response;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import in.aviaryan.cfbuddy.model.Contest;
import in.aviaryan.cfbuddy.ui.ContestsFragment;


public class ContestsParser extends BaseParser implements Response.Listener<JSONObject> {
    private final String TAG = "CFLOG_CP";
    private Fragment fragment;

    public ContestsParser(Fragment fragment){
        this.fragment = fragment;
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d(TAG, response.toString());
        ArrayList<Contest> contests = parse(response);
        if (contests != null){
            ((ContestsFragment) fragment).updateDisplay(contests);
            ((ContestsFragment) fragment).updateCache(response.toString());
        }
    }

    public ArrayList<Contest> parse(JSONObject jsonObject){
        ArrayList<Contest> contests = new ArrayList<>();
        try {
            JSONArray result = jsonObject.getJSONArray("result");
            for (int i=0; i<result.length(); i++){
                JSONObject jsObj = result.getJSONObject(i);
                Contest contest = new Contest();
                contest.id = jsObj.getInt("id");
                contest.name = jsObj.getString("name");
                contest.durationSeconds = jsObj.getInt("durationSeconds");
                contest.type = jsObj.getString("type");
                if (jsObj.has("startTimeSeconds")){
                    Date date = new Date();
                    date.setTime(jsObj.getLong("startTimeSeconds") * 1000);  // in millis
                    contest.startTime = date;
                }
                contests.add(contest);
                if (i == 300)  // no need to show more contests, they are ancient
                    break;
            }
            return contests;
        } catch (JSONException e){
            FirebaseCrash.report(e);
            Log.d(TAG, e.toString());
            return null;
        }
    }
}
