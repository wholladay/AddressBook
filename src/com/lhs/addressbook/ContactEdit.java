package com.lhs.addressbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import static com.lhs.addressbook.AddressBookDbAdapter.*;

/**
 * Created by wholladay on 10/17/14.
 */
public class ContactEdit extends Activity {

    private Long rowId;
    private EditText firstNameText;
    private EditText lastNameText;
    private EditText phoneText;
    private EditText emailText;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_edit);
        setTitle(R.string.edit_contact);
        firstNameText = (EditText) findViewById(R.id.first_name);
        lastNameText = (EditText) findViewById(R.id.last_name);
        phoneText = (EditText) findViewById(R.id.phone);
        emailText = (EditText) findViewById(R.id.email);

        rowId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            rowId = extras.getLong(KEY_ROWID);
            String firstName = extras.getString(KEY_FIRST_NAME);
            String lastName = extras.getString(KEY_LAST_NAME);
            String phone = extras.getString(KEY_PHONE);
            String email = extras.getString(KEY_EMAIL);

            if (firstName != null) {
                firstNameText.setText(firstName);
            }
            if (lastName != null) {
                lastNameText.setText(lastName);
            }
            if (phone != null) {
                phoneText.setText(phone);
            }
            if (email != null) {
                emailText.setText(email);
            }
        }

        Button confirmButton = (Button) findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Bundle bundle = new Bundle();

                bundle.putString(KEY_FIRST_NAME, firstNameText.getText().toString());
                bundle.putString(KEY_LAST_NAME, lastNameText.getText().toString());
                bundle.putString(KEY_PHONE, phoneText.getText().toString());
                bundle.putString(KEY_EMAIL, emailText.getText().toString());
                if (rowId != null) {
                    bundle.putLong(KEY_ROWID, rowId);
                }
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}