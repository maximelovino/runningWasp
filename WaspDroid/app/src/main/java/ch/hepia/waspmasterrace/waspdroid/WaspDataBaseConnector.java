package ch.hepia.waspmasterrace.waspdroid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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


    public ResultSet getRunList() throws SQLException {
        String sqlQuery="SELECT * FROM t_run";
        return getQueryData(sqlQuery);
    }

    public ResultSet getRunData() throws SQLException {
        String sqlQuery="SELECT * FROM t_rundata";
        return getQueryData(sqlQuery);
    }


}