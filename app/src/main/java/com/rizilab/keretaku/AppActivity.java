package com.rizilab.keretaku;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.database.Cursor;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by R on 4/8/16.
 */
public class AppActivity extends AppCompatActivity{

    Button button;
    private int ticketPrice = 2000;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        KeretaDbHelper mDbHelper = new KeretaDbHelper(this);
        SQLiteDatabase realDB = mDbHelper.getWritableDatabase();
        ContentValues listStation = insertFirstDb();
        realDB.insert(KeretaDb.StationEntry.TABLE_NAME, null, listStation);


        //Spinner stasiun Asal
        setContentView(R.layout.opening_app);
        final Spinner spFrom = (Spinner)findViewById(R.id.spinner_from);



        ArrayAdapter<CharSequence> iFrom = ArrayAdapter.createFromResource(this, R.array.stasFrom, android.R.layout.simple_spinner_item);
        iFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrom.setAdapter(iFrom);
        spFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Spinner stasiun To
        String stationNameTo = spTo.getSelectedItem().toString();

        //Spinner stasiun Awal
        String stationNameFrom = spFrom.getSelectedItem().toString();

        //Index spinner
        int indexFrom = fromStationToIndex(mDbHelper, stationNameFrom, KeretaDb.StationEntry.TABLE_NAME, KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME);
        int indexTo = fromStationToIndex(mDbHelper, stationNameTo, KeretaDb.StationEntry.TABLE_NAME, KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME);
        int finalDistance = countingDistance(mDbHelper, indexFrom, indexTo, KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, KeretaDb.StationEntry.TABLE_NAME);
        double i  = 25.0;
        while ( i < finalDistance){
            i += 10.0;
            ticketPrice += 1000;
        }

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
    public boolean checkStationExists (KeretaDbHelper cekStatusData, String tableName, String dbField, String stationNameFrom){
        int count = -1;
        SQLiteDatabase stationDatabase = cekStatusData.getReadableDatabase();
        String checkStation = "SELECT * FROM " + tableName + " where " + dbField + " = ?";
        Cursor cursor = stationDatabase.rawQuery(checkStation, new String [] {stationNameFrom});

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

    public int fromStationToIndex (KeretaDbHelper stationData, String stationName, String tableName, String dbField, String stationColumn){
        SQLiteDatabase stationDatabase = stationData.getReadableDatabase();
        Cursor getStationIndex = stationDatabase.query(tableName, new String[]{dbField}, stationColumn + " = '" + stationName + "'", null, null, null, null, null);
        int theIndex = getStationIndex.getColumnIndex(dbField);
        stationDatabase.close();

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

    public ContentValues insertFirstDb (){

        ContentValues stDb = new ContentValues();
        // ZONA 1 (Bogor - Jakarta Kota)

        String[] Rute = {"Bogor","Cilebut", "Bojong Gede", "Citayam", "Depok", "Depok Baru", "Pondok Cina", "Universitas Indonesia",
                         "Universitas Pancasila", "Lenteng Agung", "Tanjung Barat", "Pasar Minggu", "Pasar Minggu Baru", "Duren Kalibata",
                         "Cawang", "Tebet", "Manggarai", "Cikini", "Gondangdia", "Gambir", "Juanda", "Sawah Besar", "Mangga Besar", "Jayakarta",
                         "Jakarta Kota"};
        double[] Jarak = {7.518, 4.331, 5.197, 5.084, 1.741, 2.570, 1.109, 2.264, 1.029,
                2.460, 3.301, 1.695, 1.509, 1.475, 1.301, 2.610, 1.606, 1.699, 1.000, 1.198, 0.707, 1.171, 1.020, 1.487,0.000};

        /*for(int i = 0; i<Rute.length; i++) {
            int j = 1 + i;
            stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, (j));
            stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, Rute[i]);
            stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, Jarak[i]);
        }*/

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, 1);
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, "Bogor");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 7.518);

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, 2);
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, "Cilebut");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 4.331);

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, 3);
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, "Bojong Gede");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 5.197);

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, 4);
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, "Citayam");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 5.084);

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, 5);
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, "Depok");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 1.741);

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, 6);
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, "Depok Baru");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 2.570);

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, 7);
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, "Pondok Cina");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 1.109);

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, 8);
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, "Universitas Indonesia");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 2.264);

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, 9);
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, "Universitas Pancasila");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 1.029);

        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_ID, 10);
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_STATION_NAME, "Lenteng Agung");
        stDb.put(KeretaDb.StationEntry.COLUMN_NAME_DISTANCE, 2.460);


        return stDb;





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
