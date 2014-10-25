package com.lhs.addressbook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * Show the preferences layout.
 * <p/>
 * Created by wholladay on 10/20/14.
 */
public class PrefFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        setSortBySummary(prefs);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        if ("pref_sortBy".equals(key)) {
            setSortBySummary(sharedPreferences);
        }
    }

    private void setSortBySummary(SharedPreferences sharedPreferences) {

        ListPreference sortPref = (ListPreference) findPreference("pref_sortBy");
        int prefVal = Integer.parseInt(sharedPreferences.getString("pref_sortBy", "0"));
        int summaryId = (prefVal == 0) ? R.string.pref_sort_by_last : R.string.pref_sort_by_first;
        sortPref.setSummary(summaryId);
    }
}