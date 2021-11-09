package com.enx.pipedrop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class dbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "pipeDrop.dp";
    public static final int DB_VERSION = 1;
    public static final String PIPEDROP_TABLE_NAME = "pipedrop";

    // Column names
    private static final String ID_COL = "id";
    private static final String PLANT_COL = "plant";
    private static final String VALVE_COL = "valve";
    private static final String TEMP_COL = "temperature";
    private static final String PRESSURE_COL = "pressure";
    private static final String AMMONIA_CONC_COL = "ammoniaConc";
    private static final String PIPE_ID_COL = "pipeID";
    private static final String ORIFICE_DIAMETER_COL = "orificeDiameter";

    // Table creation
    private static final String CREATE_PIPEDROP_TABLE = "CREATE TABLE IF NOT EXISTS " + PIPEDROP_TABLE_NAME +
            " (" + ID_COL + "INTEGER PRIMARY KEY AUTOINCREMENT, " + PLANT_COL + " TEXT, " + VALVE_COL +
            " TEXT, " + TEMP_COL + " REAL, " + PRESSURE_COL + " REAL, " + AMMONIA_CONC_COL + " REAL, "
            + PIPE_ID_COL + " REAL, " + ORIFICE_DIAMETER_COL + " REAL);";

    public dbHelper(Context context) {
        super(context, DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PIPEDROP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PIPEDROP_TABLE_NAME);
        onCreate(db);
    }

    public void addPipe(Pipe pipe){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PLANT_COL, pipe.getPlant());
        values.put(VALVE_COL, pipe.getValve());
        values.put(TEMP_COL, pipe.getTemperature());
        values.put(PRESSURE_COL, pipe.getPressure());
        values.put(AMMONIA_CONC_COL, pipe.getAmmoniaConc());
        values.put(PIPE_ID_COL, pipe.getPipeID());
        values.put(ORIFICE_DIAMETER_COL, pipe.getOrificeID());

        db.insert(PIPEDROP_TABLE_NAME, null, values);
        db.close();
    }

    public void updatePipe(Pipe pipe){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PLANT_COL, pipe.getPlant());
        values.put(VALVE_COL, pipe.getValve());
        values.put(TEMP_COL, pipe.getTemperature());
        values.put(PRESSURE_COL, pipe.getPressure());
        values.put(AMMONIA_CONC_COL, pipe.getAmmoniaConc());
        values.put(PIPE_ID_COL, pipe.getPipeID());
        values.put(ORIFICE_DIAMETER_COL, pipe.getOrificeID());

        db.update(PIPEDROP_TABLE_NAME, values,"plant = ? AND valve = ?",
                new String[]{pipe.getPlant(), pipe.getValve()});
        db.close();
    }

    public ArrayList<Pipe> getAllPipes() {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        String sql = "SELECT * FROM " + PIPEDROP_TABLE_NAME;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()){
            do {
                String plant = cursor.getString(cursor.getColumnIndex(PLANT_COL));
                String valve = cursor.getString(cursor.getColumnIndex(VALVE_COL));
                double temperature = cursor.getDouble(cursor.getColumnIndex(TEMP_COL));
                double pressure = cursor.getDouble(cursor.getColumnIndex(PRESSURE_COL));
                double ammoniaConc = cursor.getDouble(cursor.getColumnIndex(AMMONIA_CONC_COL));
                double pipeID = cursor.getDouble(cursor.getColumnIndex(PIPE_ID_COL));
                double orificeID = cursor.getDouble(cursor.getColumnIndex(ORIFICE_DIAMETER_COL));

                pipeList.add(new Pipe(plant, valve, temperature, pressure, ammoniaConc, pipeID, orificeID));
            } while(cursor.moveToNext());

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        db.close();
        return pipeList;
    }

}
