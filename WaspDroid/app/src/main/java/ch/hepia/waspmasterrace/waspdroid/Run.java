package ch.hepia.waspmasterrace.waspdroid;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by maximelovino on 30/08/16.
 */
public class Run {

    private int runID;
    private int userID;
    private Date startDate;
    private int timeOfRun;
    private HashMap<GPScoordinates,int> runData;


    public Run(int runID, int userID, Date startDate, int timeOfRun){
        this.runID=runID;
        this.userID=userID;
        this.startDate=startDate;
        this.timeOfRun=timeOfRun;
    }

    private void addPointOfRun(GPScoordinates coord, int seconds){
        runData.put(coord, seconds);
    }

    public void getRunDataFromDB(){
        
    }






}
