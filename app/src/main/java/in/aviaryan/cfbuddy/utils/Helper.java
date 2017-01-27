package in.aviaryan.cfbuddy.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import org.jsoup.Jsoup;

import java.util.Date;

import in.aviaryan.cfbuddy.data.Contract;


public class Helper {
    private static final String TAG = "CFLOG_HLP";

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    public static String getCache(ContentResolver contentResolver, String url){
        return getCacheFromCursor(contentResolver.query(
                Contract.Cache.makeUriForUID(url),
                null, null, null, null
        ));
    }

    public static String getCacheFromCursor(Cursor cursor){
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

    public static CursorLoader getCacheLoader(Context context, String url){
        return new CursorLoader(context, Contract.Cache.makeUriForUID(url),
                null, null, null, null);
    }

    public static String humanizeTimeAgo(Date date){
        Date curDate = new Date();
        long diffSecs = (curDate.getTime() - date.getTime()) / 1000;
        if (diffSecs <= 0)
            return "moments ago";
        else if (diffSecs < 60){
            return diffSecs + " seconds ago";
        } else if (diffSecs < 60*60){
            return diffSecs/60 + " minutes ago";
        } else if (diffSecs < 60*60*24){
            return  diffSecs/(60*60) + " hours ago";
        } else if (diffSecs < 60*60*24*30){
            return diffSecs/(60*60*24) + " days ago";
        } else if (diffSecs < 60*60*24*30*24){
            return diffSecs/(60*60*24*30) + " months ago";
        } else {
            return diffSecs/(60*60*24*30*365) + " years ago";
        }
    }
}
