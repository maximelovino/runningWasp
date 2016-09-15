package ch.hepia.waspmasterrace.waspdroid;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
 * Created by maximelovino on 01/09/16.
 */
public class PHPConnector extends AsyncTask<Void,Void,ArrayList<Run>> {

    private String baseURL;
    private int portNumber;
    private String serverPath;
    private ArrayAdapter<Run> runAdapter;
    private MainActivity activity;
    private SQLiteDatabase db;
    private RunDBHelper helper;

    public PHPConnector(String baseURL,int portNumber, ArrayAdapter<Run> runAdapter, MainActivity activity, SQLiteDatabase db, RunDBHelper helper){
        this.baseURL = baseURL;
        this.portNumber = portNumber;
        this.runAdapter = runAdapter;
        this.activity = activity;
        this.db = db;
        this.helper = helper;
        buildServerPath();
    }

    public String getServerPath(){
        return this.serverPath;
    }

    private void buildServerPath(){
        this.serverPath = this.baseURL +":"+ this.portNumber;
    }

    private ArrayList<Run> getRunList() throws IOException, ParseException {
        //runID;DATE

        ArrayList<Run> runList = new ArrayList<>();
        URL url = new URL("http://"+this.serverPath+"/android.php?uid=1&listrun");


        URLConnection urlConnection = url.openConnection();
        System.out.println("Connection opened with runList, URL:"+url);

        BufferedReader inStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


        String inputLine;
        while ((inputLine = inStream.readLine())!=null){
            String[] lineArray = inputLine.split(";");
            System.out.println("line: "+inputLine);
            if (inputLine.equals(""))
                continue;

            int runID = Integer.valueOf(lineArray[0]);

            //HH for 24hrs time, hh for 12hrs
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar date = Calendar.getInstance();
            date.setTime(df.parse(lineArray[1]));
            System.out.println("DATE: "+date.getTime());

            int timeOfRun = Integer.valueOf(lineArray[2]);

            Run run = new Run(runID,date,timeOfRun);
            run.setRunData(getRunData(run.getRunID()));
            runList.add(run);
        }

        inStream.close();

        return runList;
    }



    private ArrayList<DataPoint> getRunData(int runID) throws IOException {

        ArrayList<DataPoint> runData = new ArrayList<>();


        URL url = new URL("http://"+this.serverPath+"/android.php?uid=1&rundata&idRun="+runID);

        URLConnection urlConnection = url.openConnection();
        System.out.println("Connection opened");

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
            System.out.println("syncing");
            return getRunList();
        } catch (Exception e) {
            System.out.println("fail");


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
        System.out.println("finished sync");
        if (runs!=null){
            System.out.println("we've got data");
            runAdapter.clear();
            runAdapter.addAll(runs);
            updateDB(runs);
        }else{
            System.out.println("hello from null world");
            Toast.makeText(activity,"Couldn't sync data",Toast.LENGTH_LONG).show();
            System.out.println("hello from after toast");
        }
        if (activity.swipe2Refresh.isRefreshing())
            activity.swipe2Refresh.setRefreshing(false);
    }

    private void updateDB(ArrayList<Run> runs){
        this.db.execSQL("DROP TABLE IF EXISTS "+ RunListEntry.TABLE_NAME);
        this.db.execSQL("DROP TABLE IF EXISTS "+ RunDataEntry.TABLE_NAME);
        helper.onCreate(this.db);

        for (Run run : runs) {
            int runID = run.getRunID();
            int userID = run.getUserID();
            int seconds = run.getTimeOfRun();
            String date = run.getDateForDB();
            ArrayList<DataPoint> points = run.getRunData();

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
