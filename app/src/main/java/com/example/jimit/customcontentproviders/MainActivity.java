package com.example.jimit.customcontentproviders;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText txtTitle, txtContent, txtId;
    private Button btnAdd, btnDelete, btnUpdate, btnShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTitle = (EditText) findViewById(R.id.txt_title);
        txtContent = (EditText) findViewById(R.id.txt_content);
        txtId = (EditText) findViewById(R.id.txt_id);

        btnAdd = (Button) findViewById(R.id.btn_add);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnShow = (Button) findViewById(R.id.btn_show);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(txtTitle.getText().toString())
                        && !TextUtils.isEmpty(txtContent.getText().toString())) {
                    ContentValues values = new ContentValues();
                    values.put(NotesMetaData.NotesTable.TITLE, txtTitle.getText().toString());
                    values.put(NotesMetaData.NotesTable.CONTENT, txtContent.getText().toString());

                    Uri uri = getContentResolver().insert(NotesMetaData.CONTENT_URI, values);
                    if (null != uri)
                        makeToast(uri.toString());
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(txtId.getText().toString())) {
                    try {
                        int id = Integer.parseInt(txtId.getText().toString());
                        int count = getContentResolver().delete(NotesMetaData.CONTENT_URI,
                                NotesMetaData.NotesTable.ID + " = " + id, null);
                        Log.d(TAG, "onClick Delete: id=" + id + ", deleted=" + (count > 0));
                        makeToast("id=" + id + ", deleted=" + (count > 0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(txtTitle.getText().toString())
                        && !TextUtils.isEmpty(txtContent.getText().toString())
                        && !TextUtils.isEmpty(txtId.getText().toString())) {
                    try {
                        ContentValues values = new ContentValues();
                        values.put(NotesMetaData.NotesTable.TITLE, txtTitle.getText().toString());
                        values.put(NotesMetaData.NotesTable.CONTENT, txtContent.getText().toString());
                        int count = getContentResolver().update(NotesMetaData.CONTENT_URI, values,
                                NotesMetaData.NotesTable.ID + " = " + txtId.getText().toString(), null);
                        makeToast("Notes Updated = " + count);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = getContentResolver().query(NotesMetaData.CONTENT_URI,
                        null, null, null, null);

                if (cursor.getCount() > 0) {
                    Log.i(TAG, "Showing values.....");
                    while (cursor.moveToNext()) {
                        String id = cursor.getString(cursor.getColumnIndex(NotesMetaData.NotesTable.ID));
                        String title = cursor.getString(cursor
                                .getColumnIndex(NotesMetaData.NotesTable.TITLE));
                        Log.d(TAG, "onClick Show: id=" + id + ", title=" + title);
                    }
                    makeToast("Check the LogCat for Notes");
                } else {
                    Log.i(TAG, "No Notes added");
                    makeToast("No Notes added");
                }

                cursor.close();
            }
        });
    }

    private void makeToast(String display) {
        Toast.makeText(getApplicationContext(), display, Toast.LENGTH_LONG).show();
    }
}
