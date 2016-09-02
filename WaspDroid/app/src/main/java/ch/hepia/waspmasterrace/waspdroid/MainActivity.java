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

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<Run> runArrayAdapter;
    private ArrayList<Run> runList = new ArrayList<>();
    public SwipeRefreshLayout swipe2Refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                String runString = runArrayAdapter.getItem(position).toString();
                System.out.println(runString);
                Intent intent = new Intent(runArrayAdapter.getContext(),DetailView.class).putExtra(Intent.EXTRA_TEXT,runString);
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

    //TODO add refresh manually method, and put all views and variable as instance variables
    //TODO at the end of the method, signal that the refresh is done using swipe2Refresh.setRefreshing(false)
}
