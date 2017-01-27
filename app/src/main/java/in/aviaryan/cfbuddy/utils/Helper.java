package in.aviaryan.cfbuddy.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import org.jsoup.Jsoup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
            return diffSecs/(60*60*24*365) + " years ago";
        }
    }

    public static String humanizeSecondsAccurate(int seconds){
        return padWithZeros((seconds/3600) + "", 2)
                    + ":" + padWithZeros((seconds%60) + "", 2) + " hours";
    }

    public static String dateToStr(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime() + 60*60*5 * 1000);  // don't know why
        // but 5 hours timezone is added maybe MST*2
        date = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(date) + " UTC";
    }

    public static String padWithZeros(String s, int zc){
        while (s.length() < zc){
            s = "0" + s;
        }
        return s;
    }
}
