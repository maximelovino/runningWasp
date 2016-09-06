package ch.hepia.waspmasterrace.waspdroid;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by maximelovino on 01/09/16.
 */
public class PHPConnector extends AsyncTask<Void,Void,ArrayList<Run>> {

    private String baseURL;
    private int portNumber;
    private String serverPath;
    private ArrayAdapter<Run> runAdapter;
    private MainActivity activity;

    public PHPConnector(String baseURL, ArrayAdapter<Run> runAdapter, MainActivity activity){
        this(baseURL,8080,runAdapter, activity);
    }

    public PHPConnector(String baseURL,int portNumber, ArrayAdapter<Run> runAdapter, MainActivity activity){
        this.baseURL = baseURL;
        this.portNumber = portNumber;
        this.runAdapter = runAdapter;
        this.activity = activity;
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
        System.out.println("Connection opened with runList, URL:"+this.serverPath);

        BufferedReader inStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


        String inputLine;
        while ((inputLine = inStream.readLine())!=null){
            String[] lineArray = inputLine.split(";");
            System.out.println("line: "+inputLine);
            if (inputLine.equals(""))
                continue;

            int runID = Integer.valueOf(lineArray[0]);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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



    private LinkedHashMap<Integer,GPScoordinates> getRunData(int runID) throws IOException {
        //x;y;Count
        LinkedHashMap<Integer,GPScoordinates> runData = new LinkedHashMap<>();


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

            int time = (Integer.valueOf(lineArray[2])-1)*5;

            runData.put(time,gps);
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
        }else{
            System.out.println("hello from null world");
            Toast.makeText(activity,"Couldn't sync data",Toast.LENGTH_LONG).show();
            System.out.println("hello from after toast");
        }
        if (activity.swipe2Refresh.isRefreshing())
            activity.swipe2Refresh.setRefreshing(false);
    }
}
