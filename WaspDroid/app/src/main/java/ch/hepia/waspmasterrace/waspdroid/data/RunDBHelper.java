package ch.hepia.waspmasterrace.waspdroid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ch.hepia.waspmasterrace.waspdroid.data.RunDBContract.*;

/**
 * Created by maximelovino on 13/09/16.
 */
public class RunDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "run.db";

    public RunDBHelper(Context context){
        super(context,DB_NAME,null, DB_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_T_RUN_TABLE = "CREATE TABLE "+ RunListEntry.TABLE_NAME+" ("+
                RunListEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                RunListEntry.COLUMN_RUNID+" INTEGER NOT NULL, "+
                RunListEntry.COLUMN_USERID+" INTEGER NOT NULL, "+
                RunListEntry.COLUMN_DATE+" TEXT NOT NULL, "+
                RunListEntry.COLUMN_SECONDS+" INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_T_RUN_TABLE);

        final String SQL_CREATE_T_RUNDATA_TABLE = "CREATE TABLE "+ RunDataEntry.TABLE_NAME+" ("+
                RunDataEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                RunDataEntry.COLUMN_RUNID+" INTEGER NOT NULL, "+
                RunDataEntry.COLUMN_X+" DOUBLE NOT NULL, "+
                RunDataEntry.COLUMN_Y+" DOUBLE NOT NULL, "+
                RunDataEntry.COLUMN_COUNT+" INTEGER NOT NULL, "+
                RunDataEntry.COLUMN_TIME+" INTEGER NOT NULL, "+

                " FOREIGN KEY ("+RunDataEntry.COLUMN_RUNID+") REFERENCES "+
                RunListEntry.TABLE_NAME+" ("+RunListEntry.COLUMN_RUNID+"));";

        db.execSQL(SQL_CREATE_T_RUNDATA_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+RunListEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+RunDataEntry.TABLE_NAME);
        onCreate(db);
    }

    public void clear(SQLiteDatabase db) {
        onUpgrade(db,this.DB_VERSION,this.DB_VERSION);
    }
}
