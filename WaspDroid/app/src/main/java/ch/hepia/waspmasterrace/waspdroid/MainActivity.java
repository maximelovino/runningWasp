package ch.hepia.waspmasterrace.waspdroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> test = new ArrayList<>();

        for (int i=1;i<101;i++){
            test.add("hello "+i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_run_item,R.id.list_run_item_text,test);

        ListView lView = (ListView) findViewById(R.id.listView);

        lView.setAdapter(adapter);

    }
}
