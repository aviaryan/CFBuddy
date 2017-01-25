package in.aviaryan.cfbuddy.parser;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.volley.Response;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import in.aviaryan.cfbuddy.model.Problem;


public class ProblemsParser extends BaseParser implements Response.Listener<JSONObject> {
    private final String TAG = "CFLOG_PP";
    private Fragment fragment;

    public ProblemsParser(Fragment fragment){
        this.fragment = fragment;
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d(TAG, response.toString());
        ArrayList<Problem> problems = parse(response);
    }

    public ArrayList<Problem> parse(JSONObject jsonObject){
        ArrayList<Problem> problems = new ArrayList<>();
        HashMap<String, Integer> problemToSolved = new HashMap<>();

        try {
            JSONObject result = jsonObject.getJSONObject("result");
            // stats first
            JSONArray probStats = result.getJSONArray("problemStatistics");
            for (int i=0; i < probStats.length(); i++){
                JSONObject jsObj = probStats.getJSONObject(i);
                problemToSolved.put(
                        jsObj.getInt("contestId") + jsObj.getString("index"),
                        jsObj.getInt("solvedCount")
                );
            }
            // problems next
            JSONArray probArray = result.getJSONArray("problems");
            for (int i=0; i<probArray.length(); i++){
                JSONObject jsObj = probArray.getJSONObject(i);
                Problem problem = new Problem();
                problem.contestId = jsObj.getInt("contestId");
                problem.index = jsObj.getString("index");
                problem.name = jsObj.getString("name");
                problem.problemText = null;
                problem.solvedCount = problemToSolved.get(problem.contestId + problem.name);
                // tags
                JSONArray tagsArray = jsObj.getJSONArray("tags");
                ArrayList<String> tags = new ArrayList<>();
                for (int j=0; j<tagsArray.length(); j++){
                    tags.add(tagsArray.getString(j));
                }
                problem.tags = tags;

                problems.add(problem);
            }

            return problems;
        } catch (JSONException e){
            FirebaseCrash.report(e);
            Log.d(TAG, e.toString());
            return null;
        }
    }
}
