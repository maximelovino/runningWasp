package ch.hepia.waspmasterrace.waspdroid;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Run implements Parcelable {

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

    public static final Creator<Run> CREATOR = new Creator<Run>() {
        @Override
        public Run createFromParcel(Parcel in) {
            return new Run(in);
        }

        @Override
        public Run[] newArray(int size) {
            return new Run[size];
        }
    };

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


    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(String.valueOf(runID));
    }

    private Run (Parcel in){
        runID=Integer.valueOf(in.readString());
    }
}
