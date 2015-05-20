package com.example.tommyhui.evcapplication.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tommyhui.evcapplication.menu.MenuActivity;
import com.example.tommyhui.evcapplication.overview.OverviewActivity;
import com.example.tommyhui.evcapplication.search.SearchActivity;
import com.example.tommyhui.evcapplication.search.SearchResultActivity;
import com.example.tommyhui.evcapplication.socket.SocketListActivity;

import java.util.ArrayList;

public class ItemCS_DBController {

    public static final String TABLE_NAME = "ChargingStation";

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

    public ItemCS_DBController(Context context) {
        db = DBHelper.getDatabase(context);
    }

    public void close() {
        db.close();
    }

    public ItemCS addCS(ItemCS cs) {

        // 1. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, cs.getAddress()); // get address
        values.put(KEY_DISTRICT, cs.getDistrict()); // get district
        values.put(KEY_DESCRIPTION, cs.getDescription()); // get charging station
        values.put(KEY_TYPE, cs.getType()); // get type
        values.put(KEY_SOCKET, cs.getSocket()); // get socket
        values.put(KEY_QUANTITY, cs.getQuantity()); // get quantity
        values.put(KEY_LATITUDE, cs.getLatitude()); // get latitude
        values.put(KEY_LONGITUDE, cs.getLongitude()); // get longitude

        // 2. insert or update

        String[] input_column = new String[]{KEY_ADDRESS, KEY_DISTRICT, KEY_DESCRIPTION, KEY_TYPE, KEY_SOCKET, KEY_QUANTITY};
        String[] input_data = new String[]{cs.getAddress(), cs.getDistrict(), cs.getDescription(), cs.getType(), cs.getSocket(), Integer.toString(cs.getQuantity())};

        if (!checkRecordExist(TABLE_NAME, input_column, input_data)) {
            // Perform the insert query
            db.insert(TABLE_NAME, null, values);
            Log.d("addCS", cs.toString());
        }

        // 3. return the item cs
        return cs;
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
        Log.d("addCS", "Exist? = " + exists);

        cursor.close();

        return exists;
    }

    public ItemCS getCS(int id) {

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
        ItemCS cs = new ItemCS();
        cs.setAddress(cursor.getString(1));
        cs.setDistrict(cursor.getString(2));
        cs.setDescription(cursor.getString(3));
        cs.setType(cursor.getString(4));
        cs.setSocket(cursor.getString(5));
        cs.setQuantity(cursor.getInt(6));
        cs.setLatitude(cursor.getString(7));
        cs.setLongitude(cursor.getString(8));

        cs.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));

        Log.d("getCS(" + id + ")", cs.toString());

        if (cursor != null)
            cursor.close();

        return cs;
    }

    public ArrayList<ItemCS> getAllCSes() {

        ArrayList<ItemCS> cses = new ArrayList<>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_NAME;

        // 2. get reference to writable DB
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build cs and add it to list
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
                cs.setLatitude(cursor.getString(7));
                cs.setLongitude(cursor.getString(8));

                // Add cs to list of cs
                cses.add(cs);
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();

        Log.d("getAllCSes()", cses.toString());

        // 4. return list of cs
        return cses;
    }

    public int getItemCSCount() {

        // 1. build the query
        String countQuery = "SELECT * FROM " + TABLE_NAME;

        // 2. execute the query to search whether the record exists
        Cursor cursor = db.rawQuery(countQuery, null);

        // 2. get the count
        int count = cursor.getCount();

        cursor.close();
        return count;
    }

    public ArrayList<ItemCS> inputQueryCSes(Activity activity, String[] query, int numberQuery) {

        ArrayList<ItemCS> cses = new ArrayList<>();
        String sql = "";

        if (activity instanceof MenuActivity && (numberQuery == 1))
            sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + KEY_DISTRICT + ", " + KEY_DESCRIPTION;

        else if (activity instanceof OverviewActivity && (numberQuery == 1))
            sql = "SELECT DISTINCT * FROM " + TABLE_NAME + " WHERE " + KEY_ADDRESS + " LIKE '%" + query[0] + "%'"
                    + " OR " + KEY_DESCRIPTION + " LIKE '%" + query[0] + "%' ORDER BY " + KEY_DISTRICT + ", " + KEY_DESCRIPTION;

        else if (activity instanceof SocketListActivity && (numberQuery == 1))
            sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_TYPE + " LIKE '%" + query[0]
                    + "%' GROUP BY " + KEY_ADDRESS + " ORDER BY " + KEY_DISTRICT;

        else if (activity instanceof SearchActivity) {
            if (numberQuery == 1)
                sql = "SELECT * FROM " + TABLE_NAME + " GROUP BY " + query[0] + " ORDER BY " + query[0];
            else if (numberQuery == 2)
                sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_DESCRIPTION + " = '" + query[0] + "'";
        }

        else if (activity instanceof SearchResultActivity) {
            String[] input_column = new String[]{KEY_DISTRICT, KEY_DESCRIPTION, KEY_TYPE, KEY_SOCKET, KEY_QUANTITY};
            sql = searchResultSqlGenerator(input_column, query) + "ORDER BY " + KEY_DESCRIPTION;
        }


        Cursor cursor = db.rawQuery(sql, null);
        Log.d("search", "[Match Result = " + cursor.getCount() + "] " + sql);

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
                cs.setLatitude(cursor.getString(7));
                cs.setLongitude(cursor.getString(8));

                // Add cs to list of cs
                cses.add(cs);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return list of cs
        return cses;
    }

    public String searchResultSqlGenerator(String[] column, String[] query) {

        StringBuilder sb = new StringBuilder();

        int count = 0;
        int countValue = 0;

        for (int i = 0; i < query.length; i++) {
            if (!query[i].equals("ALL"))
                count += 1;
        }
        for (int i = 0; i < column.length; i++) {
            if(!query[i].equals("ALL")) {
                if(column[i] != KEY_QUANTITY) {
                    sb.append(column[i])
                            .append(" =\"")
                            .append(query[i])
                            .append("\" ");
                    if (countValue < count - 1) sb.append("AND ");
                }
                else {
                    sb.append(column[i])
                            .append("='")
                            .append(query[i])
                            .append("' ");
                    if (countValue < count - 1) sb.append("AND ");
                }
                countValue += 1;
            }
        }
        String sql = "SELECT * FROM " + TABLE_NAME + " ";
        if(sb.length() > 0)
            sql += "WHERE " + sb.toString();

        return sql;
    }
    public int updateCS(ItemCS cs) {

        // 1. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("address", cs.getAddress());
        values.put("district", cs.getDistrict());
        values.put("description", cs.getDescription());
        values.put("type", cs.getType());
        values.put("socket", cs.getSocket());
        values.put("quantity ", cs.getQuantity());
        values.put("latitude", cs.getLatitude());
        values.put("longitude", cs.getLongitude());

        // 2. updating row
        int i = db.update(TABLE_NAME, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(cs.getId())}); //selection args

        // 3. return the update
        return i;
    }

    public void deleteCS(ItemCS cs) {

        // 1. delete
        db.delete(TABLE_NAME, //table name
                KEY_ID + " = ?",  // selections
                new String[]{String.valueOf(cs.getId())}); //selections args

        Log.d("deletecs", cs.toString());
    }

    public void clear() {
        db.execSQL("DROP TABLE " + TABLE_NAME);
    }
}