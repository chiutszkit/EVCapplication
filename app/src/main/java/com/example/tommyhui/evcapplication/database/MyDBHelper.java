package com.example.tommyhui.evcapplication.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MyDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "charging_station_list";
    public static final String TABLE_NAME = "ChargingStation";

    public static final int VERSION = 1;

    private static final String KEY_ID = "id";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DISTRICT = "district";
    private static final String KEY_DESCRIPTION = "chargingStation";
    private static final String KEY_TYPE = "type";
    private static final String KEY_SOCKET = "socket";
    private static final String KEY_QUANTITY = "quantity";

    private static final String[] COLUMNS = {KEY_ID,KEY_ADDRESS,KEY_DISTRICT, KEY_DESCRIPTION,KEY_TYPE,KEY_SOCKET,KEY_QUANTITY};

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String DATABASE_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_ADDRESS + " TEXT," +
                KEY_DISTRICT + " TEXT," +
                KEY_DESCRIPTION + " TEXT," +
                KEY_TYPE + " TEXT," +
                KEY_SOCKET + " TEXT," +
                KEY_QUANTITY + " INTEGER" +
                ");";

        db.execSQL(DATABASE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_NAME);
        onCreate(db);
    }

    @Override
       public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    public void addCS(ItemCS cs){
        //for logging
        Log.d("addCS", cs.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, cs.getAddress()); // get address
        values.put(KEY_DISTRICT, cs.getDistrict()); // get district
        values.put(KEY_DESCRIPTION, cs.getDescription()); // get charging station
        values.put(KEY_TYPE, cs.getType()); // get type
        values.put(KEY_SOCKET, cs.getSocket()); // get socket
        values.put(KEY_QUANTITY, cs.getQuantity()); // get quantity

        // 3. insert
        db.insert(TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }
    public ItemCS getCS(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

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
        ItemCS cs = new ItemCS();
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

    public ArrayList<ItemCS> getAllCSes() {
        ArrayList<ItemCS> cses = new ArrayList<>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_NAME;
//        String query = "SELECT *  FROM " + TABLE_NAME + " WHERE " + "chargingStation ='Star Ferry Car Park'";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        ItemCS cs;
        if (cursor.moveToFirst()) {
            do {
                cs = new ItemCS();
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

        Log.d("getAllCSes()", cses.toString());

        // return books
        return cses;
    }

    public int getItemCSCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        // Close the cursor
        cursor.close();
        // return count
        return count;
    }

    public ArrayList<ItemCS> searchListCSes(Activity activity, String query) {

        ArrayList<ItemCS> cses = new ArrayList<>();

        String sql = "SELECT DISTINCT * FROM " + TABLE_NAME + " WHERE " + "address" + " LIKE '%" + query + "%'"
                + " OR " + "chargingStation" + " LIKE '%" + query + "%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        Log.d("Debug", "Count = " + cursor.getCount());

        ItemCS cs;

        if (cursor.moveToFirst()) {
            do {
                cs = new ItemCS();
                cs.setId(Integer.parseInt(cursor.getString(0)));
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


        cursor.close();
        db.close();

        // return books
        return cses;
    }
    public int updateCS(ItemCS cs) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("address", cs.getAddress());
        values.put("district", cs.getDistrict());
        values.put("chargingStation", cs.getDescription());
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

    public void deleteCS(ItemCS cs) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

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
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);

        db.execSQL("delete from "+ TABLE_NAME);

        db.close();
    }
}