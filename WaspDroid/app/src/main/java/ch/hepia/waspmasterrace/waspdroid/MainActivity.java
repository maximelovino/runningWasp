package ch.hepia.waspmasterrace.waspdroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String baseUrl = "sampang.internet-box.ch";

        WaspDataBaseConnector dbConnector = null;

        try {
            dbConnector =  new WaspDataBaseConnector(baseUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<Run> runList = new ArrayList<>();


        if (dbConnector!=null){
            try {
                runList = dbConnector.getRuns();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<Run> runAdapter = new ArrayAdapter<Run>(this,R.layout.list_run_item,R.id.list_run_item_text,runList);

        ListView lView = (ListView) findViewById(R.id.listView);

        lView.setAdapter(runAdapter);

    }
}
