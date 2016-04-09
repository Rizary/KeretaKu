package com.rizilab.keretaku;


import android.provider.BaseColumns;

/**
 * Created by R on 4/8/16.
 */
public final class KeretaDb{
    public KeretaDb() {};

    public static abstract class StationEntry implements BaseColumns {
        public static final String TABLE_NAME = "station";
        public static final String COLUMN_NAME_STATION_ID = "stationId";
        public static final String COLUMN_NAME_STATION_NAME = "stationName";
        public static final String COLUMN_NAME_DISTANCE = "stationDistance";

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    protected static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + StationEntry.TABLE_NAME + " (" +
                    StationEntry.COLUMN_NAME_STATION_ID + INTEGER_TYPE + COMMA_SEP +
                    StationEntry.COLUMN_NAME_STATION_NAME + TEXT_TYPE + COMMA_SEP +
                    StationEntry.COLUMN_NAME_DISTANCE + REAL_TYPE + " )";

    protected static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StationEntry.TABLE_NAME;

}
