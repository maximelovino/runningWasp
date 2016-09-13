package ch.hepia.waspmasterrace.waspdroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Entry point of our program, displays the list of runs
 */
public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<Run> runArrayAdapter;
    private ArrayList<Run> runList;
    public SwipeRefreshLayout swipe2Refresh;
    private static final String TAG = MainActivity.class.getName();


    /**
     * Saves state of variables when activity is stopped
     *
     * @param outState  The bundle, where we put the data
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("RUN_LIST",this.runList);
        super.onSaveInstanceState(outState);
        Log.v(TAG,"Data saved");
    }


    /**
     * This method is called after {@link #onStart} when the activity is
     * being re-initialized from a previously saved state, given here in
     * <var>savedInstanceState</var>.  Most implementations will simply use {@link #onCreate}
     * to restore their state, but it is sometimes convenient to do it here
     * after all of the initialization has been done or to allow subclasses to
     * decide whether to use your default implementation.  The default
     * implementation of this method performs a restore of any view state that
     * had previously been frozen by {@link #onSaveInstanceState}.
     * <p/>
     * <p>This method is called between {@link #onStart} and
     * {@link #onPostCreate}.
     *
     * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
     * @see #onCreate
     * @see #onPostCreate
     * @see #onResume
     * @see #onSaveInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.runList = (ArrayList<Run>) savedInstanceState.getSerializable("RUN_LIST");
        Log.v(TAG,"Data restored");
    }

    //TODO check if we should implement a recycler view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //If the runList is not restored, we have to initialize it
        if (this.runList == null) {
            this.runList = new ArrayList<>();
            Log.w(TAG,"Run list had to be initialized");
        }
        //FAB code and listener for the settings button
        final FloatingActionButton settingsFab = (FloatingActionButton) findViewById(R.id.fab_main);

        settingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(settingsFab.getContext(),SettingsActivity.class);
                startActivity(intent);
            }
        });

        //Swipe to refresh listener to update data
        swipe2Refresh = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        swipe2Refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });

        //Setting the adapter and list view to display the runs
        runArrayAdapter = new ArrayAdapter<>(this,R.layout.list_run_item,R.id.list_run_item_text,runList);
        ListView lView = (ListView) findViewById(R.id.listView);
        lView.setAdapter(runArrayAdapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Run runToPass=runArrayAdapter.getItem(position);
                //We pass the run object in the intent, so it's available to the detail activity
                Intent intent = new Intent(runArrayAdapter.getContext(),DetailView.class);
                intent.putExtra("RUN",runToPass);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateData();
    }

    /**
     * Method to update the data from the server, launches update via AsyncTask
     */
    private void updateData(){
        //We get the server address and port from preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String baseUrl = prefs.getString(getString(R.string.pref_key_url),getString(R.string.pref_default_url));
        int portNumber = Integer.valueOf(prefs.getString(getString(R.string.pref_key_port),getString(R.string.pref_default_port)));
        //We create an asyncTask to take care of the network task
        AsyncTask task = new PHPConnector(baseUrl,portNumber,runArrayAdapter,this).execute();
    }

}
