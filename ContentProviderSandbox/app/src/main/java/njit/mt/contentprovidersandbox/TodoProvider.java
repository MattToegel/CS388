package njit.mt.contentprovidersandbox;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public class TodoProvider extends ContentProvider {
    // adapted from https://www.tutorialspoint.com/android/android_content_providers.htm
    static final String TAG = "TodoProvider";
    //setting up variables for easier access
    static final String PROVIDER_NAME = "njit.mt.contentprovidersandbox.todoprovider";
    static final String URL = "content://" + PROVIDER_NAME + "/todos";
    static final Uri CONTENT_URI = Uri.parse(URL);
    //https://developer.android.com/guide/topics/providers/content-provider-creating#content-uri-patterns
    static final int TODOS = 1;
    static final int TODO_ID = 2;
    //define columns for easier access
    static final String _ID = "_id";
    static final String NOTE = "note";
    static final String CREATED = "created";
    static final String COMPLETED = "completed";
    static final String DONE = "done";
    //projection
    private static HashMap<String, String> PROJECTION_MAP;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "todos", TODOS);
        uriMatcher.addURI(PROVIDER_NAME, "todos/#", TODO_ID);
    }

    /**
     * Database specific constant declarations
     */

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "fun";
    static final String TABLE_NAME = "todos";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " note TEXT NOT NULL, " +
                    " created datetime default current_timestamp," +
                    " completed datetime default null," +
                    " done INTEGER DEFAULT 0);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */

    private static class DatabaseHelper extends SQLiteOpenHelper {
        final static String TAG = "DatabaseHelper";

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.v(TAG, "constructor()");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.v(TAG, "onCreate()");
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.v(TAG, "onUpgrade()");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public TodoProvider() {

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v(TAG, "delete()");
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case TODOS:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case TODO_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(TABLE_NAME, _ID + " = ?"
                        , new String[]{id});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        Log.v(TAG, "getType()");
        switch (uriMatcher.match(uri)) {
            //Get all todo records*/
            case TODOS:
                return "vnd.android.cursor.dir/vnd." + PROVIDER_NAME + "/" + TABLE_NAME;
            // Get a particular todo
            case TODO_ID:
                return "vnd.android.cursor.item/vnd." + PROVIDER_NAME + "/" + TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v(TAG, "insert()");
        // Add a new todo record
        long rowID = db.insert(TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public boolean onCreate() {
        Log.v(TAG, "onCreate()");
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a writeable database which will trigger its
         * creation if it doesn't already exist.
         */

        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.v(TAG, "query()");
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case TODOS:
                Log.v(TAG, "Todos");
                // qb.setProjectionMap(PROJECTION_MAP);
                selectionArgs = null;
                break;

            case TODO_ID:
                Log.v(TAG, "todo id");
                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (sortOrder == null || sortOrder == "") {
            sortOrder = CREATED;
        }

        Cursor c = qb.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.v(TAG, "update()");
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case TODOS:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;

            case TODO_ID:
                count = db.update(TABLE_NAME, values,
                        _ID + " = ?", new String[]{uri.getPathSegments().get(1)});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}