package com.example.seu_is_20_ict_046_assignment02;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "contacts.db";
    private static final int DB_VERSION = 2; // bumped to 2 because schema changed

    public static final String TABLE_CONTACTS = "contacts";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PHONE = "phone";
    public static final String COL_EMAIL = "email";
    public static final String COL_IMAGE = "image_uri"; // new

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CONTACTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_IMAGE + " TEXT)";
        db.execSQL(createTable);
    }

    // simple migration: drop old and recreate (for dev). In production you'd write ALTER TABLE.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If upgrading from v1 -> v2 where image column missing, drop & recreate:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    // Insert contact (imageUri may be null)
    public long addContact(String name, String phone, String email, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_PHONE, phone);
        values.put(COL_EMAIL, email);
        values.put(COL_IMAGE, imageUri);
        long id = db.insert(TABLE_CONTACTS, null, values);
        // db.close(); // avoid closing here in case caller uses DB afterwards
        return id;
    }

    // Get all contacts
    public Cursor getAllContacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " ORDER BY " + COL_NAME + " COLLATE NOCASE", null);
    }

    // Get contact by ID
    public Cursor getContactById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CONTACTS, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
    }

    // Search contacts by name
    public Cursor searchContactsByName(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COL_NAME + " LIKE ?",
                new String[]{"%" + keyword + "%"});
    }

    // Delete Contact
    public void deleteContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Update existing contact
    public boolean updateContact(int id, String name, String phone, String email, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_PHONE, phone);
        values.put(COL_EMAIL, email);
        values.put(COL_IMAGE, imageUri);

        int rows = db.update(TABLE_CONTACTS, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }
}

