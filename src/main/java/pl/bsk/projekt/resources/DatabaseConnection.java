package pl.bsk.projekt.resources;

import java.sql.*;
import com.microsoft.sqlserver.jdbc.*;  

/**
 * Created by Grzegorz on 2017-04-11.
 */
public class DatabaseConnection {
    
    public static Connection getConnection() {
        Connection connection = null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=BSK_Test;integratedSecurity=true;");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }
}
