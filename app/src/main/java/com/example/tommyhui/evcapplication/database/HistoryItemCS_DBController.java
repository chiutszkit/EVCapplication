package com.example.tommyhui.evcapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class HistoryItemCS_DBController {

    public static final String TABLE_NAME = "HistoryOfChargingStation";

    private static final String KEY_ID = "id";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DISTRICT = "district";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TYPE = "type";
    private static final String KEY_SOCKET = "socket";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private static final String[] COLUMNS = {KEY_ID, KEY_ADDRESS, KEY_DISTRICT, KEY_DESCRIPTION, KEY_TYPE, KEY_SOCKET, KEY_QUANTITY, KEY_LATITUDE, KEY_LONGITUDE};

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_ADDRESS + " TEXT," +
            KEY_DISTRICT + " TEXT," +
            KEY_DESCRIPTION + " TEXT," +
            KEY_TYPE + " TEXT," +
            KEY_SOCKET + " TEXT," +
            KEY_QUANTITY + " INTEGER," +
            KEY_LATITUDE + " TEXT," +
            KEY_LONGITUDE + " TEXT);";

    private SQLiteDatabase db;

    public HistoryItemCS_DBController(Context context) {
        db = DBHelper.getDatabase(context);
    }

    public void close() {
        db.close();
    }

    public HistoryItemCS addHistoryCS(HistoryItemCS historyCS) {

        // 1. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, historyCS.getAddress()); // get address
        values.put(KEY_DISTRICT, historyCS.getDistrict()); // get district
        values.put(KEY_DESCRIPTION, historyCS.getDescription()); // get charging station
        values.put(KEY_TYPE, historyCS.getType()); // get type
        values.put(KEY_SOCKET, historyCS.getSocket()); // get socket
        values.put(KEY_QUANTITY, historyCS.getQuantity()); // get quantity
        values.put(KEY_LATITUDE, historyCS.getLatitude()); // get latitude
        values.put(KEY_LONGITUDE, historyCS.getLongitude()); // get longitude

        // 2. insert or update

        String[] input_column = new String[]{KEY_ADDRESS, KEY_DISTRICT, KEY_DESCRIPTION, KEY_TYPE, KEY_SOCKET, KEY_QUANTITY};
        String[] input_data = new String[]{historyCS.getAddress(), historyCS.getDistrict(), historyCS.getDescription(), historyCS.getType(), historyCS.getSocket(), Integer.toString(historyCS.getQuantity())};

        if (!checkRecordExist(TABLE_NAME, input_column, input_data)) {
            // Perform the insert query
            db.insert(TABLE_NAME, null, values);
            Log.d("addHistoryCS", historyCS.toString());
        }

        // 3. return the item historyCS
        return historyCS;
    }

    private boolean checkRecordExist(String tableName, String[] keys, String[] values) {

        StringBuilder sb = new StringBuilder();
        boolean exists;

        // 1. set up a string builder of comparing columns
        for (int i = 0; i < keys.length; i++) {
            sb.append(keys[i])
                    .append("=\"")
                    .append(values[i])
                    .append("\" ");
            if (i < keys.length - 1) sb.append("AND ");
        }

        // 2. execute the query to search whether the record exists
        String query = "SELECT * FROM " + tableName + " WHERE " + sb.toString();
        Cursor cursor = db.rawQuery(query, null);

        exists = (cursor.getCount() > 0);
        Log.d("addHistoryCS", "Exist? = " + exists);

        cursor.close();

        return exists;
    }

    public HistoryItemCS getHistoryCS(int id) {

        // 1. build query
        Cursor cursor =
                db.query(TABLE_NAME, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 2. if we got results, get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 3. build cs object
        HistoryItemCS historyCS = new HistoryItemCS();
        historyCS.setAddress(cursor.getString(1));
        historyCS.setDistrict(cursor.getString(2));
        historyCS.setDescription(cursor.getString(3));
        historyCS.setType(cursor.getString(4));
        historyCS.setSocket(cursor.getString(5));
        historyCS.setQuantity(cursor.getInt(6));
        historyCS.setLatitude(cursor.getString(7));
        historyCS.setLongitude(cursor.getString(8));

        historyCS.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));

        Log.d("getHistoryCS(" + id + ")", historyCS.toString());

        if (cursor != null)
            cursor.close();
        // 5. return book
        return historyCS;
    }

    public ArrayList<HistoryItemCS> getAllHistoryCSes() {

        ArrayList<HistoryItemCS> historyCSes = new ArrayList<>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_NAME;

        // 2. get reference to writable DB
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build cs and add it to list
        HistoryItemCS historyCS;
        if (cursor.moveToFirst()) {
            do {
                historyCS = new HistoryItemCS();
                historyCS.setId(cursor.getInt(0));
                historyCS.setAddress(cursor.getString(1));
                historyCS.setDistrict(cursor.getString(2));
                historyCS.setDescription(cursor.getString(3));
                historyCS.setType(cursor.getString(4));
                historyCS.setSocket(cursor.getString(5));
                historyCS.setQuantity(Integer.parseInt(cursor.getString(6)));
                historyCS.setLatitude(cursor.getString(7));
                historyCS.setLongitude(cursor.getString(8));

                // Add cs to list of cs
                historyCSes.add(historyCS);
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();

        Log.d("getAllHistoryCSes", historyCSes.toString());

        // 4. return list of cs
        return historyCSes;
    }

    public int getHistoryCSCount() {

        // 1. build the query
        String countQuery = "SELECT * FROM " + TABLE_NAME;

        // 2. execute the query to search whether the record exists
        Cursor cursor = db.rawQuery(countQuery, null);

        // 2. get the count
        int count = cursor.getCount();

        cursor.close();
        return count;
    }

    public int updateHistoryCS(HistoryItemCS historyCS) {

        // 1. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("address", historyCS.getAddress());
        values.put("district", historyCS.getDistrict());
        values.put("description", historyCS.getDescription());
        values.put("type", historyCS.getType());
        values.put("socket", historyCS.getSocket());
        values.put("quantity ", historyCS.getQuantity());
        values.put("latitude", historyCS.getLatitude());
        values.put("longitude", historyCS.getLongitude());


        // 2. updating row
        int i = db.update(TABLE_NAME, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(historyCS.getId())}); //selection args

        // 3. return the update 
        return i;
    }

    public void deleteHistoryCS(HistoryItemCS historyCS) {

        // 1. delete
        db.delete(TABLE_NAME, //table name
                KEY_ID + " = ?",  // selections
                new String[]{String.valueOf(historyCS.getId())}); //selections args

        Log.d("deleteHistoryCS", historyCS.toString());
    }

    public void clear() {
        db.execSQL("DROP TABLE " + TABLE_NAME);
    }
}