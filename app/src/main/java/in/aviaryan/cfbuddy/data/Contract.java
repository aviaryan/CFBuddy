package in.aviaryan.cfbuddy.data;


import android.net.Uri;
import android.provider.BaseColumns;


public final class Contract {
    static final String AUTHORITY = "in.aviaryan.cfbuddy";
    static final String PATH_CACHE = "cache";
    static final String PATH_CACHE_WITH_ITEM = "cache/*";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract(){
        // nothing here
    }

    public static final class Cache implements BaseColumns {
        // replace slash with $ when storing

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_CACHE).build();
        public static final String COLUMN_UID = "uid";
        public static final String COLUMN_DATA = "data";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_ID = "_id";

        public static final int POSITION_DATA = 2;   // see SQLiteHelper

        public static final String TABLE_NAME = "cache";

        public static Uri makeUriForUID(String uid){
            return URI.buildUpon().appendPath(uid).build();
        }

        public static String getUIDFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }

        public static String makeUIDFromRealUri(String uri){
            return uri
                    .replace("http://", "")  // not needed
                    .replace("/", "$");  // path safe
        }
    }

}
