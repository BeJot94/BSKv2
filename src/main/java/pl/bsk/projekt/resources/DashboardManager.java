package pl.bsk.projekt.resources;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Bartosz on 08.04.2017.
 */

@Path("/dashboardmanager")
public class DashboardManager {
    
    private Connection connection = null;
    
    private void Connect() {
        try {
            connection = DatabaseConnection.getConnection();
            if (connection != null) {
                System.out.println("Connection estabilished");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Disconnect(){
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Context
    HttpServletResponse response;
    
    // Funkcja pobiera z bazy danych role i ich ustawienia.
    @GET
    @Path("roles/all")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getAllRoles() throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT * FROM Roles");
        ResultSet rs = query.executeQuery();
        int iloscRol = 0;        
   
        while(rs.next())
            iloscRol++;
        
        ArrayList list = new ArrayList(iloscRol);
        if(iloscRol > 0)
        {
            rs = query.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            
            
            while (rs.next())
            {
                HashMap row = new HashMap(columns);
                for (int i = 1; i <= columns; ++i)
                    row.put(md.getColumnName(i), rs.getObject(i));
                list.add(row);
            }
            Disconnect();
            return list;
        }
        
        Disconnect();
        return list;
    }
}
