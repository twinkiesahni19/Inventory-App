package com.example.android.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.security.Provider;

import static android.R.attr.id;

/**
 * Created by Twinkle Sahni on 11-May-17.
 */

public class ItemProvider extends ContentProvider {

    private static final String LOG_TAG = Provider.class.getSimpleName();

    private ItemDbHelper mItemDbHelper;

    private static final int INVENTORY = 1000;

    private static final int INVENTORY_ID = 2000;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_PRODUCTS, INVENTORY);
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_PRODUCTS + "/#", INVENTORY_ID);
    }

    @Override
    public boolean onCreate() {
        mItemDbHelper = new ItemDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mItemDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor cursor;

        switch (match) {
            case INVENTORY:
                cursor = database.query(ItemContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            case INVENTORY_ID:
                selection = ItemContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ItemContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return ItemContract.InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return ItemContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertInventory(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertInventory(Uri uri, ContentValues values) {

        if (values.containsKey(ItemContract.InventoryEntry.PRODUCT_PRICE)) {
            int Price = values.getAsInteger(ItemContract.InventoryEntry.PRODUCT_PRICE);
            if (Price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        if (values.containsKey(ItemContract.InventoryEntry.PRODUCT_QUANTITY)) {
            int Quantity = values.getAsInteger(ItemContract.InventoryEntry.PRODUCT_QUANTITY);
            if (Quantity < 0) {
                Toast.makeText(getContext(), "Quantity cannot be negative", Toast.LENGTH_SHORT);
                throw new IllegalArgumentException("Product requires valid price");
            }
        }
        SQLiteDatabase db = mItemDbHelper.getWritableDatabase();
        long newRowId = db.insert(ItemContract.InventoryEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Log.i(LOG_TAG, "Error inserting the product");
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        SQLiteDatabase database = mItemDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowsDeleted = database.delete(ItemContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case INVENTORY_ID:
                selection = ItemContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ItemContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);

                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mItemDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateProduct(values, selection, selectionArgs);
            case INVENTORY_ID:
                selection = ItemContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return updateProduct(values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Updation is not supported for " + uri);
        }
    }


    private int updateProduct(ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        if (values.containsKey(ItemContract.InventoryEntry.PRODUCT_PRICE)) {
            int p__price = values.getAsInteger(ItemContract.InventoryEntry.PRODUCT_PRICE);
            if (p__price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        if (values.containsKey(ItemContract.InventoryEntry.PRODUCT_QUANTITY)) {
            int p__quantity = values.getAsInteger(ItemContract.InventoryEntry.PRODUCT_QUANTITY);
            if (p__quantity < 0) {
                Toast.makeText(getContext(), "Quantity cannot be negative", Toast.LENGTH_SHORT);
                throw new IllegalArgumentException("Product requires valid price");
            }
        }

        SQLiteDatabase db = mItemDbHelper.getWritableDatabase();

        return db.update(ItemContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

    }
}
