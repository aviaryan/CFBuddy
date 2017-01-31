package in.aviaryan.cfbuddy.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SQLiteHelper extends SQLiteOpenHelper {
    static final String DB_NAME = "cfbuddy.db";
    private static final int DB_VERSION = 2;
    private static final String TAG = "CFLOG_SQLH";

    public SQLiteHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String builder = "CREATE TABLE " + Contract.Cache.TABLE_NAME + " ("
                + Contract.Cache.COLUMN_ID + " INTEGER PRIMARY KEY, "
                + Contract.Cache.COLUMN_UID + " TEXT NOT NULL UNIQUE, "
                + Contract.Cache.COLUMN_DATA + " TEXT NOT NULL, "
                + Contract.Cache.COLUMN_TIME + " INTEGER NOT NULL"
                + ")";
        Log.d(TAG, "creating table");
        sqLiteDatabase.execSQL(builder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.Cache.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
