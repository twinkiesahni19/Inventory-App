package com.example.android.inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Twinkle Sahni on 11-May-17.
 */

public class ItemDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Products.db";

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_TABLE = "CREATE TABLE " + ItemContract.InventoryEntry.TABLE_NAME + " ("
                + ItemContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemContract.InventoryEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + ItemContract.InventoryEntry.PRODUCT_QUANTITY + " INTEGER, "
                + ItemContract.InventoryEntry.PRODUCT_PRICE + " INTEGER, "
                + ItemContract.InventoryEntry.PRODUCT_IMAGE + " TEXT );";

        db.execSQL(SQL_CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

