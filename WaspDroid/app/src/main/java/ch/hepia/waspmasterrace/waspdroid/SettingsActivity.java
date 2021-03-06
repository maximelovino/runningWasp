package ch.hepia.waspmasterrace.waspdroid;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import java.util.List;

import ch.hepia.waspmasterrace.waspdroid.data.RunDBHelper;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        addPreferencesFromResource(R.xml.pref_general);
        setupActionBar();
        Preference clearCache = (Preference) findPreference(getString(R.string.pref_clearDB));
        //If we eat clear cache, we call the method to clear the DB, and throw a toast
        clearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /**
             * Called when a Preference has been clicked.
             *
             * @param preference The Preference that was clicked.
             * @return True if the click was handled.
             */
            @Override
            public boolean onPreferenceClick(Preference preference) {
                RunDBHelper helper = new RunDBHelper(context.getApplicationContext());
                helper.clear(helper.getWritableDatabase());
                Toast.makeText(context,"Cleared database",Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
