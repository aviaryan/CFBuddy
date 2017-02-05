package in.aviaryan.cfbuddy.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import in.aviaryan.cfbuddy.model.Problem;
import in.aviaryan.cfbuddy.ui.ProblemsFragment;

// https://code.tutsplus.com/tutorials/android-fundamentals-intentservice-basics--mobile-6183

public class MyIntentService extends IntentService {
    public static final String ACTION_PROBLEM_FILTER = "in.aviaryan.cfbuddy.service.action.PROBLEM_FILTER";
    private static final String TAG = "CFLOG_MIS";

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROBLEM_FILTER.equals(action)) {
                final String param1 = intent.getStringExtra("query");
                handleActionFoo(param1);
                respondFoo();
            }
        }
    }

    private void handleActionFoo(String query) {
        Log.d(TAG, "handling foo");

        String [] queries = query.split(";");
        ArrayList<Problem> newProblems = new ArrayList<>();
        Boolean fail;
        for (Problem problem: ProblemsFragment.fullProblems){
            fail = false;
            for (String q: queries){
                if (!(problem.tags.contains(q) || (problem.contestId + problem.index).contains(q))) {
                    fail = true;
                    break;
                }
            }
            if (!fail)
                newProblems.add(problem);
        }
        ProblemsFragment.newProblems.clear();
        ProblemsFragment.newProblems.addAll(newProblems);
    }

    private void respondFoo(){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ProblemsFragment.ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
//        broadcastIntent.putExtra(PARAM_OUT_MSG, resultTxt);
        sendBroadcast(broadcastIntent);
    }
}
