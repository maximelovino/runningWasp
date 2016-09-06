package ch.hepia.waspmasterrace.waspdroid;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Created by maximelovino on 30/08/16.
 */
public class Run implements Serializable{

    private int runID;
    private int userID;
    private Calendar startDate;
    private int timeOfRun;
    private LinkedHashMap<Integer,GPScoordinates> runData;
    private final String BASE_URL_WEB = "sampang.internet-box.ch";
    private double maxSpeed;
    private double distance;
    private double avgSpeed;
    

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
        runData.put(seconds,coord);
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

    public LinkedHashMap<Integer,GPScoordinates> getRunData(){
        return new LinkedHashMap<>(this.runData);
    }

    @Override
    public String toString(){
        return "Run "+runID;
    }

    public void setRunData(LinkedHashMap<Integer,GPScoordinates> newData){
        this.runData.clear();
        this.runData.putAll(newData);
    }

    public URL getURL() throws MalformedURLException {
        return new URL("http://"+BASE_URL_WEB+":8080/view.php?&runid="+String.valueOf(runID));
    }
    
    public void computeStats(){
        System.out.println("Computing stats");
        double dist = 0;
        double maxDistance = 0;

        ArrayList<Integer> keys = new ArrayList(this.runData.keySet());

        Collections.sort(keys);

        for (int i = 0;i<keys.size()-1;i++){
            int key1 = keys.get(i);
            int key2 = keys.get(i+1);

            GPScoordinates gps1 = this.runData.get(key1);
            GPScoordinates gps2 = this.runData.get(key2);

            double tempDist = gps1.distanceTo(gps2);

            if (tempDist>maxDistance){
                maxDistance = tempDist;
            }

            dist += tempDist;
        }

        this.distance = dist;
        this.avgSpeed = dist / this.timeOfRun;
        this.maxSpeed = maxDistance / 5.0;
    }

}
