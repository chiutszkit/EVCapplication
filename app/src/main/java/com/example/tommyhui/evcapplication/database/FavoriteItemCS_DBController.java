package com.example.tommyhui.evcapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class FavoriteItemCS_DBController {

    public static final String TABLE_NAME = "FavoriteOfChargingStation";

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

    public FavoriteItemCS_DBController(Context context) {
        db = DBHelper.getDatabase(context);
    }

    public void close() {
        db.close();
    }

    public FavoriteItemCS addFavoriteCS(FavoriteItemCS favoriteCS){

        // 1. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, favoriteCS.getAddress()); // get address
        values.put(KEY_DISTRICT, favoriteCS.getDistrict()); // get district
        values.put(KEY_DESCRIPTION, favoriteCS.getDescription()); // get charging station
        values.put(KEY_TYPE, favoriteCS.getType()); // get type
        values.put(KEY_SOCKET, favoriteCS.getSocket()); // get socket
        values.put(KEY_QUANTITY, favoriteCS.getQuantity()); // get quantity

        // 2. insert or update

        String[] input_column = new String[] {KEY_ADDRESS,KEY_DISTRICT, KEY_DESCRIPTION,KEY_TYPE,KEY_SOCKET,KEY_QUANTITY};
        String[] input_data = new String[]{favoriteCS.getAddress(),favoriteCS.getDistrict(), favoriteCS.getDescription(),favoriteCS.getType(),favoriteCS.getSocket(),Integer.toString(favoriteCS.getQuantity())};

        if(!checkRecordExist(TABLE_NAME, input_column, input_data))
        {
            // Perform the insert query
            db.insert(TABLE_NAME, null, values);
            Log.d("addFavoriteCS", favoriteCS.toString());

            // Return the item favoriteCS
            return favoriteCS;
        }

        // Return null if the favourite item was added before
        return null;
    }
    private boolean checkRecordExist(String tableName, String[] keys, String [] values) {

        StringBuilder sb = new StringBuilder();
        boolean exists;

        // 1. set up a string builder of comparing columns
        for (int i = 0; i < keys.length; i++) {
            sb.append(keys[i])
                    .append("=\"")
                    .append(values[i])
                    .append("\" ");
            if (i<keys.length-1) sb.append("AND ");
        }

        // 2. execute the query to search whether the record exists
        String query = "SELECT * FROM " + tableName + " WHERE " + sb.toString();
        Cursor cursor = db.rawQuery(query, null);

        exists = (cursor.getCount() > 0);
        Log.d("addFavoriteCS", "Exist? = " + exists);

        cursor.close();

        return exists;
    }

    public FavoriteItemCS getFavoriteCS(int id){

        // 1. build query
        Cursor cursor =
                db.query(TABLE_NAME, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 2. if we got results, get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 3. build cs object
        FavoriteItemCS favoriteCS = new FavoriteItemCS();
        favoriteCS.setAddress(cursor.getString(1));
        favoriteCS.setDistrict(cursor.getString(2));
        favoriteCS.setDescription(cursor.getString(3));
        favoriteCS.setType(cursor.getString(4));
        favoriteCS.setSocket(cursor.getString(5));
        favoriteCS.setQuantity(cursor.getInt(6));

        favoriteCS.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));

        Log.d("getFavoriteCS(" + id + ")", favoriteCS.toString());

        if(cursor != null) {
            cursor.close();
            return favoriteCS;
        }
        return null;
    }

    public ArrayList<FavoriteItemCS> getAllFavoriteCSes() {

        ArrayList<FavoriteItemCS> FavoriteCSes = new ArrayList<>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_NAME;

        // 2. get reference to writable DB
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build cs and add it to list
        FavoriteItemCS favoriteCS;
        if (cursor.moveToFirst()) {
            do {
                favoriteCS = new FavoriteItemCS();
                favoriteCS.setId(cursor.getInt(0));
                favoriteCS.setAddress(cursor.getString(1));
                favoriteCS.setDistrict(cursor.getString(2));
                favoriteCS.setDescription(cursor.getString(3));
                favoriteCS.setType(cursor.getString(4));
                favoriteCS.setSocket(cursor.getString(5));
                favoriteCS.setQuantity(Integer.parseInt(cursor.getString(6)));

                // Add cs to list of cs
                FavoriteCSes.add(favoriteCS);
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();

        Log.d("getAllFavoriteCSes", FavoriteCSes.toString());

        // 4. return list of cs
        return FavoriteCSes;
    }

    public int getFavoriteCSCount() {

        // 1. build the query
        String countQuery = "SELECT * FROM " + TABLE_NAME;

        // 2. execute the query to search whether the record exists
        Cursor cursor = db.rawQuery(countQuery, null);

        // 2. get the count
        int count = cursor.getCount();

        cursor.close();
        return count;
    }

    public int updateFavoriteCS(FavoriteItemCS favoriteCS) {

        // 1. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("address", favoriteCS.getAddress());
        values.put("district", favoriteCS.getDistrict());
        values.put("description", favoriteCS.getDescription());
        values.put("type", favoriteCS.getType());
        values.put("socket", favoriteCS.getSocket());
        values.put("quantity ", favoriteCS.getQuantity());

        // 2. updating row
        int i = db.update(TABLE_NAME, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(favoriteCS.getId()) }); //selection args

        // 3. return the update 
        return i;
    }

    public void deleteFavoriteCS(FavoriteItemCS favoriteCS) {

        // 1. delete
        db.delete(TABLE_NAME, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(favoriteCS.getId()) }); //selections args

        Log.d("deleteFavoriteCS", favoriteCS.toString());
    }

    public void clear()
    {
        db.execSQL("DROP TABLE "+ TABLE_NAME);
    }
}