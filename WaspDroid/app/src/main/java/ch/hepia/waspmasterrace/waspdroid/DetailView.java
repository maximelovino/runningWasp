package ch.hepia.waspmasterrace.waspdroid;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Detail activity to display the data of a Run
 */
public class DetailView extends AppCompatActivity implements OnMapReadyCallback {
    private Run run;
    private GoogleMap mMap;
    private final int REQUEST_LOCATION_CODE = 42;
    private static final String TAG = DetailView.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);
        //we unpack the run from the intent
        this.run = (Run) getIntent().getSerializableExtra("RUN");
        //we get the map fragment from the layout
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_detail_view);
        //To set Run XX as the subtitle for the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("Run "+run.getRunID());
        //call to load the map in another thread
        mapFragment.getMapAsync(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //We compute the stats and display it where they should be
        run.computeStats();
        TextView dateTxt = (TextView) findViewById(R.id.run_date_value);
        TextView timeTxt = (TextView) findViewById(R.id.run_time_value);
        TextView distanceTxt = (TextView) findViewById(R.id.run_distance_value);
        TextView avgSpeedTxt = (TextView) findViewById(R.id.run_avgSpeed_value);
        TextView maxSpeedTxt = (TextView) findViewById(R.id.run_maxSpeed_value);
        TextView paceTxt = (TextView) findViewById(R.id.run_pace_value);
        dateTxt.setText(run.getDateAsString());
        timeTxt.setText(String.valueOf(run.getTimeOfRun())+" s");
        DecimalFormat df = new DecimalFormat("#.###");
        distanceTxt.setText(df.format(run.getDistanceInKm())+" km");
        avgSpeedTxt.setText(df.format(run.getAvgSpeedAsKmh())+" km/h");
        maxSpeedTxt.setText(df.format(run.getMaxSpeedAsKmh())+" km/h");
        paceTxt.setText(df.format(run.getPaceInMinKm())+" min/km");
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p/>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p/>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p/>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p/>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu consists of the share button
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_view_menu,menu);

        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p/>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                try {
                    //we launch the shareIntent when clicking on Share
                    startActivity(createShareIntent());
                } catch (MalformedURLException e) {
                    Log.w(TAG,"Couldn't form Share url");
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Create a sharing intent for our web interface
     *
     * @return  The share intent
     * @throws MalformedURLException
     */
    private Intent createShareIntent() throws MalformedURLException {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,run.getURL().toString());
        return intent;

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        //Request for location permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG,"Location permission is already granted");
            mMap.setMyLocationEnabled(true);
        } else {
            Log.v(TAG,"We don't have location permission yet");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
        }

        //If we have points, we build a polyline and we prepare bounds
        ArrayList<DataPoint> points = run.getRunData();
        if (!points.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            PolylineOptions poly = new PolylineOptions();

            for (DataPoint point : points) {
                LatLng mapPoint = point.getPoint().getForMaps();
                poly.add(mapPoint);
                builder.include(mapPoint);
            }
            final LatLngBounds bounds = builder.build();
            mMap.addPolyline(poly);

            // We add a marker to denote the start of the run
            mMap.addMarker(new MarkerOptions().position(points.get(0).getPoint().getForMaps()).title("Start point"));

            // We wait for the map to load before setting the bounds otherwise it may result in a crash
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    //100 margin is arbitrary
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
                    mMap.animateCamera(cu);
                }
            });


        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @SuppressWarnings("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }
    }
}
