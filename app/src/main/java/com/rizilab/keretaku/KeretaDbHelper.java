package com.rizilab.keretaku;
/**
 * Created by R on 4/8/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**public static final String TABLE_NAME = "station";
 * public static final String COLUMN_NAME_STATION_FROM = "stationFrom";
 * public static final String COLUMN_NAME_STATION_TO = "stationTo";
 * public static final String COLUMN_NAME_DISTANCE = "stationDistance";
 */

public class KeretaDbHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Keretaku.db";

    public KeretaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQLiteDatabase realDB = mDbHelper.getWritableDatabase();
        db.execSQL(KeretaDb.SQL_CREATE_ENTRIES);
        insertFirstDb(db);
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

    public void insertFirstDb (SQLiteDatabase db){

        ContentValues stDb = new ContentValues();
        // ZONA 1 (Bogor - Jakarta Kota)

        String[] Rute = {"Bogor","Cilebut", "Bojong Gede", "Citayam", "Depok", "Depok Baru", "Pondok Cina", "Universitas Indonesia",
                "Universitas Pancasila", "Lenteng Agung", "Tanjung Barat", "Pasar Minggu", "Pasar Minggu Baru", "Duren Kalibata",
                "Cawang", "Tebet", "Manggarai", "Cikini", "Gondangdia", "Gambir", "Juanda", "Sawah Besar", "Mangga Besar", "Jayakarta",
                "Jakarta Kota"};
        double[] Jarak = {7.518, 4.331, 5.197, 5.084, 1.741, 2.570, 1.109, 2.264, 1.029,
                2.460, 3.031, 1.695, 1.509, 1.475, 1.301, 2.610, 1.606, 1.699, 1.000, 1.198, 0.707, 1.171, 1.020, 1.487,0.000};

        for(int i = 0; i<Rute.length; i++) {
            int j = 1 + i;
            stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, (j));
            stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, Rute[i]);
            stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, Jarak[i]);
            db.insert(KeretaDb.StationEntry.TABLE_NAME, null, stDb);

        }

    }

}
