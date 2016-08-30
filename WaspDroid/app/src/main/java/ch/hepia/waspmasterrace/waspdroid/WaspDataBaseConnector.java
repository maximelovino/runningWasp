package ch.hepia.waspmasterrace.waspdroid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by maximelovino on 30/08/16.
 */
public class WaspDataBaseConnector {

    private Connection dbConnection;

    public WaspDataBaseConnector(String baseURL) throws SQLException {
        this(baseURL,"waspgps","android","nougat");
    }

    public WaspDataBaseConnector(String baseUrl, String dbName, String userName, String password) throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String dbURL="jdbc:mysql://"+baseUrl+"/"+dbName;
        this.dbConnection= DriverManager.getConnection(dbURL,userName,password);
    }

    private ResultSet getQueryData(String query) throws SQLException {
        Statement stm = this.dbConnection.createStatement();

        return stm.executeQuery(query);
    }

    public ArrayList<Run> getRuns() throws SQLException {
        ResultSet runs = getRunList();
        ArrayList<Run> listOfRuns = new ArrayList<>();

        while (runs.next()){
            int runID = runs.getInt("idRun");
            int userID = runs.getInt("idUser");
            Date date = runs.getDate("Date");
            int time = runs.getInt("Seconds");

            Run newRun = new Run(runID,userID,date,time);

            ResultSet data = getRunData(newRun.getRunID());

            while(data.next()){
                double x = data.getDouble("xcoord");
                double y = data.getDouble("ycoord");
                GPScoordinates gps = new GPScoordinates(x,y);
                int chronoValue = (data.getInt("count")-1)*5;

                newRun.addPointOfRun(gps,chronoValue);
            }
            listOfRuns.add(newRun);
        }

        return listOfRuns;
    }


    private ResultSet getRunList() throws SQLException {
        String sqlQuery="SELECT * FROM t_run";
        return getQueryData(sqlQuery);
    }

    private ResultSet getRunData(int runID) throws SQLException {
        String sqlQuery="SELECT * FROM t_rundata WHERE idRun="+runID+" ORDER BY count";
        return getQueryData(sqlQuery);
    }


}
