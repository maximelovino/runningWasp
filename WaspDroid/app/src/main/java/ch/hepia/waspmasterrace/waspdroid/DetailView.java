package ch.hepia.waspmasterrace.waspdroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.MalformedURLException;

public class DetailView extends AppCompatActivity {

    //TODO add share action here https://developer.android.com/training/sharing/shareaction.html
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        Run run = (Run) getIntent().getSerializableExtra("RUN");

        TextView txtView = (TextView) findViewById(R.id.detail_view_text);

        txtView.setText(run.toString());

    }
}
