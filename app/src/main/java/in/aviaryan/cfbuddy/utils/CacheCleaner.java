package in.aviaryan.cfbuddy.utils;

import android.content.ContentResolver;
import in.aviaryan.cfbuddy.data.Contract;


public class CacheCleaner {

    public static long getThresholdTime(){
        Long curTime = System.currentTimeMillis();
        Long timespan = 1000L * 3600L * 24L * 10L;  // 10 days
        Long thresTime = curTime - timespan;
        return thresTime;
    }

    public static void cleanCache(ContentResolver contentResolver){
        contentResolver.delete(Contract.Cache.URI, null, null);
    }

}
