package ch.hepia.waspmasterrace.waspdroid.data;

import android.provider.BaseColumns;

/**
 * Created by maximelovino on 13/09/16.
 */
public class RunDBContract {

    public static final class RunListEntry implements BaseColumns {
        public static final String TABLE_NAME = "t_run";

        public static final String COLUMN_RUNID = "runID";
        public static final String COLUMN_USERID = "userID";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SECONDS = "seconds";

    }


    public static final class RunDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "t_rundata";

        public static final String COLUMN_RUNID = "runID";
        public static final String COLUMN_X = "xCoord";
        public static final String COLUMN_Y = "yCoord";
        public static final String COLUMN_COUNT = "count";
        public static final String COLUMN_TIME = "time";

    }
}
