package com.lerenard.catchphrase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.Locale;

/**
 * Created by mc on 13-Jan-17.
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setSecondsInRound();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                             .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                             .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.seconds_in_round_key))) {
            setSecondsInRound();
        }
    }

    private void setSecondsInRound() {
        String key = getString(R.string.seconds_in_round_key);
        Preference secondsInRoundPreference = findPreference(key);
        secondsInRoundPreference.setTitle(
                String.format(Locale.getDefault(), getString(R.string.seconds_in_round_title_formatted),
                              getPreferenceScreen().getSharedPreferences().getString(key, getString(
                                      R.string.seconds_in_round_default_value))));
    }
}
