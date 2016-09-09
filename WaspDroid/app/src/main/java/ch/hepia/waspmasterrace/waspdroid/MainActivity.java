package ch.hepia.waspmasterrace.waspdroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<Run> runArrayAdapter;
    private ArrayList<Run> runList;
    public SwipeRefreshLayout swipe2Refresh;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("RUN_LIST",this.runList);
        System.out.println("SAVED LIST"+outState.getSerializable("RUN_LIST"));
        System.out.println("saved data");
        super.onSaveInstanceState(outState);
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
        System.out.println("We're restoring the data");
        this.runList = (ArrayList<Run>) savedInstanceState.getSerializable("RUN_LIST");
    }


    //TODO implement onSaveInstanceState and onRestoreInstanceState to keep runData between runs, so we're protected from server shutdown https://developer.android.com/training/basics/activity-lifecycle/recreating.html
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (this.runList == null){
            this.runList = new ArrayList<>();
        }
        //Dummy runs for when there is no network

//        runList.add(new Run(10, Calendar.getInstance(),2505));
//        runList.add(new Run(30, Calendar.getInstance(),2505));
//        runList.add(new Run(42, Calendar.getInstance(),2505));


        runArrayAdapter = new ArrayAdapter<Run>(this,R.layout.list_run_item,R.id.list_run_item_text,runList);

        ListView lView = (ListView) findViewById(R.id.listView);
        final FloatingActionButton settingsFab = (FloatingActionButton) findViewById(R.id.fab_main);

        settingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(settingsFab.getContext(),SettingsActivity.class);
                startActivity(intent);
            }
        });

        swipe2Refresh = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        swipe2Refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });

        lView.setAdapter(runArrayAdapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Run runToPass=runArrayAdapter.getItem(position);
                System.out.println(runToPass);
                Intent intent = new Intent(runArrayAdapter.getContext(),DetailView.class);
                System.out.println("intent created");
                intent.putExtra("RUN",runToPass);
                System.out.println("runAdded");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateData();
    }

    private void updateData(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String baseUrl = prefs.getString(getString(R.string.pref_key_url),getString(R.string.pref_default_url));
        int portNumber = Integer.valueOf(prefs.getString(getString(R.string.pref_key_port),getString(R.string.pref_default_port)));
        AsyncTask task = new PHPConnector(baseUrl,portNumber,runArrayAdapter,this).execute();

    }

}
