package ch.hepia.waspmasterrace.waspdroid;

import java.util.Calendar;
import java.util.LinkedHashMap;

/**
 * Created by maximelovino on 30/08/16.
 */
public class Run{

    private int runID;
    private int userID;
    private Calendar startDate;
    private int timeOfRun;
    private LinkedHashMap<GPScoordinates,Integer> runData;

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

}
