package com.lhs.addressbook;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupDB();
    }

    // DB name
    private static String DB_NAME = "address_book";
    // Table name
    private static String TABLE_NAME = "contact";
    // String array has list Android versions which will be populated in the list
    private static final String[][] CONTACTS = {
            {"Walter", "Holladay", "801-555-8190", "walter@somewhere.com"},
            {"Jeff", "Campbell", "801-555-1234", "jeff@thechurch.com"},
            {"Dave", "Duncan", "801-555-5678", "dave@whatever.com"},
            {"Mike", "Scalora", "801-555-7665", "mike@acompany.com"},
    };

    private void setupDB() {
//        List<String> results = new ArrayList<String>();
        // Declare SQLiteDatabase object
        SQLiteDatabase sampleDB = null;


        try {
            // Instantiate sampleDB object
            sampleDB = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
            // Create table using execSQL
            sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (id INTEGER PRIMARY KEY, first_name VARCHAR, last_name VARCHAR, phone VARCHAR, email VARCHAR);");

            // Create Cursor object to read versions from the table
            Cursor c = sampleDB.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            // If there is no data in the table...
            if (c == null || c.getCount() == 0) {
                // Insert seed data into table.
                for (String[] contact : CONTACTS) {
                    sampleDB.execSQL("INSERT INTO " + TABLE_NAME + "(first_name, last_name, phone, email) VALUES ('" + contact[0] + "', '" + contact[1] + "', '" + contact[2] + "', '" + contact[3] + "');");
                }

                // Move cursor to first row
//                if (c.moveToFirst()) {
//                    do {
                        // Get version from Cursor
//                        String name = c.getString(c.getColumnIndex("name"));
                        // Add the version to Arraylist 'results'
//                        results.add(name);
//                    } while (c.moveToNext()); //Move to next row
//                }
            }

            // Set the ararylist to Android UI List
//            this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results));

        } catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(), "Couldn't create or open the database", Toast.LENGTH_LONG).show();
        } finally {
            if (sampleDB != null) {
//                sampleDB.execSQL("DELETE FROM " + tableName);
                sampleDB.close();
            }
        }
    }
}