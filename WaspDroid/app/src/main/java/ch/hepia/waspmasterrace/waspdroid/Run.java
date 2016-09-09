package ch.hepia.waspmasterrace.waspdroid;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;

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
    private Double maxSpeed;
    private Double distance;
    private Double avgSpeed;
    

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

    public Date getStartDate(){
        return this.startDate.getTime();
    }

    public String getDateAsString(){
        int year = this.startDate.get(Calendar.YEAR);
        String month = this.startDate.getDisplayName(Calendar.MONTH,Calendar.LONG,new Locale("us"));
        int day = this.startDate.get(Calendar.DAY_OF_MONTH);

        return day+" "+month+" "+year;
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
        return new URL("http://"+BASE_URL_WEB+":8080/view.php?runid="+String.valueOf(runID));
    }

    public ArrayList<GPScoordinates> getSortedListOfPoints(){
        ArrayList<GPScoordinates> points = new ArrayList<>();
        System.out.println("DATA for run "+runID+": "+this.runData);
        ArrayList<Integer> keys = new ArrayList<>(this.runData.keySet());

        Collections.sort(keys);
        System.out.println("keys: "+keys);
        for (int i=0;i<keys.size();i++){
            points.add(this.runData.get(keys.get(i)));
            System.out.println("point "+i+" "+this.runData.get(keys.get(i)));
        }

        return points;
    }
    
    public void computeStats(){
        System.out.println("Computing stats");
        double dist = 0;
        double maxDistance = 0;

        ArrayList<GPScoordinates> points = getSortedListOfPoints();


        for (int i = 0;i<points.size()-1;i++){

            GPScoordinates gps1 = points.get(i);
            GPScoordinates gps2 = points.get(i+1);

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

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public Double getDistance() {
        return distance;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }
}
