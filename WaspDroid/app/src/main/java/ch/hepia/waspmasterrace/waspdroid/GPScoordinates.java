package ch.hepia.waspmasterrace.waspdroid;

import com.google.android.gms.maps.model.LatLng;

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

    public LatLng getForMaps(){
        return new LatLng(this.yCoord,this.xCoord);
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p/>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Latitude: "+this.yCoord+", Longitude: "+this.xCoord;
    }
}
