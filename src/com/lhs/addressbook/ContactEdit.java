package com.lhs.addressbook;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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

    /**
     * Activity is being created and needs to be initialized.
     *
     * @param savedInstanceState The bundle that contains the field values, may be null.
     */
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
                if (saveContact()) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    /**
     * Save the current values of the form so that they can be restored later.
     *
     * @param outState The bundle that will receive the form state.
     */
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

    /**
     * Called after the activity has been suspended.
     *
     * @param savedInstanceState The bundle, which contains the state of the form.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        populateFieldsFromBundle(savedInstanceState);
    }

    /**
     * Get the value from an edit text field and save it to the specified bundle.
     *
     * @param outState The bundle that will receive the text value.
     * @param key      The key to use for storing the value in the bundl.
     * @param field    The text field that holds the value to be saved.
     */
    private void saveTextField(Bundle outState, String key, EditText field) {

        String val = field.getText().toString();
        if (val != null) {
            outState.putString(key, val);
        }
    }

    /**
     * Set the value of the single text field from a bundle.
     *
     * @param savedInstanceState The bundle that will be queried for a value.
     * @param key                The key to use in retrieving the value from the bundle.
     * @param field              The edit text field that needs to be set.
     */
    private void restoreTextField(Bundle savedInstanceState, String key, EditText field) {

        String val = savedInstanceState.getString(key);
        if (val != null) {
            field.setText(val);
        }
    }

    /**
     * Initialize the form fields from the values that are currently stored in the database.
     */
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

    /**
     * Initialize all the form fields from the specified bundle.
     *
     * @param savedInstanceState The bundle that contains the field values.
     */
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

    /**
     * Validate and save the contact to the database. The first and last name fields are required to save an entry in
     * the database.
     *
     * @return true if the contact was successfully saved to the DB.
     */
    private boolean saveContact() {
        boolean success = true;
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String phone = phoneText.getText().toString();
        String email = emailText.getText().toString();

        if (firstName.length() < 1) {
            firstNameText.setError(getString(R.string.err_first_name));
            success = false;
        }
        if (lastName.length() < 1) {
            lastNameText.setError(getString(R.string.err_last_name));
            success = false;
        }
        if (success) {
            if (rowId > 0) {
                try {
                    abHelper.updateContact(rowId, firstName, lastName, phone, email);
                } catch (Exception e) {
                    Log.e("AB", e.getMessage(), e);
                    success = false;
                }
            } else {
                try {
                    long id = abHelper.createContact(firstName, lastName, phone, email);
                    if (id > 0) {
                        rowId = id;
                    }
                } catch (Exception e) {
                    Log.e("AB", e.getMessage(), e);
                    success = false;
                }
            }
        }
        return success;
    }
}