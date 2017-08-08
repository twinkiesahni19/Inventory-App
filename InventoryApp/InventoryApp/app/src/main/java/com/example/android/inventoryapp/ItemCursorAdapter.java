package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Twinkle Sahni on 11-May-17.
 */

public class ItemCursorAdapter extends CursorAdapter {
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        int Name = cursor.getColumnIndex(ItemContract.InventoryEntry.PRODUCT_NAME);
        int Price = cursor.getColumnIndex(ItemContract.InventoryEntry.PRODUCT_PRICE);
        final int Quantity = cursor.getColumnIndex(ItemContract.InventoryEntry.PRODUCT_QUANTITY);
        int Id = cursor.getColumnIndex(ItemContract.InventoryEntry._ID);

        final long propid = cursor.getLong(Id);
        String itemName = cursor.getString(Name);
        int itemPrice = cursor.getInt(Price);
        int itemQuantity = cursor.getInt(Quantity);

        TextView product_name = (TextView) view.findViewById(R.id.itemName1);
        final TextView product_quantity = (TextView) view.findViewById(R.id.itemQuantity);
        TextView product_price = (TextView) view.findViewById(R.id.itemPrice);
        ImageView Sale = (ImageView) view.findViewById(R.id.sale1);

        product_name.setText(itemName);
        product_price.setText(Integer.toString(itemPrice));
        product_quantity.setText(Integer.toString(itemQuantity));


        Sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(product_quantity.getText().toString());
                if (quantity > 0) {

                    quantity--;
                    product_quantity.setText(Integer.toString(quantity));
                    ContentValues values = new ContentValues();
                    values.put(ItemContract.InventoryEntry.PRODUCT_QUANTITY, quantity);
                    Uri currentUri = ContentUris.withAppendedId(ItemContract.InventoryEntry.CONTENT_URI, propid);
                    context.getContentResolver().update(currentUri, values, null, null);
                }
            }
        });

    }
}
