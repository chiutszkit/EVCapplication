package com.example.tommyhui.evcapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "chargingStationList.db";
    public static final int VERSION = 1;
    private static SQLiteDatabase database;

    public DBHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new DBHelper(context, DATABASE_NAME,
                    null, VERSION).getWritableDatabase();
        }

        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DBController.CREATE_TABLE);
        db.execSQL(HistoryDBController.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + DBController.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HistoryDBController.TABLE_NAME);

        onCreate(db);
    }
}
