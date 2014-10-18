package com.lhs.addressbook;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.lhs.addressbook.AddressBookDbAdapter.KEY_EMAIL;
import static com.lhs.addressbook.AddressBookDbAdapter.KEY_FIRST_NAME;
import static com.lhs.addressbook.AddressBookDbAdapter.KEY_LAST_NAME;
import static com.lhs.addressbook.AddressBookDbAdapter.KEY_PHONE;
import static com.lhs.addressbook.AddressBookDbAdapter.KEY_ROWID;

/**
 * Activity for editing a single contact.
 * <p/>
 * Created by wholladay on 10/17/14.
 */
public class ContactEdit extends Activity {

    private AddressBookDbAdapter abHelper;
    private Long rowId;
    private EditText firstNameText;
    private EditText lastNameText;
    private EditText phoneText;
    private EditText emailText;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        abHelper = new AddressBookDbAdapter(this);
        abHelper.open();

        setContentView(R.layout.contact_edit);
        setTitle(R.string.edit_contact);
        firstNameText = (EditText) findViewById(R.id.first_name);
        lastNameText = (EditText) findViewById(R.id.last_name);
        phoneText = (EditText) findViewById(R.id.phone);
        emailText = (EditText) findViewById(R.id.email);

        rowId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(KEY_ROWID);
        if (rowId == null) {
            Bundle extras = getIntent().getExtras();
            rowId = extras != null ? extras.getLong(KEY_ROWID) : null;
        }
        populateFields();

        Button confirmButton = (Button) findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(KEY_ROWID, rowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void populateFields() {
        if (rowId != null) {
            Cursor contact = abHelper.fetchContact(rowId);
            startManagingCursor(contact);
            firstNameText.setText(contact.getString(contact.getColumnIndexOrThrow(KEY_FIRST_NAME)));
            lastNameText.setText(contact.getString(contact.getColumnIndexOrThrow(KEY_LAST_NAME)));
            phoneText.setText(contact.getString(contact.getColumnIndexOrThrow(KEY_PHONE)));
            emailText.setText(contact.getString(contact.getColumnIndexOrThrow(KEY_EMAIL)));
        }
    }

    private void saveState() {
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String phone = phoneText.getText().toString();
        String email = emailText.getText().toString();

        if (rowId == null) {
            long id = abHelper.createContact(firstName, lastName, phone, email);
            if (id > 0) {
                rowId = id;
            }
        } else {
            abHelper.updateContact(rowId, firstName, lastName, phone, email);
        }
    }
}