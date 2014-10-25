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
    private long rowId;
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

        if (savedInstanceState != null) {
            populateFieldsFromBundle(savedInstanceState);
        } else {
            populateFieldsFromDB();
        }

        Button confirmButton = (Button) findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                saveState();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (rowId > 0) {
            outState.putLong(KEY_ROWID, rowId);
        }
        saveTextField(outState, KEY_FIRST_NAME, firstNameText);
        saveTextField(outState, KEY_LAST_NAME, lastNameText);
        saveTextField(outState, KEY_PHONE, phoneText);
        saveTextField(outState, KEY_EMAIL, emailText);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        populateFieldsFromBundle(savedInstanceState);
    }

    private void saveTextField(Bundle outState, String key, EditText field) {

        String val = field.getText().toString();
        if (val != null) {
            outState.putString(key, val);
        }
    }

    private void restoreTextField(Bundle savedInstanceState, String key, EditText field) {

        String val = savedInstanceState.getString(key);
        if (val != null) {
            field.setText(val);
        }
    }

    private void populateFieldsFromDB() {

        Bundle extras = getIntent().getExtras();
        rowId = extras != null ? extras.getLong(KEY_ROWID) : 0;
        if (rowId > 0) {
            Cursor contact = abHelper.fetchContact(rowId);
            startManagingCursor(contact);
            firstNameText.setText(contact.getString(contact.getColumnIndexOrThrow(KEY_FIRST_NAME)));
            lastNameText.setText(contact.getString(contact.getColumnIndexOrThrow(KEY_LAST_NAME)));
            phoneText.setText(contact.getString(contact.getColumnIndexOrThrow(KEY_PHONE)));
            emailText.setText(contact.getString(contact.getColumnIndexOrThrow(KEY_EMAIL)));
        }
    }

    private void populateFieldsFromBundle(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            rowId = savedInstanceState.getLong(KEY_ROWID);
            restoreTextField(savedInstanceState, KEY_FIRST_NAME, firstNameText);
            restoreTextField(savedInstanceState, KEY_LAST_NAME, lastNameText);
            restoreTextField(savedInstanceState, KEY_PHONE, phoneText);
            restoreTextField(savedInstanceState, KEY_EMAIL, emailText);
        } else {
            rowId = 0;
        }
    }

    private void saveState() {
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String phone = phoneText.getText().toString();
        String email = emailText.getText().toString();

        if (rowId > 0) {
            abHelper.updateContact(rowId, firstName, lastName, phone, email);
        } else {
            long id = abHelper.createContact(firstName, lastName, phone, email);
            if (id > 0) {
                rowId = id;
            }
        }
    }
}