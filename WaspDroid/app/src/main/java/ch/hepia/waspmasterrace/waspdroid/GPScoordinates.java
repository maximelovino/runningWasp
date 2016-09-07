package ch.hepia.waspmasterrace.waspdroid;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by maximelovino on 30/08/16.
 */
public class GPScoordinates implements Serializable {
    private double xCoord;
    private double yCoord;
    private final static int EARTH_RADIUS = 6371000;

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

    public double distanceTo(GPScoordinates secondCoordinate){
        double lambda1 =  this.xCoord;
        double phi1 = this.yCoord;

        double lambda2 = secondCoordinate.getXCoord();
        double phi2 = secondCoordinate.getYCoord();

        double alpha = Math.pow(Math.sin((phi1-phi2)/2.0),2) + Math.cos(phi1)*Math.cos(phi2)*Math.pow(Math.sin((lambda1-lambda2)/2.0),2);

        return 2 * EARTH_RADIUS * Math.atan(Math.sqrt(alpha)/(Math.sqrt(1-alpha)));
    }

    public static GPScoordinates getCenter(ArrayList<GPScoordinates> coordinates){
        double sumX = 0;
        double sumY = 0;

        for (GPScoordinates data : coordinates){
            sumX += data.getXCoord();
            sumY += data.getYCoord();
        }

        return new GPScoordinates(sumX/(double)coordinates.size(),sumY/(double)coordinates.size());
    }
}
