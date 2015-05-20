package com.example.tommyhui.evcapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

        db.execSQL(ItemCS_DBController.CREATE_TABLE);
        db.execSQL(HistoryItemCS_DBController.CREATE_TABLE);
        db.execSQL(FavoriteItemCS_DBController.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + ItemCS_DBController.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HistoryItemCS_DBController.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteItemCS_DBController.TABLE_NAME);

        onCreate(db);
    }
}
