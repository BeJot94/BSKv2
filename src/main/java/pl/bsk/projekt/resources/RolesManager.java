package pl.bsk.projekt.resources;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Bartosz on 08.04.2017.
 */

@Path("/rolesmanager")
public class RolesManager {
    
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
    
    // Funkcja pobiera z bazy danych informacje o określonej po ID roli.
    @GET
    @Path("roles/{id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getRoleWithID(@PathParam("id") Integer id) throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT * FROM Roles WHERE ID='" + id + "';");
        ResultSet rs = query.executeQuery();   
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();     
        ArrayList list = new ArrayList();
        
        if (rs.next())
        {
            HashMap row = new HashMap(columns);
            for (int i = 1; i <= columns; ++i)
                row.put(md.getColumnName(i), rs.getObject(i));
            list.add(row);
        }
               
        Disconnect();
        return list;
    }
    
    // Funkcja usuwa z bazy danych rolę o określonym ID.
    @POST
    @Path("roles/{id}/delete")
    public void deleteRoleWithID(@PathParam("id") Integer id) throws SQLException, IOException {
        Connect();        
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM Roles WHERE id = " + id.toString());               
        Disconnect();
        
        response.sendRedirect("../../../../admin/pages/roles.html");
    }
    
    // Funkcja edytuje w bazie danych rolę o określonym ID.
    @POST
    @Path("roles/{id}/edit")
    public void editRoleWithID(@PathParam("id") Integer id,
                            @FormParam("nazwaRoli") String name,
                            @FormParam("opisRoli") String about) throws SQLException, IOException {
        Connect();        
        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE Roles SET Name='" + name + "', About='" +
                about + "' WHERE ID = " + id.toString());           
        Disconnect();
        
        response.sendRedirect("../../../../admin/pages/roles.html");
    }
    
    // Funkcja do dodawania roli
    @POST
    @Path("roles/add")
    public void addRole(@FormParam("ID") String ID,
                      @FormParam("nazwaRoli") String name,
                      @FormParam("opisRoli") String about) throws IOException, SQLException
    {
        Connect();
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO Roles VALUES ('" + name + "', '" + about + "')");

        Disconnect();
        
        response.sendRedirect("../../../admin/pages/roles.html");
    }
}
