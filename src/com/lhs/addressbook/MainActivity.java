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

import static com.lhs.addressbook.AddressBookDbAdapter.KEY_FIRST_NAME;
import static com.lhs.addressbook.AddressBookDbAdapter.KEY_LAST_NAME;
import static com.lhs.addressbook.AddressBookDbAdapter.KEY_ROWID;

/**
 * The main activity, which displays a list of contacts.
 * <p/>
 * Created by wholladay on 10/15/14.
 */
public class MainActivity extends ListActivity {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private AddressBookDbAdapter abHelper;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        abHelper = new AddressBookDbAdapter(this);
        abHelper.open();
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
                abHelper.deleteContact(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ContactEdit.class);
        i.putExtra(KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    private void createContact() {

        Intent i = new Intent(this, ContactEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void fillData() {
        // Get all of the contacts from the database and create the item list
        Cursor contactsCursor = abHelper.fetchAllContacts();
        startManagingCursor(contactsCursor);

        String[] from = new String[]{KEY_FIRST_NAME, KEY_LAST_NAME};
        int[] to = new int[]{R.id.first_name, R.id.last_name};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter contacts = new SimpleCursorAdapter(this, R.layout.contact_row, contactsCursor, from, to);
        setListAdapter(contacts);
    }
}