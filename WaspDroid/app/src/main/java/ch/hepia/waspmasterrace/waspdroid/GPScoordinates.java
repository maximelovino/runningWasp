package ch.hepia.waspmasterrace.waspdroid;

import java.util.ArrayList;

/**
 * Created by maximelovino on 30/08/16.
 */
public class GPScoordinates {
    private double xCoord;
    private double yCoord;

    public GPScoordinates(double xCoord, double yCoord){
        this.xCoord=xCoord;
        this.yCoord=yCoord;
    }

    public double getXCoord(){
        return this.xCoord;
    }

    public double getYCoord(){
        return this.yCoord;
    }

    //TODO get math formula for this
    public Double distanceTo(GPScoordinates secondCoordinate){
        return null;
    }

    //TODO add this formula
    public static GPScoordinates getCenter(ArrayList<GPScoordinates> coordinates){

        return null;
    }
}
