package ch.hepia.waspmasterrace.waspdroid;

import android.util.Log;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

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
    private Double pace;
    private static final String TAG = Run.class.getName();

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

    /**
     *
     * @return  The runID
     */
    public int getRunID(){
        return this.runID;
    }

    /**
     *
     * @return  The time length of the run
     */
    public int getTimeOfRun(){
        return timeOfRun;
    }

    /**
     *
     * @return  The date of the run as an instance of Date
     */
    public Date getStartDate(){
        return this.startDate.getTime();
    }

    /**
     *
     * @return  The date in a string format
     */
    public String getDateAsString(){
        int year = this.startDate.get(Calendar.YEAR);
        String month = this.startDate.getDisplayName(Calendar.MONTH,Calendar.LONG,new Locale("us"));
        int day = this.startDate.get(Calendar.DAY_OF_MONTH);

        return day+" "+month+" "+year;
    }

    /**
     *
     * @return The string format for a run, run XX on XX.XX.XX
     */
    @Override
    public String toString(){
        return "Run "+runID+" on "+this.getDateAsString();
    }

    /**
     *
     * @param newData   The list of datapoints for the run we want to set
     */
    public void setRunData(ArrayList<DataPoint> newData){
        this.runData.clear();
        this.runData.addAll(newData);
        Collections.sort(this.runData);
    }

    /**
     *
     * @return  The list of datapoints of the run
     */
    public ArrayList<DataPoint> getRunData() {
        return runData;
    }

    /**
     *
     * @return  The url of the run on our web interface
     * @throws MalformedURLException
     */
    public URL getURL() throws MalformedURLException {
        return new URL("http://"+BASE_URL_WEB+":8080/view.php?runid="+String.valueOf(runID));
    }

    /**
     * Method to calculate all the stats and populate instance fields
     */
    public void computeStats(){
        Log.v(TAG,"Computing stats");
        double dist = 0;
        double highSpeed = 0;
        
        for (int i = 0;i<this.runData.size()-1;i++){
            GPScoordinates gps1 = this.runData.get(i).getPoint();
            GPScoordinates gps2 = this.runData.get(i+1).getPoint();

            int time1 = this.runData.get(i).getTime();
            int time2 = this.runData.get(i+1).getTime();

            double tempDist = gps1.distanceTo(gps2);

            double tempSpeed = tempDist / (double) (time2-time1);

            if (tempSpeed>highSpeed)
                highSpeed = tempSpeed;

            dist += tempDist;
        }

        this.distance = dist;
        this.avgSpeed = dist / this.timeOfRun;
        this.maxSpeed = highSpeed;
        this.pace = this.timeOfRun / this.distance;
    }

    /**
     *
     * @return  The maximum speed of the run in m/s
     */
    public Double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     *
     * @return  The distance of the run in m
     */
    public Double getDistance() {
        return distance;
    }

    /**
     *
     * @return  The average speed in m/s
     */
    public Double getAvgSpeed() {
        return avgSpeed;
    }

    /**
     *
     * @return  The user id of the runner
     */
    public int getUserID() {
        return userID;
    }

    /**
     *
     * @param speed The speed we want to convert in m/s
     * @return  The speed, but in km/h
     */
    private static Double getSpeedInKmh(Double speed){
        return speed * 3.6;
    }

    /**
     *
     * @return  The average speed in km/h
     */
    public Double getAvgSpeedAsKmh(){
        return getSpeedInKmh(this.avgSpeed);
    }

    /**
     *
     * @return  The maximum speed of the run in km/h
     */
    public Double getMaxSpeedAsKmh(){
        return getSpeedInKmh(this.maxSpeed);
    }

    /**
     *
     * @return  The distance of the run in km
     */
    public Double getDistanceInKm(){
        return this.distance / 1000;
    }

    /**
     *
     * @return The pace in min/km
     */
    public Double getPaceInMinKm(){
        return this.pace / 60 * 1000;
    }

    /**
     *
     * @return  The date in database friendly format
     */
    public String getDateForDB() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(this.getStartDate());
    }

    /**
     *
     * @return The pace in s/m
     */
    public Double getPace() {
        return pace;
    }
}
