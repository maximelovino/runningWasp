package ch.hepia.waspmasterrace.waspdroid;

import android.net.Uri;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.LinkedHashMap;

/**
 * Created by maximelovino on 30/08/16.
 */
public class Run implements Serializable{

    private int runID;
    private int userID;
    private Calendar startDate;
    private int timeOfRun;
    private LinkedHashMap<GPScoordinates,Integer> runData;
    private final String BASE_URL_WEB = "sampang.internet-box.ch";

    public Run(int runID, Calendar startDate, int timeOfRun){
        this(runID,1,startDate,timeOfRun);
    }

    public Run(int runID, int userID, Calendar startDate, int timeOfRun){
        this.runID = runID;
        this.userID = userID;
        this.startDate = startDate;
        this.timeOfRun = timeOfRun;
        this.runData = new LinkedHashMap<>();
    }


    public void addPointOfRun(GPScoordinates coord, int seconds){
        runData.put(coord, seconds);
    }

    public int getRunID(){
        return this.runID;
    }

    public int getTimeOfRun(){
        return timeOfRun;
    }

    public Calendar getStartDate(){
        return this.startDate;
    }

    public LinkedHashMap<GPScoordinates,Integer> getRunData(){
        return new LinkedHashMap<>(this.runData);
    }

    @Override
    public String toString(){
        return "Run "+runID;
    }

    public void setRunData(LinkedHashMap<GPScoordinates,Integer> newData){
        this.runData.clear();
        this.runData.putAll(newData);
    }

    public URL getURL() throws MalformedURLException {
        return new URL("http://"+BASE_URL_WEB+":8080/view.php?&runid="+String.valueOf(runID));
    }

}
