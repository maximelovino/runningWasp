package ch.hepia.waspmasterrace.waspdroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ch.hepia.waspmasterrace.waspdroid.data.RunDBContract.*;
import ch.hepia.waspmasterrace.waspdroid.data.RunDBHelper;

/**
 * Entry point of our program, displays the list of runs
 */
public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<Run> runArrayAdapter;
    public SwipeRefreshLayout swipe2Refresh;
    private static final String TAG = MainActivity.class.getName();
    RunDBHelper dbHelper;

    //TODO check if we should implement a recycler view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setting the adapter and list view to display the runs
        runArrayAdapter = new ArrayAdapter<>(this,R.layout.list_run_item,R.id.list_run_item_text,new ArrayList<Run>());
        dbHelper = new RunDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        updateDataFromSQLite(db);

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
                updateDataFromServer();
            }
        });


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
        updateDataFromServer();
    }

    /**
     * Method to update the data from the server, launches update via AsyncTask
     */
    private void updateDataFromServer(){
        //We get the server address and port from preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String baseUrl = prefs.getString(getString(R.string.pref_key_url),getString(R.string.pref_default_url));
        int portNumber = Integer.valueOf(prefs.getString(getString(R.string.pref_key_port),getString(R.string.pref_default_port)));
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //We create an asyncTask to take care of the network task
        AsyncTask task = new PHPConnector(baseUrl,portNumber,runArrayAdapter,this,db,dbHelper).execute();
    }

    private void updateDataFromSQLite(SQLiteDatabase db){
        Cursor runListCursor = db.query(RunListEntry.TABLE_NAME,null,null,null,null,null,null);

        if (runListCursor.moveToFirst()){
            do {
                int runID = runListCursor.getInt(runListCursor.getColumnIndex(RunListEntry.COLUMN_RUNID));
                int userID = runListCursor.getInt(runListCursor.getColumnIndex(RunListEntry.COLUMN_USERID));
                String date = runListCursor.getString(runListCursor.getColumnIndex(RunListEntry.COLUMN_DATE));
                int seconds = runListCursor.getInt(runListCursor.getColumnIndex(RunListEntry.COLUMN_SECONDS));
                //HH for 24hrs time, hh for 12hrs
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calDate = Calendar.getInstance();
                try {
                    calDate.setTime(df.parse(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Run run = new Run(runID,userID,calDate,seconds);
                ArrayList<DataPoint> data = new ArrayList<>();

                Cursor runDataCursor = db.query(RunDataEntry.TABLE_NAME,null,RunDataEntry.COLUMN_RUNID+"=?",new String[]{String.valueOf(runID)},null,null,null);

                if (runDataCursor.moveToFirst()){
                    do {
                        double x = runDataCursor.getDouble(runDataCursor.getColumnIndex(RunDataEntry.COLUMN_X));
                        double y = runDataCursor.getDouble(runDataCursor.getColumnIndex(RunDataEntry.COLUMN_Y));
                        int count = runDataCursor.getInt(runDataCursor.getColumnIndex(RunDataEntry.COLUMN_COUNT));
                        int time = runDataCursor.getInt(runDataCursor.getColumnIndex(RunDataEntry.COLUMN_TIME));

                        DataPoint point = new DataPoint(new GPScoordinates(x,y),count,time);
                        data.add(point);
                    }while (runDataCursor.moveToNext());
                }

                run.setRunData(data);
                this.runArrayAdapter.add(run);
            }while (runListCursor.moveToNext());
        }
    }
}
