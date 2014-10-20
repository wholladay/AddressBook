package com.lhs.addressbook;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by wholladay on 10/20/14.
 */
public class PrefFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}