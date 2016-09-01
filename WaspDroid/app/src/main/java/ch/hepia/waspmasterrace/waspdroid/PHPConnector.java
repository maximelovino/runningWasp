package ch.hepia.waspmasterrace.waspdroid;

import android.os.AsyncTask;
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

    public PHPConnector(String baseURL){
        this(baseURL,8080);
    }

    public PHPConnector(String baseURL,int portNumber){
        this.baseURL = baseURL;
        this.portNumber = portNumber;
        buildServerPath();
    }

    private String getServerPath(){
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

        BufferedReader inStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


        String inputLine;
        while ((inputLine = inStream.readLine())!=null){
            String[] lineArray = inputLine.split(";");
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



    private LinkedHashMap<GPScoordinates,Integer> getRunData(int runID) throws IOException {
        //x;y;Count
        LinkedHashMap<GPScoordinates,Integer> runData = new LinkedHashMap<>();


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

            int time = (Integer.valueOf(lineArray[2])-1)*5;

            runData.put(gps,time);
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
            return getRunList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Run>();
        }
    }
}
