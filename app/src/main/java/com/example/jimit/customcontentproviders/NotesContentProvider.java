package com.example.jimit.customcontentproviders;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class NotesContentProvider extends ContentProvider {

    private static final int NOTES_ALL = 1;
    private static final int NOTES_ONE = 2;

    private static final UriMatcher URI_MATCHER;
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(NotesMetaData.AUTHORITY, "notes", NOTES_ALL);
        URI_MATCHER.addURI(NotesMetaData.AUTHORITY, "notes/#", NOTES_ONE);
    }

    //Map table column
    private static final HashMap<String, String> NOTES_PROJECTION_MAP;
    static {
        NOTES_PROJECTION_MAP = new HashMap<>();
        NOTES_PROJECTION_MAP.put(NotesMetaData.NotesTable.ID, NotesMetaData.NotesTable.ID);
        NOTES_PROJECTION_MAP.put(NotesMetaData.NotesTable.TITLE, NotesMetaData.NotesTable.TITLE);
        NOTES_PROJECTION_MAP.put(NotesMetaData.NotesTable.CONTENT, NotesMetaData.NotesTable.CONTENT);
    }

    private NotesDBHelper mDbHelper;

    public NotesContentProvider() {
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new NotesDBHelper(getContext());
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = 0;
        switch (URI_MATCHER.match(uri)) {
            case NOTES_ALL:
                count = db.delete(NotesMetaData.NotesTable.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTES_ONE:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(NotesMetaData.NotesTable.TABLE_NAME,
                        NotesMetaData.NotesTable.ID + "=" + rowId + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")"),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (null != getContext())
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case NOTES_ALL:
                return NotesMetaData.CONTENT_TYPE_NOTES_ALL;
            case NOTES_ONE:
                return NotesMetaData.CONTENT_TYPE_NOTES_ONE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != NOTES_ALL) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(NotesMetaData.NotesTable.TABLE_NAME, null, values);
        if (rowId > 0) {
            Uri notesUri = ContentUris.withAppendedId(NotesMetaData.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(notesUri, null);
            return notesUri;
        }
        throw new IllegalArgumentException("<Illegal>Unknown URI: " + uri);
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs,  String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (URI_MATCHER.match(uri)) {
            case NOTES_ALL:
                builder.setTables(NotesMetaData.NotesTable.TABLE_NAME);
                builder.setProjectionMap(NOTES_PROJECTION_MAP);
                break;

            case NOTES_ONE:
                builder.setTables(NotesMetaData.NotesTable.TABLE_NAME);
                builder.setProjectionMap(NOTES_PROJECTION_MAP);
                builder.appendWhere(NotesMetaData.NotesTable.ID + " = " + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor queryCursor = builder.query(db, projection, selection, selectionArgs, null, null, null);
        queryCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return queryCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        switch (URI_MATCHER.match(uri)) {
            case NOTES_ALL:
                count = db.update(NotesMetaData.NotesTable.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case NOTES_ONE:
                String rowId = uri.getLastPathSegment();
                count = db.update(NotesMetaData.NotesTable.TABLE_NAME, values,
                        NotesMetaData.NotesTable.ID + " = " + rowId + (TextUtils.isEmpty(selection) ? "" : " AND (" + ")" ),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private static class NotesDBHelper extends SQLiteOpenHelper {

        private static final String SQL_CREATE = "CREATE TABLE "
                + NotesMetaData.NotesTable.TABLE_NAME + "("
                + NotesMetaData.NotesTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NotesMetaData.NotesTable.TITLE + " TEXT NOT NULL, "
                + NotesMetaData.NotesTable.CONTENT + " TEXT NOT NULL"
                + ");";

        public NotesDBHelper(Context context) {
            super(context, NotesMetaData.DATABASE_NAME, null, NotesMetaData.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
