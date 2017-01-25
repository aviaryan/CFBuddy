package in.aviaryan.cfbuddy.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.crash.FirebaseCrash;


public class VolleyErrorListener implements Response.ErrorListener {

    private final String TAG = "CFLOG_VER";
    Context context;

    public VolleyErrorListener(Context c){
        this.context = c;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        FirebaseCrash.log("Volley error " + error.toString());
        Log.d(TAG, error.toString());
    }
}
