package ch.hepia.waspmasterrace.waspdroid;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ch.hepia.waspmasterrace.waspdroid.data.RunDBContract.*;
import ch.hepia.waspmasterrace.waspdroid.data.RunDBHelper;

/**
 * Class to work with our php server hook
 */
public class PHPConnector extends AsyncTask<Void,Void,ArrayList<Run>> {

    private String baseURL;
    private int portNumber;
    private String serverPath;
    private ArrayAdapter<Run> runAdapter;
    private MainActivity activity;
    private SQLiteDatabase db;
    private RunDBHelper helper;
    private static final String TAG = PHPConnector.class.getName();

    /**
     * Default constructor for our connector
     *
     * @param baseURL   The url of the server
     * @param portNumber    The port to use
     * @param runAdapter    The adapter to populate
     * @param activity  The calling activity
     * @param db    The database to update
     * @param helper    The database helper instance
     */
    public PHPConnector(String baseURL,int portNumber, ArrayAdapter<Run> runAdapter, MainActivity activity, SQLiteDatabase db, RunDBHelper helper){
        this.baseURL = baseURL;
        this.portNumber = portNumber;
        this.runAdapter = runAdapter;
        this.activity = activity;
        this.db = db;
        this.helper = helper;
        buildServerPath();
    }

    /**
     *
     * @return  The full server path (url+port)
     */
    public String getServerPath(){
        return this.serverPath;
    }

    /**
     * Builds the server path
     */
    private void buildServerPath(){
        this.serverPath = this.baseURL +":"+ this.portNumber;
    }

    /**
     *
     * @return  An arraylist of Runs objects, fetched from the server
     * @throws IOException
     * @throws ParseException
     */
    private ArrayList<Run> getRunList() throws IOException, ParseException {
        //runID;DATE;Time

        ArrayList<Run> runList = new ArrayList<>();
        URL url = new URL("http://"+this.serverPath+"/android.php?uid=1&listrun");


        URLConnection urlConnection = url.openConnection();
        Log.v(TAG,"Connection opened with runList, URL:"+url);

        BufferedReader inStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


        String inputLine;
        while ((inputLine = inStream.readLine())!=null){
            String[] lineArray = inputLine.split(";");
            Log.v(TAG,"line: "+inputLine);
            //If line empty, we jump
            if (inputLine.equals(""))
                continue;

            int runID = Integer.valueOf(lineArray[0]);

            //HH for 24hrs time, hh for 12hrs
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar date = Calendar.getInstance();
            date.setTime(df.parse(lineArray[1]));
            int timeOfRun = Integer.valueOf(lineArray[2]);
            Run run = new Run(runID,date,timeOfRun);
            run.setRunData(getRunData(run.getRunID()));
            runList.add(run);
        }

        inStream.close();

        return runList;
    }

    /**
     *
     * @param runID The id of the run we want the data for
     * @return  The datapoints for a run
     * @throws IOException
     */
    private ArrayList<DataPoint> getRunData(int runID) throws IOException {

        ArrayList<DataPoint> runData = new ArrayList<>();


        URL url = new URL("http://"+this.serverPath+"/android.php?uid=1&rundata&idRun="+runID);

        URLConnection urlConnection = url.openConnection();

        BufferedReader inStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        String inputLine;

        while ((inputLine = inStream.readLine()) != null){
            if (inputLine.equals(""))
                continue;

            String[] lineArray = inputLine.split(";");
            double x = Double.valueOf(lineArray[0]);
            double y = Double.valueOf(lineArray[1]);

            GPScoordinates gps = new GPScoordinates(x,y);

            int count = Integer.valueOf(lineArray[2]);
            int time = Integer.valueOf(lineArray[3]);

            DataPoint point = new DataPoint(gps,count,time);
            runData.add(point);
        }
        inStream.close();

        return runData;
    }


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected ArrayList<Run> doInBackground(Void... params) {
        try {
            Log.v(TAG,"syncing");
            return getRunList();
        } catch (Exception e) {
            Log.w(TAG,"there was a problem with syncing");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param runs The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(ArrayList<Run> runs) {
        super.onPostExecute(runs);
        Log.v(TAG,"syncing bg task done");
        if (runs!=null){
            Log.v(TAG,"we have data");
            runAdapter.clear();
            runAdapter.addAll(runs);
            updateDB(runs);
        }else{
            Toast.makeText(activity,"Couldn't sync data",Toast.LENGTH_LONG).show();
            Log.w(TAG,"sync wasn't possible");
        }
        if (activity.swipe2Refresh.isRefreshing())
            activity.swipe2Refresh.setRefreshing(false);
    }

    /**
     * We update the database, using a diff, we insert only elements that aren't already present
     *
     * @param runs  The list of all runs
     */
    private void updateDB(ArrayList<Run> runs){
        for (Run run : runs) {
            int runID = run.getRunID();
            int userID = run.getUserID();
            int seconds = run.getTimeOfRun();
            String date = run.getDateForDB();
            ArrayList<DataPoint> points = run.getRunData();

            String query = "select count(*) from "+RunListEntry.TABLE_NAME+" where "+RunListEntry.COLUMN_RUNID+"="+runID;

            Cursor tempCursor = this.db.rawQuery(query,null);
            int elements = 1;

            if (tempCursor.moveToFirst()) {
                elements = tempCursor.getInt(0);
            }

            if (elements==0){
                Log.v(TAG,"Inserting an element in db, run "+runID);
                ContentValues runLine = new ContentValues();
                runLine.put(RunListEntry.COLUMN_RUNID,runID);
                runLine.put(RunListEntry.COLUMN_DATE,date);
                runLine.put(RunListEntry.COLUMN_SECONDS,seconds);
                runLine.put(RunListEntry.COLUMN_USERID,userID);
                this.db.insert(RunListEntry.TABLE_NAME,null,runLine);

                for (DataPoint point : points) {
                    GPScoordinates gps = point.getPoint();
                    double x = gps.getXCoord();
                    double y = gps.getYCoord();
                    int count = point.getCount();
                    int time = point.getTime();

                    ContentValues pointLine = new ContentValues();
                    pointLine.put(RunDataEntry.COLUMN_RUNID,runID);
                    pointLine.put(RunDataEntry.COLUMN_X,x);
                    pointLine.put(RunDataEntry.COLUMN_Y,y);
                    pointLine.put(RunDataEntry.COLUMN_COUNT,count);
                    pointLine.put(RunDataEntry.COLUMN_TIME,time);

                    this.db.insert(RunDataEntry.TABLE_NAME,null,pointLine);
                }

            }

        }
    }
}
