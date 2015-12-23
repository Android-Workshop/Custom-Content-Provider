package com.example.jimit.customcontentproviders;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jimit on 23-12-2015.
 */
public final class NotesMetaData {
    public static final String AUTHORITY = "com.example.jimit.customcontentproviders.NOTES";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notes");

    public static final String DATABASE_NAME = "notes.db";
    public static final int DATABASE_VERSION = 1;

    public static final String CONTENT_TYPE_NOTES_ALL = "vnd.android.cursor.dir/vnd.example.notes";
    public static final String CONTENT_TYPE_NOTES_ONE = "vnd.android.cursor.item/vnd.example.notes";

    public final class NotesTable implements BaseColumns {
        public static final String TABLE_NAME = "tbl_notes";

        public static final String ID = "_id";
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
    }
}
