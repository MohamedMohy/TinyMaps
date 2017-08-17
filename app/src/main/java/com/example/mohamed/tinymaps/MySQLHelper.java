package com.example.mohamed.tinymaps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mohamed on 15/08/17.
 */

public class MySQLHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME= "locations";
    public static final String ID_COL ="loc_id";
    public static final String TITLE ="loc_title";
    public static final String POSITION="loc_position";
    public static final int D_VERSION =1;
    public static final String DB_NAME ="markerlocations.db";
    private static final String DB_CREATE ="create table "+TABLE_NAME+"("+ID_COL+" integer primary key autoincrement, "+TITLE+" text, "+POSITION+" text);";

    public MySQLHelper(Context context) {
        super(context,DB_NAME,null,D_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+ TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
