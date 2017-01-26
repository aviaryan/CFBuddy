package in.aviaryan.cfbuddy.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;

import org.jsoup.Jsoup;

import in.aviaryan.cfbuddy.data.Contract;


public class Helper {
    private static final String TAG = "CFLOG_HLP";

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    public static String getCache(ContentResolver contentResolver, String url){
        Cursor cursor = contentResolver.query(
                Contract.Cache.makeUriForUID(url),
                null, null, null, null
        );
        if (cursor == null)
            return null;
        if (cursor.moveToNext()){
            String data = cursor.getString(Contract.Cache.POSITION_DATA);
            Log.d(TAG, data);
            cursor.close();
            return data;
        } else {
            cursor.close();
            return null;
        }
    }
}
