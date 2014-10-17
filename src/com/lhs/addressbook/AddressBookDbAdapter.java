/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.lhs.addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple address book database access helper class. Defines the basic CRUD operations
 * for the contact table, and gives the ability to list all contacts as well as
 * retrieve or modify a specific contact.
 */
public class AddressBookDbAdapter {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";

    private static final String TAG = "AddressBookDbAdapter";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    private static final String DATABASE_NAME = "address_book";
    private static final String TABLE_NAME = "contact";
    private static final int DATABASE_VERSION = 3;

    /**
     * Database creation sql statement
     */
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            KEY_ROWID + " INTEGER PRIMARY KEY, " +
            KEY_FIRST_NAME + " TEXT NOT NULL, " +
            KEY_LAST_NAME + " TEXT NOT NULL, " +
            KEY_PHONE + " TEXT, " +
            KEY_EMAIL + " TEXT);";

    private final Context context;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param context the Context within which to work
     */
    public AddressBookDbAdapter(Context context) {
        this.context = context;
    }

    /**
     * Open the address_book database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure.
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public AddressBookDbAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Create a new contact using the fields provided. If the contact is
     * successfully created, return the new rowId for that contact, otherwise return
     * a -1 to indicate failure.
     *
     * @param firstName the contact's first name (required)
     * @param lastName  the contact's last name (required)
     * @param phone     phone number for the contact (optional)
     * @param email     email address for the contact (optional)
     * @return rowId or -1 if failed
     */
    public long createContact(String firstName, String lastName, String phone, String email) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FIRST_NAME, firstName);
        initialValues.put(KEY_LAST_NAME, lastName);
        if (phone != null) {
            initialValues.put(KEY_PHONE, phone);
        }
        if (email != null) {
            initialValues.put(KEY_EMAIL, email);
        }

        return database.insert(TABLE_NAME, null, initialValues);
    }

    /**
     * Delete the contact with the given rowId
     *
     * @param rowId id of contact to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteContact(long rowId) {

        return database.delete(TABLE_NAME, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all contacts in the database
     *
     * @return Cursor over all contact
     */
    public Cursor fetchAllContacts() {

        return database.query(TABLE_NAME, new String[]{KEY_ROWID, KEY_FIRST_NAME,
                KEY_LAST_NAME, KEY_PHONE, KEY_EMAIL}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the contact that matches the given rowId
     *
     * @param rowId id of contact to retrieve
     * @return Cursor positioned to matching contact, if found
     * @throws SQLException if contact could not be found/retrieved
     */
    public Cursor fetchContact(long rowId) throws SQLException {

        Cursor cursor = database.query(true, TABLE_NAME, new String[]{KEY_ROWID, KEY_FIRST_NAME, KEY_LAST_NAME,
                KEY_PHONE, KEY_EMAIL}, KEY_ROWID + "=" + rowId, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    /**
     * Update the contact using the details provided. The contact to be updated is specified using the rowId, and it is
     * altered to use the field values passed in.
     *
     * @param rowId id of note to update
     * @param firstName value to set contact's first name to
     * @param lastName  value to set contact's last name to
     * @param phone value to set the contact's phone number to
     * @param email value to set the contact's email address to
     * @return true if the contact was successfully updated, false otherwise
     */
    public boolean updateContact(long rowId, String firstName, String lastName, String phone, String email) {

        ContentValues args = new ContentValues();
        args.put(KEY_FIRST_NAME, firstName);
        args.put(KEY_LAST_NAME, lastName);
        args.put(KEY_PHONE, phone);
        args.put(KEY_EMAIL, email);

        return database.update(TABLE_NAME, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}

//import android.database.Cursor;
//
//import android.database.sqlite.SQLiteDatabase;
//
///**
// * Created by wholladay on 10/16/14.
// */
//public class DBHelper {
//
//    // DB name
//    private static String DB_NAME = "address_book";
//    // Table name
//    private static String TABLE_NAME = "contact";
//    // String array has list Android versions which will be populated in the list
//    private static final String[][] CONTACTS = {
//            {"Walter", "Holladay", "801-555-8190", "walter@somewhere.com"},
//            {"Jeff", "Campbell", "801-555-1234", "jeff@thechurch.com"},
//            {"Dave", "Duncan", "801-555-5678", "dave@whatever.com"},
//            {"Mike", "Scalora", "801-555-7665", "mike@acompany.com"},
//    };
//
//    public static void setupDB() {
////        List<String> results = new ArrayList<String>();
//        // Declare SQLiteDatabase object
//        SQLiteDatabase sampleDB = null;
//
//
//        try {
//            // Instantiate sampleDB object
//            sampleDB = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
//            // Create table using execSQL
//            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (id INTEGER PRIMARY KEY, first_name VARCHAR, last_name VARCHAR, phone VARCHAR, email VARCHAR);");
//
//            // Create Cursor object to read versions from the table
//            Cursor c = sampleDB.rawQuery("SELECT * FROM " + TABLE_NAME, null);
//            // If there is no data in the table...
//            if (c == null || c.getCount() == 0) {
//                // Insert seed data into table.
//                for (String[] contact : CONTACTS) {
//                    sampleDB.execSQL("INSERT INTO " + TABLE_NAME + "(first_name, last_name, phone, email) VALUES ('" + contact[0] + "', '" + contact[1] + "', '" + contact[2] + "', '" + contact[3] + "');");
//                }
//
//                // Move cursor to first row
////                if (c.moveToFirst()) {
////                    do {
//                // Get version from Cursor
////                        String name = c.getString(c.getColumnIndex("name"));
//                // Add the version to Arraylist 'results'
////                        results.add(name);
////                    } while (c.moveToNext()); //Move to next row
////                }
//            }
//
//            // Set the ararylist to Android UI List
////            this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results));
//
//        } catch (SQLiteException se) {
//            Toast.makeText(getApplicationContext(), "Couldn't create or open the database", Toast.LENGTH_LONG).show();
//        } finally {
//            if (sampleDB != null) {
////                sampleDB.execSQL("DELETE FROM " + tableName);
//                sampleDB.close();
//            }
//        }
//    }
//}
