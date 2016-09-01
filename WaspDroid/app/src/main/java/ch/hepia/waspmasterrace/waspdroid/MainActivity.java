package ch.hepia.waspmasterrace.waspdroid;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String baseUrl = "sampang.internet-box.ch";
        baseUrl = "192.168.160.247";

        PHPConnector connector = new PHPConnector(baseUrl);


        ArrayList<Run> runList;

        try {
            runList = connector.getRunList();
        } catch (Exception e) {
            runList = new ArrayList<>();
            e.printStackTrace();
        }

        ArrayAdapter<Run> runAdapter = new ArrayAdapter<Run>(this,R.layout.list_run_item,R.id.list_run_item_text,runList);

        ListView lView = (ListView) findViewById(R.id.listView);

        lView.setAdapter(runAdapter);

    }
}
