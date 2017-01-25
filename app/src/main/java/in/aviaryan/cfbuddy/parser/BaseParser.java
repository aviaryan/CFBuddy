package in.aviaryan.cfbuddy.parser;


import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseParser {
    /*
     * Gets the object from the JSONObject safely,
     * returning null if needed
     */
    public String safeGetItem(JSONObject jsObj, String key){
        try {
            if (jsObj.has(key)){
                return jsObj.getString(key);
            } else
                return null;
        } catch (JSONException e){
            FirebaseCrash.report(e);
            return null;
        }
    }
}
