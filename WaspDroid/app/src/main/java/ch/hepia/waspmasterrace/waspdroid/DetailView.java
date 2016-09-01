package ch.hepia.waspmasterrace.waspdroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DetailView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);


        String runString = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        TextView txtView = (TextView) findViewById(R.id.detail_view_text);

        txtView.setText(runString);

    }
}
