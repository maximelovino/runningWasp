package ch.hepia.waspmasterrace.waspdroid;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * Created by maximelovino on 30/08/16.
 */
public class Run {

    private int runID;
    private int userID;
    private Date startDate;
    private int timeOfRun;
    private LinkedHashMap<GPScoordinates,Integer> runData;


    public Run(int runID, int userID, Date startDate, int timeOfRun){
        this.runID = runID;
        this.userID = userID;
        this.startDate = startDate;
        this.timeOfRun = timeOfRun;
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

    public String toString(){
        return "Run "+runID+" on "+startDate;
    }


}
