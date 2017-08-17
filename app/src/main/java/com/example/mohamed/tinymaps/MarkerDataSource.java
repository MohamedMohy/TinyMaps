package com.example.mohamed.tinymaps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohamed on 15/08/17.
 */

public class MarkerDataSource {
    MySQLHelper mySQLHelper;
    SQLiteDatabase sqLiteDatabase;

    public MarkerDataSource(Context context) {
        mySQLHelper = new MySQLHelper(context);

    }

    public void open() throws SQLException {
        sqLiteDatabase = mySQLHelper.getWritableDatabase();
    }

    public void close() {
        sqLiteDatabase.close();
    }

    public void addMarkers(ArrayList<String> markers) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MySQLHelper.TITLE, markers.get(0));
        contentValues.put(MySQLHelper.POSITION, markers.get(1));
        sqLiteDatabase.insert(mySQLHelper.TABLE_NAME, null, contentValues);

    }
    public void clearDatabase(){
        sqLiteDatabase = mySQLHelper.getWritableDatabase();
        sqLiteDatabase.delete(mySQLHelper.TABLE_NAME,null,null);
    }



    public List<List<String>> getAllMarkers() {
        List<List<String>> reesult = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + mySQLHelper.TABLE_NAME;
        SQLiteDatabase db = this.mySQLHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(cursor.getString(1));
                temp.add(cursor.getString(2));
                reesult.add(temp);

            } while ((cursor.moveToNext()));

        }
        return reesult;
    }
    public void deleteEntry(long row) {

        sqLiteDatabase.delete(mySQLHelper.TABLE_NAME, mySQLHelper.ID_COL + "=" + row, null);

      /*if you just have key_name to select a row,you can ignore passing rowid(here-row) and use:

      db.delete(DATABASE_TABLE, KEY_NAME + "=" + key_name, null);
      */

    }
}
