package com.rizilab.keretaku;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.ArrayList;

/**
 * Created by R on 4/8/16.
 */
public class AppActivity extends AppCompatActivity{

    Button button;
    private int ticketPrice = 2000;
    String iseng;
    String stationNameFrom;
    String stationNameTo;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        KeretaDbHelper mDbHelper = new KeretaDbHelper(this);
        SQLiteDatabase realDB = mDbHelper.getWritableDatabase();
        insertFirstDb(realDB);

        //Spinner stasiun Asal
        setContentView(R.layout.opening_app);
        final Spinner spFrom = (Spinner)findViewById(R.id.spinner_from);

        ArrayAdapter<CharSequence> iFrom = ArrayAdapter.createFromResource(this, R.array.stasFrom, android.R.layout.simple_spinner_item);
        iFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrom.setAdapter(iFrom);
        spFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                stationNameFrom = spFrom.getSelectedItem().toString();
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

                stationNameTo = arg0.getItemAtPosition(arg2).toString();;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        //Spinner stasiun To


        //Spinner stasiun Awal

        //Index spinner
        int indexFrom = fromStationToIndex(mDbHelper, "Bogor", StationEntry.TABLE_NAME, StationEntry.COLUMN_NAME_STATION_ID, StationEntry.COLUMN_NAME_STATION_NAME);
        int indexTo = fromStationToIndex(mDbHelper, "Tebet", StationEntry.TABLE_NAME, StationEntry.COLUMN_NAME_STATION_ID, StationEntry.COLUMN_NAME_STATION_NAME);
        int finalDistance = countingDistance(mDbHelper, indexFrom, indexTo, StationEntry.COLUMN_NAME_STATION_ID, StationEntry.COLUMN_NAME_DISTANCE, StationEntry.TABLE_NAME);
        double i  = 25.0;
        while ( i < finalDistance){
            i += 10.0;
            ticketPrice += 1000;
        }
        iseng = stationNameFrom;
        //ticketPrice = indexFrom;
        addListenerOnButton();
    }

    //fungsi listener apabila tomboltarif ditekan

    public void addListenerOnButton(){
        final Context context = this;

        button = (Button) findViewById(R.id.buttonTarif);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                TextView finalPrice = (TextView) findViewById(R.id.hasil_tarif);
                finalPrice.setText(String.valueOf(ticketPrice));

                /*Intent intent = new Intent(context, StackActivity.class);
                startActivity(intent);*/
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

        Log.d("Andika", Integer.toString(getStationIndex.getCount()));

        if( getStationIndex != null && getStationIndex.moveToFirst()) {
            theIndex = getStationIndex.getInt(getStationIndex.getColumnIndexOrThrow(dbField));
            getStationIndex.close();
        }



        return theIndex;
    }

    public int countingDistance (KeretaDbHelper stationData, int indexStationFrom, int indexStationTo, String dbField, String stationDistance, String tableName){//KeretaDbHelper dataStasiun, String stasiunAwal, String stasiunAkhir, String tableName, String dbField, String cols1, String cols2)
        int totalDistance = 0;
        if(indexStationFrom > indexStationTo) swapRoute(indexStationFrom, indexStationTo);

        SQLiteDatabase stationDatabase = stationData.getReadableDatabase();

        Cursor stationCur = stationDatabase.rawQuery("SELECT SUM(" + stationDistance + ") FROM " + tableName + " WHERE " + dbField + " BETWEEN " + indexStationFrom + " AND " + indexStationTo, null);
        if(stationCur.moveToFirst())
        {
            totalDistance = stationCur.getInt(0);
        }

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
        stationCur.close();
        return totalDistance;

    }

    public void swapRoute(int indexStationFrom, int indexStationTo) {
        int temp = indexStationFrom;
        indexStationFrom = indexStationTo;
        indexStationTo = temp;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void insertFirstDb (SQLiteDatabase db){

        ContentValues stDb = new ContentValues();
        // ZONA 1 (Bogor - Jakarta Kota)

        String[] Rute = {"Bogor","Cilebut", "Bojong Gede", "Citayam", "Depok", "Depok Baru", "Pondok Cina", "Universitas Indonesia",
                         "Universitas Pancasila", "Lenteng Agung", "Tanjung Barat", "Pasar Minggu", "Pasar Minggu Baru", "Duren Kalibata",
                         "Cawang", "Tebet", "Manggarai", "Cikini", "Gondangdia", "Gambir", "Juanda", "Sawah Besar", "Mangga Besar", "Jayakarta",
                         "Jakarta Kota"};
        double[] Jarak = {7.518, 4.331, 5.197, 5.084, 1.741, 2.570, 1.109, 2.264, 1.029,
                2.460, 3.301, 1.695, 1.509, 1.475, 1.301, 2.610, 1.606, 1.699, 1.000, 1.198, 0.707, 1.171, 1.020, 1.487,0.000};

        for(int i = 0; i<Rute.length; i++) {
            int j = 1 + i;
            stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, (j));
            stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, Rute[i]);
            stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, Jarak[i]);
            db.insert(StationEntry.TABLE_NAME, null, stDb);

        }




        // ZONA 2 (Manggarai Jakarta Kota)

        // ZONA 3 (Manggarai Tanah Abang)

        /*stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_FROM, "Manggarai");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_TO, "Sudirman");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 3186);

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_FROM, "Manggarai");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_TO, "Sudirman");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 3186);

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_FROM, "Manggarai");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_TO, "Sudirman");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 3186);

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_FROM, "Manggarai");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_TO, "Sudirman");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 3186);*/

    }

}
