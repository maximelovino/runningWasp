package ch.hepia.waspmasterrace.waspdroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by maximelovino on 01/09/16.
 */
public class PHPConnector {

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

    public String getServerPath(){
        return this.serverPath;
    }

    private void buildServerPath(){
        this.serverPath = this.baseURL +":"+ this.portNumber;
    }

    public ArrayList<Run> getRuns(){
        ArrayList<Run> runs = new ArrayList<>();


        return runs;
    }

    private ArrayList<Run> getRunList(){
        //runID;DATE

        ArrayList<Run> runList = new ArrayList<>();
        URL url = null;

        try{
            url = new URL("http://"+this.serverPath+"/android.php?uid=1&listrun");

            if (url!=null){
                URLConnection urlConnection = null;

                urlConnection = url.openConnection();

                BufferedReader inStream = null;

                inStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                String inputLine;
                if (inStream!=null){
                    while ((inputLine = inStream.readLine())!=null){
                        String[] lineArray = inputLine.split(";");
                        int runID = Integer.valueOf(lineArray[0]);
                        Date date = getDateFromString(lineArray[1]);
                    }
                }
                inStream.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Date getDateFromString(String stringDate){
        int year = Integer.valueOf(stringDate.substring(0,5));
        int month = Integer.valueOf(stringDate.substring(6,8));
        int day = Integer.valueOf(stringDate.substring(9,11));

        int hours = Integer.valueOf(stringDate.substring(12,14));
        int minutes = Integer.valueOf(stringDate.substring(15,17));
        int seconds = Integer.valueOf(stringDate.substring(18,20));

        Date date = new Date(year,month,day,hours,minutes,seconds);
        return date;
    }



    private String[][] getRunData(int runID){
        //x;y;Count
    }


}
