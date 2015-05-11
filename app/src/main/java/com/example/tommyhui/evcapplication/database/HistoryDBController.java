package com.example.tommyhui.evcapplication.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class HistoryDBController {

    public static final String DATABASE_NAME = "charging_station_list";
    public static final String TABLE_NAME = "HistoryOfChargingStation";

    public static final int VERSION = 1;

    private static final String KEY_ID = "id";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DISTRICT = "district";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TYPE = "type";
    private static final String KEY_SOCKET = "socket";
    private static final String KEY_QUANTITY = "quantity";

    private static final String[] COLUMNS = {KEY_ID,KEY_ADDRESS,KEY_DISTRICT, KEY_DESCRIPTION,KEY_TYPE,KEY_SOCKET,KEY_QUANTITY};

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_ADDRESS + " TEXT," +
            KEY_DISTRICT + " TEXT," +
            KEY_DESCRIPTION + " TEXT," +
            KEY_TYPE + " TEXT," +
            KEY_SOCKET + " TEXT," +
            KEY_QUANTITY + " INTEGER" +
            ");";

    private SQLiteDatabase db;

    public HistoryDBController(Context context) {
        db = DBHelper.getDatabase(context);
    }

    public void close() {
        db.close();
    }

    public HistoryItemCS addHistoryCS(HistoryItemCS cs){

        // 1. get reference to writable DB

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, cs.getAddress()); // get address
        values.put(KEY_DISTRICT, cs.getDistrict()); // get district
        values.put(KEY_DESCRIPTION, cs.getDescription()); // get charging station
        values.put(KEY_TYPE, cs.getType()); // get type
        values.put(KEY_SOCKET, cs.getSocket()); // get socket
        values.put(KEY_QUANTITY, cs.getQuantity()); // get quantity

        String[] input_column = new String[] {KEY_ADDRESS,KEY_DISTRICT, KEY_DESCRIPTION,KEY_TYPE,KEY_SOCKET,KEY_QUANTITY};
        String[] input_data = new String[]{cs.getAddress(),cs.getDistrict(), cs.getDescription(),cs.getType(),cs.getSocket(),Integer.toString(cs.getQuantity())};

        // 3. insert or update
        if(!checkRecordExist(TABLE_NAME, input_column, input_data))
        {
            //Perform the insert query
            db.insert(TABLE_NAME, null, values);
            //for logging
            Log.d("addHistoryCS", cs.toString());
        }

//        db.insert(TABLE_NAME, // table
//                null, //nullColumnHack
//                values); // key/value -> keys = column names/ values = column values

        // 4. close

        return cs;
    }
    private boolean checkRecordExist(String tableName, String[] keys, String [] values) {

        StringBuilder sb = new StringBuilder();
        boolean exists;

        for (int i = 0; i < keys.length; i++) {
            sb.append(keys[i])
                    .append("=\"")
                    .append(values[i])
                    .append("\" ");
            if (i<keys.length-1) sb.append("AND ");
        }

        String query = "SELECT * FROM " + tableName + " WHERE " + sb.toString();
        Cursor cursor = db.rawQuery(query, null);
        exists = (cursor.getCount() > 0);
//        Log.d("debug", "Exist? = " + exists);
        cursor.close();

        return exists;
    }

    public HistoryItemCS getHistoryCS(int id){

        // 2. build query
        Cursor cursor =
                db.query(TABLE_NAME, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        HistoryItemCS cs = new HistoryItemCS();
        cs.setAddress(cursor.getString(1));
        cs.setDistrict(cursor.getString(2));
        cs.setDescription(cursor.getString(3));
        cs.setType(cursor.getString(4));
        cs.setSocket(cursor.getString(5));
        cs.setQuantity(cursor.getInt(6));

        cs.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));


        //log
        Log.d("getCS("+id+")", cs.toString());

        if(cursor != null)
            cursor.close();
        // 5. return book
        return cs;
    }

    public ArrayList<HistoryItemCS> getAllHistoryCSes() {
        ArrayList<HistoryItemCS> cses = new ArrayList<>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_NAME;

        // 2. get reference to writable DB
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        HistoryItemCS cs;
        if (cursor.moveToFirst()) {
            do {
                cs = new HistoryItemCS();
                cs.setId(cursor.getInt(0));
                cs.setAddress(cursor.getString(1));
                cs.setDistrict(cursor.getString(2));
                cs.setDescription(cursor.getString(3));
                cs.setType(cursor.getString(4));
                cs.setSocket(cursor.getString(5));
                cs.setQuantity(Integer.parseInt(cursor.getString(6)));

                // Add book to books
                cses.add(cs);
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();

        Log.d("getAllHistoryCSes()", cses.toString());

        // return books
        return cses;
    }

    public int getHistoryItemCSCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        // Close the cursor
        cursor.close();
        // return count
        return count;
    }

//    public ArrayList<HistoryItemCS> searchListHistoryCSes(Activity activity, String query) {
//
//        ArrayList<HistoryItemCS> cses = new ArrayList<>();
//
//        String sql = "SELECT DISTINCT * FROM " + TABLE_NAME + " WHERE " + KEY_ADDRESS + " LIKE '%" + query + "%'"
//                + " OR " + KEY_DESCRIPTION + " LIKE '%" + query + "%'";
//
//        Cursor cursor = db.rawQuery(sql, null);
//
//        Log.d("Debug", "Count = " + cursor.getCount());
//
//        HistoryItemCS cs;
//
//        if (cursor.moveToFirst()) {
//            do {
//                cs = new HistoryItemCS();
//                cs.setId(Integer.parseInt(cursor.getString(0)));
//                cs.setAddress(cursor.getString(1));
//                cs.setDistrict(cursor.getString(2));
//                cs.setDescription(cursor.getString(3));
//                cs.setType(cursor.getString(4));
//                cs.setSocket(cursor.getString(5));
//                cs.setQuantity(Integer.parseInt(cursor.getString(6)));
//
//                // Add book to books
//                cses.add(cs);
//            } while (cursor.moveToNext());
//        }
//
//
//        cursor.close();
//        db.close();
//
//        // return books
//        return cses;
//    }
    public int updateHistoryCS(HistoryItemCS cs) {

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("address", cs.getAddress());
        values.put("district", cs.getDistrict());
        values.put("description", cs.getDescription());
        values.put("type", cs.getType());
        values.put("socket", cs.getSocket());
        values.put("quantity ", cs.getQuantity());

        // 3. updating row
        int i = db.update(TABLE_NAME, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(cs.getId()) }); //selection args

        // 4. close
        db.close();

        return i;
    }

    public void deleteHistoryCS(HistoryItemCS cs) {

        // 2. delete
        db.delete(TABLE_NAME, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(cs.getId()) }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteBook", cs.toString());

    }
    public void clear()
    {
        db.execSQL("DROP TABLE "+ TABLE_NAME);
        db.close();
    }
}