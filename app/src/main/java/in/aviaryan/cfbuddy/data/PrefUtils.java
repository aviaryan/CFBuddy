package in.aviaryan.cfbuddy.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class PrefUtils {

    private SharedPreferences sharedPref;
    private static String HANDLE_KEY = "handle";
    private static String NOTFIS_KEY = "challenge_notifs";

    public PrefUtils(Context context){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getHandle(){
        return sharedPref.getString(HANDLE_KEY, "aviaryan");
    }

    public Boolean getNotificationSetting(){
        return sharedPref.getBoolean(NOTFIS_KEY, true);
    }
}
