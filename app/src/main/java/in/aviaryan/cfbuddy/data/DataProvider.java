package in.aviaryan.cfbuddy.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.crash.FirebaseCrash;

import in.aviaryan.cfbuddy.utils.CacheCleaner;


public class DataProvider extends ContentProvider {

    static final int CACHE_ITEM = 1;
    static final int CACHE = 2;

    static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH_CACHE_WITH_ITEM, CACHE_ITEM);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH_CACHE, CACHE);
    }

    static final String TAG = "CFLOG_DP";
    SQLiteHelper sqLiteHelper;

    @Override
    public boolean onCreate() {
        sqLiteHelper = new SQLiteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)){
            case CACHE_ITEM:
                retCursor = db.query(
                        Contract.Cache.TABLE_NAME,
                        projection,
                        Contract.Cache.COLUMN_UID + " = ?",
                        new String[]{Contract.Cache.getUIDFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                FirebaseCrash.log("URI error: " + uri);
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        Uri retUri;

        switch (uriMatcher.match(uri)){
            case CACHE:
                retUri = Contract.Cache.makeUriForUID(contentValues.getAsString(Contract.Cache.COLUMN_UID));
                // delete old data
                delete(retUri, null, null);
                // insert new
                db.insert(
                        Contract.Cache.TABLE_NAME,
                        null,
                        contentValues
                );
                break;
            default:
                FirebaseCrash.log("URI error: " + uri);
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }
        // not sure uri or retUri here
        getContext().getContentResolver().notifyChange(retUri, null);
        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        int rowsDeleted;

        switch (uriMatcher.match(uri)){
            case CACHE_ITEM:
                rowsDeleted = db.delete(
                        Contract.Cache.TABLE_NAME,
                        Contract.Cache.COLUMN_UID + " = ?",
                        new String[]{Contract.Cache.getUIDFromUri(uri)}
                );
                break;
            case CACHE:
                rowsDeleted = db.delete(
                        Contract.Cache.TABLE_NAME,
                        Contract.Cache.COLUMN_TIME + " < ?",
                        new String[]{CacheCleaner.getThresholdTime()+""}
                );
                break;
            default:
                FirebaseCrash.log("URI error: " + uri);
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        FirebaseCrash.log("Call to update in provider: " + uri);
        return 0;
    }
}
