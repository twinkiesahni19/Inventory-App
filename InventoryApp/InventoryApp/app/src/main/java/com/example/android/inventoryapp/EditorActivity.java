package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Twinkle Sahni on 11-May-17.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    static final int TAKE_IMAGE = 101;
    Uri imageUri;
    private EditText mName;
    private EditText mPrice;
    private EditText mQuantity;
    private Uri ProductUri;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mName = (EditText) findViewById(R.id.nameText);
        mPrice = (EditText) findViewById(R.id.priceText);
        mQuantity = (EditText) findViewById(R.id.quantityText);
        mImage = (ImageView) findViewById(R.id.image);

        final Intent intent = getIntent();
        ProductUri = intent.getData();

        if (ProductUri == null) {
            setTitle("Add a Product");
        } else {
            setTitle("Edit a product");
            getLoaderManager().initLoader(0, null, this);
        }

        Button order = (Button) findViewById(R.id.orderText);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = mName.getText().toString();
                String productQuan = mQuantity.getText().toString();

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Order for the following product");
                intent.putExtra(Intent.EXTRA_TEXT, "Product :" + productName + "\n" + " Quantity :" + productQuan);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        Button add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int quantity = Integer.parseInt(mQuantity.getText().toString());
                quantity = quantity + 1;
                mQuantity.setText(String.valueOf(quantity));
            }
        });
        Button sub = (Button) findViewById(R.id.sub);
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(mQuantity.getText().toString());
                if (quantity <= 0) {
                    Toast.makeText(EditorActivity.this, "Quantity cannot be less than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                quantity = quantity - 1;
                mQuantity.setText(String.valueOf(quantity));
            }
        });
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE);
                }
            }
        });
    }


    private void saveProduct() {
        String Name = mName.getText().toString().trim();
        String Quantity = mQuantity.getText().toString().trim();
        String Price = mPrice.getText().toString().trim();
        String imageUriString = "";
        if (imageUri != null) {
            imageUriString = imageUri.toString();
        }

        if (ProductUri == null &&
                TextUtils.isEmpty(Name) && TextUtils.isEmpty(Price) &&
                TextUtils.isEmpty(Quantity)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ItemContract.InventoryEntry.PRODUCT_NAME, Name);
        values.put(ItemContract.InventoryEntry.PRODUCT_QUANTITY, Quantity);
        values.put(ItemContract.InventoryEntry.PRODUCT_PRICE, Price);
        values.put(ItemContract.InventoryEntry.PRODUCT_IMAGE, imageUriString);
        if (ProductUri == null) {
            Uri insert = getContentResolver().insert(ItemContract.InventoryEntry.CONTENT_URI, values);
            if (insert != null) {
                Toast.makeText(this, "New Item saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error with saving Item", Toast.LENGTH_SHORT).show();
            }
        } else {
            int insert = getContentResolver().update(ProductUri, values, null, null);
            if (insert != -1) {
                Toast.makeText(this, "Item Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error with updating Item", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            mImage.setImageURI(Uri.parse(String.valueOf(imageUri)));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void save(View view) {
        if (checkFields()) {
            saveProduct();
            finish();
        }
    }

    public void delete(View view) {
        showDeleteConfirmation();
    }

    private void showDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (ProductUri != null) {
                    getContentResolver().delete(ProductUri, null, null);
                    finish();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean checkFields() {
        String pr_name = mName.getText().toString().trim();
        String pr_price = mPrice.getText().toString().trim();
        String pr_quantity = mQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(pr_name) || TextUtils.isEmpty(pr_price) || TextUtils.isEmpty(pr_quantity)) {
            mName.setError("Enter name");
            mPrice.setError("Enter price");
            mQuantity.setError("Enter quantity");
            return false;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ItemContract.InventoryEntry._ID,
                ItemContract.InventoryEntry.PRODUCT_NAME,
                ItemContract.InventoryEntry.PRODUCT_QUANTITY,
                ItemContract.InventoryEntry.PRODUCT_PRICE,
                ItemContract.InventoryEntry.PRODUCT_IMAGE};

        return new CursorLoader(this, ProductUri, projection, null, null, null);
    }

    @Override
    public void onBackPressed() {
        Log.i("Back button pressed ", "Back button pressed");
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("DIscard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int Name = cursor.getColumnIndex(ItemContract.InventoryEntry.PRODUCT_NAME);
            int Quantity = cursor.getColumnIndex(ItemContract.InventoryEntry.PRODUCT_QUANTITY);
            int Price = cursor.getColumnIndex(ItemContract.InventoryEntry.PRODUCT_PRICE);

            String name = cursor.getString(Name);
            int quantity = cursor.getInt(Quantity);
            int price = cursor.getInt(Price);

            String imageUriString = cursor.getString(cursor.getColumnIndex(ItemContract.InventoryEntry.PRODUCT_IMAGE));
            if (imageUriString.startsWith("content://com.android.providers.media.documents/document/image")) {
                mImage.setImageURI(Uri.parse(imageUriString));
            } else {
                mImage.setImageResource(R.drawable.akshardhamm);
            }

            mName.setText(name);
            mPrice.setText(Integer.toString(price));
            mQuantity.setText(Integer.toString(quantity));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mImage.setImageURI(null);
    }
}
