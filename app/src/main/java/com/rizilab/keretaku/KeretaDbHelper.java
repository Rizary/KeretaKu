package com.rizilab.keretaku;
/**
 * Created by R on 4/8/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**public static final String TABLE_NAME = "station";
 * public static final String COLUMN_NAME_STATION_FROM = "stationFrom";
 * public static final String COLUMN_NAME_STATION_TO = "stationTo";
 * public static final String COLUMN_NAME_DISTANCE = "stationDistance";
 */

public class KeretaDbHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Keretaku.db";

    public KeretaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(KeretaDb.SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(KeretaDb.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }



}
