package ch.hepia.waspmasterrace.waspdroid;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by maximelovino on 30/08/16.
 */


/**
 * Class that designates a Run with all its data
 * Implements Serializable so it can be passed in an Intent bundle
 */
public class Run implements Serializable{

    private int runID;
    private int userID;
    private Calendar startDate;
    private int timeOfRun;

    private ArrayList<DataPoint> runData;

    private final String BASE_URL_WEB = "sampang.internet-box.ch";
    private Double maxSpeed;
    private Double distance;
    private Double avgSpeed;
    /**
     * Default constructor for Run, uses userID 1
     *
     * @param runID The ID of the run
     * @param startDate The date of the beginning of the run, as a Calendar instance
     * @param timeOfRun The total time of the run, in seconds
     */
    public Run(int runID, Calendar startDate, int timeOfRun){
        this(runID,1,startDate,timeOfRun);
    }

    /**
     * Complete constructor for Run
     *
     * @param runID The ID of the Run
     * @param userID    The userID of the Run
     * @param startDate The date of the beginning of the run, as a Calendar instance
     * @param timeOfRun The total time of th run, in seconds
     */
    public Run(int runID, int userID, Calendar startDate, int timeOfRun){
        this.runID = runID;
        this.userID = userID;
        this.startDate = startDate;
        this.timeOfRun = timeOfRun;
        this.runData = new ArrayList<>();
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

    @Override
    public String toString(){
        return "Run "+runID+" on "+this.getDateAsString();
    }

    public void setRunData(ArrayList<DataPoint> newData){
        this.runData.clear();
        this.runData.addAll(newData);
        Collections.sort(this.runData);
    }

    public ArrayList<DataPoint> getRunData() {
        return runData;
    }

    public URL getURL() throws MalformedURLException {
        return new URL("http://"+BASE_URL_WEB+":8080/view.php?runid="+String.valueOf(runID));
    }

    
    public void computeStats(){
        System.out.println("Computing stats");
        double dist = 0;
        double highSpeed = 0;
        
        for (int i = 0;i<this.runData.size()-1;i++){
            GPScoordinates gps1 = this.runData.get(i).getPoint();
            GPScoordinates gps2 = this.runData.get(i+1).getPoint();

            int time1 = this.runData.get(i).getTime();
            int time2 = this.runData.get(i+1).getTime();

            System.out.println(gps1);
            double tempDist = gps1.distanceTo(gps2);

            double tempSpeed = tempDist / (double) (time2-time1);

            if (tempSpeed>highSpeed)
                highSpeed = tempSpeed;

            dist += tempDist;
            System.out.println("Distance "+tempDist);
        }

        this.distance = dist;
        this.avgSpeed = dist / this.timeOfRun;
        this.maxSpeed = highSpeed;

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

    public int getUserID() {
        return userID;
    }

    private static Double getSpeedInKmh(Double speed){
        return speed / 3.6;
    }

    public Double getAvgSpeedAsKmh(){
        return getSpeedInKmh(this.avgSpeed);
    }

    public Double getMaxSpeedAsKmh(){
        return getSpeedInKmh(this.maxSpeed);
    }

    public Double getDistanceInKm(){
        return this.distance / 1000;
    }
}
