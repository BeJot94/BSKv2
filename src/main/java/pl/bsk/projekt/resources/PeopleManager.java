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

@Path("/peoplemanager")
public class PeopleManager {
    
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
    @Path("people/all")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getPeople() throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT Osoba.ID as 'oID', * FROM Osoba LEFT JOIN Użytkownik on Osoba.ID = Użytkownik.ID_Osoba;");
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
    
    //Zwraca info o wybranym pacjencie
    @GET
    @Path("person/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getInfoAboutPerson(@PathParam("id") int id) throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT Osoba.ID as 'oID', * FROM Osoba LEFT JOIN Użytkownik on Osoba.ID = Użytkownik.ID_Osoba WHERE Osoba.ID="+id);
        ResultSet rs = query.executeQuery();
        
        ArrayList list=new ArrayList(1);
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        
        while(rs.next()){
        HashMap row = new HashMap(columns);   
        for (int i = 1; i <= columns; ++i)
            row.put(md.getColumnName(i), rs.getObject(i));
        list.add(row);
        }
        
        
        Disconnect();
        return list;
    }
    
    // Funkcja edytuje w bazie danych użytkownika o określonym ID.
    @POST
    @Path("account/{id}/edit")
    public void editAccountWithID(@PathParam("id") Integer IDKonta,
                        @FormParam("login") String login,
                        @FormParam("haslo") String haslo) throws IOException, SQLException
    {
        Connect();        
        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE Użytkownik SET Login='" + login + "', Hasło='" +
                haslo + "' WHERE ID = " + IDKonta.toString());           
        Disconnect();
        
        response.sendRedirect("../../../../admin/pages/people.html");
    }
    
    // Funkcja do dodawania użytkownika
    @POST
    @Path("account/{id}/add")
    public void addAccount(@PathParam("id") String IDOsoba,
                        @FormParam("login") String login,
                        @FormParam("haslo") String haslo) throws IOException, SQLException
    {
        Connect();
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO Użytkownik VALUES (" + IDOsoba
                + ", '" + login + "', '" + haslo + "', null);");

        Disconnect();
        
        response.sendRedirect("../../../../admin/pages/people.html");
    }
}

