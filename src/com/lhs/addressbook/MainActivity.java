package com.lhs.addressbook;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {

    public static final int INSERT_ID = Menu.FIRST;

    private int contactNumber = 1;
    private AddressBookDbAdapter addressBookDbAdapter;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        addressBookDbAdapter = new AddressBookDbAdapter(this);
        addressBookDbAdapter.open();
        fillData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNote() {
        String firstName = "firstName" + contactNumber;
        String lastName = "lastName" + contactNumber++;
        addressBookDbAdapter.createContact(firstName, lastName, null, null);
        fillData();
    }

    private void fillData() {
        // Get all of the contacts from the database and create the item list
        Cursor cursor = addressBookDbAdapter.fetchAllContacts();
        startManagingCursor(cursor);

        String[] from = new String[] { AddressBookDbAdapter.KEY_FIRST_NAME };
        int[] to = new int[] { R.id.name };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter contacts = new SimpleCursorAdapter(this, R.layout.contact_row, cursor, from, to);
        setListAdapter(contacts);
    }
}