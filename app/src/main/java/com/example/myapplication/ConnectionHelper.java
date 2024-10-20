package com.example.myapplication;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    Connection conn;
    String username, password, ip, port, database;

    public Connection connectionClass() {
        ip = "10.0.2.2";
        database = "PRM_Demo_Database";
        username = "root";
        password = "12345";
        port = "3306";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection connection = null;
        String ConnectionURL = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            ConnectionURL = "jdbc:mysql://" + ip + ":" + port + "/" + database + "?user=" + username + "&password=" + password;
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return connection;
    }
}
