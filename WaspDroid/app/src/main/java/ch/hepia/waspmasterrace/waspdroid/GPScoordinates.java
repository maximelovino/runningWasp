package ch.hepia.waspmasterrace.waspdroid;

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
}
