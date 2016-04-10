package com.rizilab.keretaku;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.database.Cursor;
import android.widget.Button;
import android.widget.TextView;
import com.rizilab.keretaku.KeretaDb.*;
import android.widget.Toast;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by R on 4/8/16.
 */
public class AppActivity extends AppCompatActivity{

    Button button;
    private int ticketPrice = 0;
    private int indexFrom = 0;
    private int indexTo = 0;

    protected void onCreate(Bundle savedInstanceState) {
        KeretaDbHelper mDbHelper = new KeretaDbHelper(getBaseContext());
        super.onCreate(savedInstanceState);
        SQLiteDatabase realDb = mDbHelper.getWritableDatabase();
        realDb.beginTransaction();

        //Spinner stasiun Asal
        setContentView(R.layout.opening_app);
        final Spinner spFrom = (Spinner)findViewById(R.id.spinner_from);

        ArrayAdapter<CharSequence> iFrom = ArrayAdapter.createFromResource(this, R.array.stasFrom, android.R.layout.simple_spinner_item);
        iFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrom.setAdapter(iFrom);
        spFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                String stationNameFrom = spFrom.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        //Spinner stasiun Tujuan
        final Spinner spTo = (Spinner)findViewById(R.id.spinner_to);
        ArrayAdapter<CharSequence> iTo = ArrayAdapter.createFromResource(this, R.array.stasTo, android.R.layout.simple_spinner_item);
        iTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTo.setAdapter(iTo);
        spTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                String stationNameTo = spTo.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        addListenerOnButton(mDbHelper, spFrom, spTo);
        realDb.endTransaction();

    }

    //fungsi listener apabila tomboltarif ditekan

    public void addListenerOnButton(final KeretaDbHelper db, final Spinner from, final Spinner to){
        final Context context = this;

        button = (Button) findViewById(R.id.buttonTarif);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ticketPrice = 2000;
                Intent myIntent = new Intent(view.getContext(), AppActivity.class);
                TextView finalPrice = (TextView) findViewById(R.id.hasil_tarif);
                String stationNameFrom = from.getSelectedItem().toString();
                String stationNameTo = to.getSelectedItem().toString();
                indexFrom = fromStationToIndex(db, stationNameFrom, StationEntry.TABLE_NAME, StationEntry.COLUMN_NAME_STATION_ID, StationEntry.COLUMN_NAME_STATION_NAME);
                indexTo = fromStationToIndex(db, stationNameTo, StationEntry.TABLE_NAME, StationEntry.COLUMN_NAME_STATION_ID, StationEntry.COLUMN_NAME_STATION_NAME);
                double finalDistance = countingDistance(db, indexFrom, indexTo, StationEntry.COLUMN_NAME_STATION_ID, StationEntry.COLUMN_NAME_DISTANCE, StationEntry.TABLE_NAME);
                double i  = 25.0;
                while ( i < finalDistance){

                    i += 10.0;
                    ticketPrice += 1000;
                }

                Log.d("indexFrom :", Integer.toString(indexFrom));
                Log.d("indexTo :", Integer.toString(indexTo));
                Log.d("finalDistance :", Double.toString(finalDistance));

                finalPrice.setText(String.valueOf(ticketPrice));
            }
        });


    }

    //fungsi untuk mengecek apakah stasiun ada di database
    public boolean checkStationExists (KeretaDbHelper cekStatusData, String tableName, String dbField, String stationFrom){
        int count = -1;
        SQLiteDatabase stationDatabase = cekStatusData.getReadableDatabase();
        String checkStation = "SELECT * FROM " + tableName + " where " + dbField + " = ?";
        Cursor cursor = stationDatabase.rawQuery(checkStation, new String[]{stationFrom});

        if(cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }

        cursor.close();
        stationDatabase.close();
        return true;
    }

    //Fungsi untuk menghitung jarak antar stasiun

    public int fromStationToIndex (KeretaDbHelper stationData, String stationString, String tableName, String dbField, String stationColumn){
        int theIndex = 0;
        SQLiteDatabase stationDatabase = stationData.getReadableDatabase();
        Cursor getStationIndex = stationDatabase.query(tableName, new String[]{dbField}, stationColumn + " = '" + stationString + "'", null, null, null, null);

        if( getStationIndex != null && getStationIndex.moveToFirst()) {
            theIndex = getStationIndex.getInt(getStationIndex.getColumnIndexOrThrow(dbField));
            getStationIndex.close();
        }

        return theIndex;
    }

    public double countingDistance (KeretaDbHelper stationData, int indexStationFrom, int indexStationTo, String dbField, String stationDistance, String tableName){//KeretaDbHelper dataStasiun, String stasiunAwal, String stasiunAkhir, String tableName, String dbField, String cols1, String cols2)
        double totalDistance = 0.000;
        if(indexStationFrom > indexStationTo) {
            int temp = indexStationFrom;
            indexStationFrom = indexStationTo;
            indexStationTo = temp;
        }

        SQLiteDatabase stationDatabase = stationData.getReadableDatabase();

        Cursor stationCur = stationDatabase.rawQuery("SELECT SUM(" + stationDistance + ") AS myTotal FROM " + tableName + " WHERE " + dbField + " BETWEEN " + indexStationFrom + " AND " + indexStationTo, null);
        //SQLiteStatement stationCur = stationDatabase.compileStatement("SELECT SUM(" + stationDistance + ") AS myTotal FROM " + tableName + " WHERE " + dbField + " BETWEEN " + indexStationFrom + " AND " + indexStationTo + "");


        if(stationCur != null && stationCur.moveToFirst()) {
            totalDistance = stationCur.getDouble(stationCur.getColumnIndex("myTotal"));


            stationCur.close();

        }
        stationDatabase.close();

        return totalDistance;

        /*SQLiteDatabase stationDatabase = dataStasiun.getReadableDatabase();

        if(checkStationExists(dataStasiun, tableName, cols1, stasiunAwal) == false) {
            Toast.makeText(getApplicationContext(), "Stasiun tidak ada", Toast.LENGTH_SHORT).show();
        }

        //String getDistance = "SELECT " + dbField + " FROM " + tableName + " WHERE " + cols1 + " = " + stasiunAwal + " AND " + cols2 + " = " + stasiunAkhir;
        Cursor getDistance = stationDatabase.query(tableName, new String[] {dbField}, cols1 + " = '" + stasiunAwal + "'" + " AND " + cols2 + " = '" + stasiunAkhir + "'", null, null, null, null, null);

        int totalJarak = getDistance.getColumnIndex(dbField);

        //hasilJarakStasiun.close();
        stationDatabase.close();
        */


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



}
