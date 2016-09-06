package ch.hepia.waspmasterrace.waspdroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailView extends AppCompatActivity {
    private Run run;

    //TODO add share action here https://developer.android.com/training/sharing/shareaction.html
    //TODO menu can be invoked only on fragment...so use FAB or create fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        System.out.println("we're in detail view");
        this.run = (Run) getIntent().getSerializableExtra("RUN");

        TextView txtView = (TextView) findViewById(R.id.detail_view_text);

        txtView.setText(run.toString());
        run.computeStats();

    }
}
