package com.lhs.addressbook;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import static com.lhs.addressbook.AddressBookDbAdapter.KEY_EMAIL;
import static com.lhs.addressbook.AddressBookDbAdapter.KEY_FIRST_NAME;
import static com.lhs.addressbook.AddressBookDbAdapter.KEY_LAST_NAME;
import static com.lhs.addressbook.AddressBookDbAdapter.KEY_PHONE;
import static com.lhs.addressbook.AddressBookDbAdapter.KEY_ROWID;

public class MainActivity extends ListActivity {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private AddressBookDbAdapter addressBookDbAdapter;
    private Cursor contactsCursor;

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
        registerForContextMenu(getListView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case INSERT_ID:
                createContact();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                addressBookDbAdapter.deleteContact(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        super.onListItemClick(l, v, position, id);
        Cursor c = contactsCursor;
        c.moveToPosition(position);
        Intent i = new Intent(this, ContactEdit.class);
        i.putExtra(KEY_ROWID, id);
        i.putExtra(KEY_FIRST_NAME, c.getString(c.getColumnIndexOrThrow(KEY_FIRST_NAME)));
        i.putExtra(KEY_LAST_NAME, c.getString(c.getColumnIndexOrThrow(KEY_LAST_NAME)));
        i.putExtra(KEY_PHONE, c.getString(c.getColumnIndexOrThrow(KEY_PHONE)));
        i.putExtra(KEY_EMAIL, c.getString(c.getColumnIndexOrThrow(KEY_EMAIL)));
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extras = intent.getExtras();

        switch (requestCode) {
            case ACTIVITY_CREATE:
                String firstName = extras.getString(KEY_FIRST_NAME);
                String lastName = extras.getString(KEY_LAST_NAME);
                String phone = extras.getString(KEY_PHONE);
                String email = extras.getString(KEY_EMAIL);
                addressBookDbAdapter.createContact(firstName, lastName, phone, email);
                fillData();
                break;
            case ACTIVITY_EDIT:
                long rowId = extras.getLong(KEY_ROWID);
                if (rowId > 0) {
                    String editFirstName = extras.getString(KEY_FIRST_NAME);
                    String editLastName = extras.getString(KEY_LAST_NAME);
                    String editPhone = extras.getString(KEY_PHONE);
                    String editEmail = extras.getString(KEY_EMAIL);
                    addressBookDbAdapter.updateContact(rowId, editFirstName, editLastName, editPhone, editEmail);
                }
                fillData();
                break;
        }

    }

    private void createContact() {

        Intent i = new Intent(this, ContactEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void fillData() {
        // Get all of the contacts from the database and create the item list
        contactsCursor = addressBookDbAdapter.fetchAllContacts();
        startManagingCursor(contactsCursor);

        String[] from = new String[]{AddressBookDbAdapter.KEY_FIRST_NAME};
        int[] to = new int[]{R.id.name};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter contacts = new SimpleCursorAdapter(this, R.layout.contact_row, contactsCursor, from, to);
        setListAdapter(contacts);
    }
}