package ch.hepia.waspmasterrace.waspdroid.data;

import android.provider.BaseColumns;

/**
 * Class to implement some static nested classes containing the column names for our tables
 */
public class RunDBContract {

    /**
     * Class for the Runs table
     */
    public static final class RunListEntry implements BaseColumns {
        public static final String TABLE_NAME = "t_run";

        public static final String COLUMN_RUNID = "runID";
        public static final String COLUMN_USERID = "userID";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SECONDS = "seconds";

    }

    /**
     * Class for the runData table
     */
    public static final class RunDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "t_rundata";

        public static final String COLUMN_RUNID = "runID";
        public static final String COLUMN_X = "xCoord";
        public static final String COLUMN_Y = "yCoord";
        public static final String COLUMN_COUNT = "count";
        public static final String COLUMN_TIME = "time";

    }
}
