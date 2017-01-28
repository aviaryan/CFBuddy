package in.aviaryan.cfbuddy.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.crash.FirebaseCrash;

import in.aviaryan.cfbuddy.R;


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
        if (error.networkResponse != null && error.networkResponse.statusCode == 400){
            // in no other case, this error
            Toast.makeText(context, R.string.msg_server_400_error, Toast.LENGTH_SHORT).show();
        }
    }
}
