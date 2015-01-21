package org.misoton.goodays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AddressHistoryManager {
    private static final String TAG = "AddressHisoryManager";
    private static final String DB = "address_autocomplete.db";
    private static final int DB_VERSION = 1;
    private static final String CREATE_TABLE = "create table address ( _id integer primary key autoincrement, name text, lat real, lon real, last integer );";
    private static final String DROP_TABLE = "drop table address;";

    private static class AddressHistoryDBOpenHelper extends SQLiteOpenHelper {

        public AddressHistoryDBOpenHelper(Context context) {
            super(context, DB, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
    }

    private static SQLiteDatabase addressDB;

    public static void init(Context context) {
        AddressHistoryDBOpenHelper dbOpenHelper = new AddressHistoryDBOpenHelper(context);
        addressDB = dbOpenHelper.getWritableDatabase();
    }

    public static boolean addHistory(String name, double lat, double lon) {
        if (DBisNull()) {
            Log.d(TAG, "addressDB is null");
            return false;
        }

        List<AddressHistory> historyList = getHistories();
        AddressHistory tester = new AddressHistory(0, name, lat, lon);

        for (AddressHistory history : historyList) {
            if (history.getName().equals(name)) {
                Log.d(TAG, "name = \"" + name + "\" is already exist.");
                return false;
            }
        }

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("lat", lat);
        values.put("lon", lon);
        addressDB.insert("address", null, values);
        return true;
    }

    public static List<AddressHistory> getHistories() {
        String[] data = new String[]{"_id, name, lat, lon"};


        if (DBisNull()) {
            Log.d(TAG, "addressDB is null");
            return null;
        }


        Cursor cursor = addressDB.query("address", data, null, null, null, null, null);

        Log.d(TAG, "columun count: " + cursor.getColumnCount());

        List<AddressHistory> historyList = new ArrayList<>();

        for (cursor.moveToFirst(); cursor.moveToNext(); ) {
            Log.d(TAG, "name: " + cursor.getString(cursor.getColumnIndex("name")));
            historyList.add(new AddressHistory(
                    cursor.getInt(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getDouble(cursor.getColumnIndex("lat")),
                    cursor.getDouble(cursor.getColumnIndex("lon"))
            ));
        }

        return historyList;
    }

    public static boolean DBisNull() {
        return addressDB == null;
    }
}
