package com.example.tommyhui.evcapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MyDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "charging_station_list.db";
    public static final String TABLE_NAME = "ChargingStation";

    public static final int VERSION = 1;
    private static MyDBHelper database;

    private static final String KEY_ID = "id";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_CHARGINGSTATION = "chargingStation";
    private static final String KEY_TYPE = "type";
    private static final String KEY_SOCKET = "socket";
    private static final String KEY_QUANTITY = "quantity";

    private static final String[] COLUMNS = {KEY_ID,KEY_ADDRESS,KEY_CHARGINGSTATION,KEY_TYPE,KEY_SOCKET,KEY_QUANTITY};
/*    public MyDBHelper(Context context, String name, CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }*/
    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


/*    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static MyDBHelper getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new MyDBHelper(context, DATABASE_NAME,
                    null, VERSION).getWritableDatabase();
        }
        return database;
    }*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建立應用程式需要的表格
        // 待會再回來完成它
        final String DATABASE_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "address TEXT, " +
                "chargingStation TEXT, " +
                "type TEXT," +
                "socket TEXT," +
                "quantity INTEGER" +
                ");";

        db.execSQL(DATABASE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        // 待會再回來完成它
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }

    @Override
       public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // TODO 每次成功打開數據庫後首先被執行
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
        values.put(KEY_CHARGINGSTATION, cs.getChargingStation()); // get charging station
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
        cs.setId(Integer.parseInt(cursor.getString(0)));
        cs.setAddress(cursor.getString(1));
        cs.setChargingStation(cursor.getString(2));
        cs.setType(cursor.getString(3));
        cs.setSocket(cursor.getString(4));
        cs.setQuantity(Integer.parseInt(cursor.getString(5)));

        //log
        Log.d("getCS("+id+")", cs.toString());

        // 5. return book
        return cs;
    }
    public List<ItemCS> getAllCSes() {
        List<ItemCS> cses = new LinkedList<ItemCS>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_NAME;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        ItemCS cs = null;
        if (cursor.moveToFirst()) {
            do {
                cs = new ItemCS();
                cs.setId(Integer.parseInt(cursor.getString(0)));
                cs.setAddress(cursor.getString(1));
                cs.setChargingStation(cursor.getString(2));
                cs.setType(cursor.getString(3));
                cs.setSocket(cursor.getString(4));
                cs.setQuantity(Integer.parseInt(cursor.getString(5)));

                // Add book to books
                cses.add(cs);
            } while (cursor.moveToNext());
        }

        Log.d("getAllCSes()", cses.toString());

        // return books
        return cses;
    }
    public int updateCS(ItemCS cs) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("address", cs.getAddress());
        values.put("chargingStation", cs.getChargingStation());
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
}