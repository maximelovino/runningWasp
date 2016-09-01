package ch.hepia.waspmasterrace.waspdroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String baseUrl = "sampang.internet-box.ch";
        //baseUrl = "192.168.160.247";

        AsyncTask task = new PHPConnector(baseUrl).execute();

        ArrayList<Run> runList = null;
        try {
            runList = (ArrayList<Run>) task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        final ArrayAdapter<Run> runAdapter = new ArrayAdapter<Run>(this,R.layout.list_run_item,R.id.list_run_item_text,runList);

        ListView lView = (ListView) findViewById(R.id.listView);
        final FloatingActionButton settingsFab = (FloatingActionButton) findViewById(R.id.fab_main);

        settingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(settingsFab.getContext(),SettingsActivity.class);
                startActivity(intent);
            }
        });

        lView.setAdapter(runAdapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String runString = runAdapter.getItem(position).toString();
                System.out.println(runString);

                Intent intent = new Intent(runAdapter.getContext(),DetailView.class).putExtra(Intent.EXTRA_TEXT,runString);
                startActivity(intent);
            }
        });

    }
}
