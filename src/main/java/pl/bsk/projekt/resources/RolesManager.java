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
        PreparedStatement query = connection.prepareStatement("SELECT * FROM Rola");
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
    
    // Funkcja pobiera z bazy danych role i ich ustawienia.
    @GET
    @Path("roles/account/{idkonta}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getAllRolesForAccountWithID(@PathParam("idkonta") Integer IDKonta) throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT ru.ID_Rola, u.Login, r.Nazwa "
                + "FROM Rola r INNER JOIN RolaUżytkownika ru on r.ID = ru.ID_Rola INNER JOIN "
                + "Użytkownik u on u.ID = ru.ID_Użytkownik WHERE ru.ID_Użytkownik = " + IDKonta.toString() + ";");
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
    @Path("roles/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getRoleWithID(@PathParam("id") Integer id) throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT * FROM Rola WHERE ID='" + id + "';");
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
    
    // Funkcja pobiera z bazy danych informacje o określonej po ID roli.
    @GET
    @Path("role/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getRoleWithName(@PathParam("name") String name) throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT * FROM Rola WHERE Nazwa='" + name + "';");
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
        
        PreparedStatement query = connection.prepareStatement("SELECT * FROM RolaUżytkownika WHERE ID_Rola='" + id.toString() + "';");
        ResultSet rs = query.executeQuery();   
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        
        if(columns > 0)
        {
            while(rs.next())
            {
                statement.executeUpdate("DELETE FROM RolaUżytkownika WHERE ID_Rola = " + id.toString());  
            }
        }
        
        statement.executeUpdate("DELETE FROM Rola WHERE ID = " + id.toString());               
        Disconnect();
        
        response.sendRedirect("../../../../admin/pages/roles.html");
    }
    
    // Funkcja edytuje w bazie danych rolę o określonym ID.
    @POST
    @Path("roles/{id}/edit")
    public void editRoleWithID(@PathParam("id") Integer id,
                            @FormParam("nazwaRoli") String name,
                            @FormParam("opisRoli") String about,
                            @FormParam("wRol") String wRol,
                            @FormParam("eRol") String eRol,
                            @FormParam("uRol") String uRol,
                            @FormParam("dRol") String dRol,
                            @FormParam("wOsob") String wOsob,
                            @FormParam("eOsob") String eOsob,
                            @FormParam("uOsob") String uOsob,
                            @FormParam("dOsob") String dOsob,
                            @FormParam("wPacjentow") String wPacjentow,
                            @FormParam("ePacjentow") String ePacjentow,
                            @FormParam("uPacjentow") String uPacjentow,
                            @FormParam("dPacjentow") String dPacjentow,
                            @FormParam("wUzytkownikow") String wUzytkownikow,
                            @FormParam("eUzytkownikow") String eUzytkownikow,
                            @FormParam("uUzytkownikow") String uUzytkownikow,
                            @FormParam("dUzytkownikow") String dUzytkownikow,
                            @FormParam("wRejestracji") String wRejestracji,
                            @FormParam("eRejestracji") String eRejestracji,
                            @FormParam("uRejestracji") String uRejestracji,
                            @FormParam("dRejestracji") String dRejestracji,
                            @FormParam("wHistorii") String wHistorii,
                            @FormParam("eHistorii") String eHistorii,
                            @FormParam("uHistorii") String uHistorii,
                            @FormParam("dHistorii") String dHistorii,
                            @FormParam("wWizyt") String wWizyt,
                            @FormParam("eWizyt") String eWizyt,
                            @FormParam("uWizyt") String uWizyt,
                            @FormParam("dWizyt") String dWizyt) throws SQLException, IOException {
        Connect();        
        Statement statement = connection.createStatement();
        String query = "UPDATE Rola SET Nazwa='" + name + "', Opis='" + about + "'";
        if(wRol != null && wRol.equals("wRol"))
            query += ", RolaWyświetlanie=1";
        else
            query += ", RolaWyświetlanie=0";
        if(eRol != null && eRol.equals("eRol"))
            query += ", RolaEdycja=1";
        else
            query += ", RolaEdycja=0";
        if(uRol != null && uRol.equals("uRol"))
            query += ", RolaUsuwanie=1";
        else
            query += ", RolaUsuwanie=0";
        if(dRol != null && dRol.equals("dRol"))
            query += ", RolaDodawanie=1";
        else
            query += ", RolaDodawanie=0";
        
        if(wOsob != null && wOsob.equals("wOsob"))
            query += ", OsobaWyświetlanie=1";
        else
            query += ", OsobaWyświetlanie=0";
        if(eOsob != null && eOsob.equals("eOsob"))
            query += ", OsobaEdycja=1";
        else
            query += ", OsobaEdycja=0";
        if(uOsob != null && uOsob.equals("uOsob"))
            query += ", OsobaUsuwanie=1";
        else
            query += ", OsobaUsuwanie=0";
        if(dOsob != null && dOsob.equals("dOsob"))
            query += ", OsobaDodawanie=1";
        else
            query += ", OsobaDodawanie=0";
        
        if(wPacjentow != null && wPacjentow.equals("wPacjentow"))
            query += ", PacjentWyświetlanie=1";
        else
            query += ", PacjentWyświetlanie=0";
        if(ePacjentow != null && ePacjentow.equals("ePacjentow"))
            query += ", PacjentEdycja=1";
        else
            query += ", PacjentEdycja=0";
        if(uPacjentow != null && uPacjentow.equals("uPacjentow"))
            query += ", PacjentUsuwanie=1";
        else
            query += ", PacjentUsuwanie=0";
        if(dPacjentow != null && dPacjentow.equals("dPacjentow"))
            query += ", PacjentDodawanie=1";
        else
            query += ", PacjentDodawanie=0";
        
        if(wUzytkownikow != null && wUzytkownikow.equals("wUzytkownikow"))
            query += ", UżytkownikWyświetlanie=1";
        else
            query += ", UżytkownikWyświetlanie=0";
        if(eUzytkownikow != null && eUzytkownikow.equals("eUzytkownikow"))
            query += ", UżytkownikEdycja=1";
        else
            query += ", UżytkownikEdycja=0";
        if(uUzytkownikow != null && uUzytkownikow.equals("uUzytkownikow"))
            query += ", UżytkownikUsuwanie=1";
        else
            query += ", UżytkownikUsuwanie=0";
        if(dUzytkownikow != null && dUzytkownikow.equals("dUzytkownikow"))
            query += ", UżytkownikDodawanie=1";
        else
            query += ", UżytkownikDodawanie=0";
        
        if(wRejestracji != null && wRejestracji.equals("wRejestracji"))
            query += ", RejestracjaWyświetlanie=1";
        else
            query += ", RejestracjaWyświetlanie=0";
        if(eRejestracji != null && eRejestracji.equals("eRejestracji"))
            query += ", RejestracjaEdycja=1";
        else
            query += ", RejestracjaEdycja=0";
        if(uRejestracji != null && uRejestracji.equals("uRejestracji"))
            query += ", RejestracjaUsuwanie=1";
        else
            query += ", RejestracjaUsuwanie=0";
        if(dRejestracji != null && dRejestracji.equals("dRejestracji"))
            query += ", RejestracjaDodawanie=1";
        else
            query += ", RejestracjaDodawanie=0";
        
        if(wHistorii != null && wHistorii.equals("wHistorii"))
            query += ", HistoriaLeczeniaWyświetlanie=1";
        else
            query += ", HistoriaLeczeniaWyświetlanie=0";
        if(eHistorii != null && eHistorii.equals("eHistorii"))
            query += ", HistoriaLeczeniaEdycja=1";
        else
            query += ", HistoriaLeczeniaEdycja=0";
        if(uHistorii != null && uHistorii.equals("uHistorii"))
            query += ", HistoriaLeczeniaUsuwanie=1";
        else
            query += ", HistoriaLeczeniaUsuwanie=0";
        if(dHistorii != null && dHistorii.equals("dHistorii"))
            query += ", HistoriaLeczeniaDodawanie=1";
        else
            query += ", HistoriaLeczeniaDodawanie=0";
        
        if(wWizyt != null && wWizyt.equals("wWizyt"))
            query += ", WizytaWyświetlanie=1";
        else
            query += ", WizytaWyświetlanie=0";
        if(eWizyt != null && eWizyt.equals("eWizyt"))
            query += ", WizytaEdycja=1";
        else
            query += ", WizytaEdycja=0";
        if(uWizyt != null && uWizyt.equals("uWizyt"))
            query += ", WizytaUsuwanie=1";
        else
            query += ", WizytaUsuwanie=0";
        if(dWizyt != null && dWizyt.equals("dWizyt"))
            query += ", WizytaDodawanie=1";
        else
            query += ", WizytaDodawanie=0";        
        
        query += " WHERE ID = " + id.toString();
        statement.executeUpdate(query);           
        Disconnect();
        
        response.sendRedirect("../../../../admin/pages/roles.html");
    }
    
    // Funkcja do dodawania roli
    @POST
    @Path("roles/add")
    public void addRole(@PathParam("id") Integer id,
                        @FormParam("nazwaRoli") String name,
                        @FormParam("opisRoli") String about,
                        @FormParam("wRol") String wRol,
                        @FormParam("eRol") String eRol,
                        @FormParam("uRol") String uRol,
                        @FormParam("dRol") String dRol,
                        @FormParam("wOsob") String wOsob,
                        @FormParam("eOsob") String eOsob,
                        @FormParam("uOsob") String uOsob,
                        @FormParam("dOsob") String dOsob,
                        @FormParam("wPacjentow") String wPacjentow,
                        @FormParam("ePacjentow") String ePacjentow,
                        @FormParam("uPacjentow") String uPacjentow,
                        @FormParam("dPacjentow") String dPacjentow,
                        @FormParam("wUzytkownikow") String wUzytkownikow,
                        @FormParam("eUzytkownikow") String eUzytkownikow,
                        @FormParam("uUzytkownikow") String uUzytkownikow,
                        @FormParam("dUzytkownikow") String dUzytkownikow,
                        @FormParam("wRejestracji") String wRejestracji,
                        @FormParam("eRejestracji") String eRejestracji,
                        @FormParam("uRejestracji") String uRejestracji,
                        @FormParam("dRejestracji") String dRejestracji,
                        @FormParam("wHistorii") String wHistorii,
                        @FormParam("eHistorii") String eHistorii,
                        @FormParam("uHistorii") String uHistorii,
                        @FormParam("dHistorii") String dHistorii,
                        @FormParam("wWizyt") String wWizyt,
                        @FormParam("eWizyt") String eWizyt,
                        @FormParam("uWizyt") String uWizyt,
                        @FormParam("dWizyt") String dWizyt) throws IOException, SQLException
    {
        Connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO Rola VALUES ('" + name + "', '" + about + "'";
        
        if(wRol != null && wRol.equals("wRol"))
            query += ", 1";
        else
            query += ", 0";
        if(eRol != null && eRol.equals("eRol"))
            query += ", 1";
        else
            query += ", 0";
        if(uRol != null && uRol.equals("uRol"))
            query += ", 1";
        else
            query += ", 0";
        if(dRol != null && dRol.equals("dRol"))
            query += ", 1";
        else
            query += ", 0";
        
        if(wOsob != null && wOsob.equals("wOsob"))
            query += ", 1";
        else
            query += ", 0";
        if(eOsob != null && eOsob.equals("eOsob"))
            query += ", 1";
        else
            query += ", 0";
        if(uOsob != null && uOsob.equals("uOsob"))
            query += ", 1";
        else
            query += ", 0";
        if(dOsob != null && dOsob.equals("dOsob"))
            query += ", 1";
        else
            query += ", 0";
        
        if(wPacjentow != null && wPacjentow.equals("wPacjentow"))
            query += ", 1";
        else
            query += ", 0";
        if(ePacjentow != null && ePacjentow.equals("ePacjentow"))
            query += ", 1";
        else
            query += ", 0";
        if(uPacjentow != null && uPacjentow.equals("uPacjentow"))
            query += ", 1";
        else
            query += ", 0";
        if(dPacjentow != null && dPacjentow.equals("dPacjentow"))
            query += ", 1";
        else
            query += ", 0";
        
        if(wUzytkownikow != null && wUzytkownikow.equals("wUzytkownikow"))
            query += ", 1";
        else
            query += ", 0";
        if(eUzytkownikow != null && eUzytkownikow.equals("eUzytkownikow"))
            query += ", 1";
        else
            query += ", 0";
        if(uUzytkownikow != null && uUzytkownikow.equals("uUzytkownikow"))
            query += ", 1";
        else
            query += ", 0";
        if(dUzytkownikow != null && dUzytkownikow.equals("dUzytkownikow"))
            query += ", 1";
        else
            query += ", 0";
        
        if(wRejestracji != null && wRejestracji.equals("wRejestracji"))
            query += ", 1";
        else
            query += ", 0";
        if(eRejestracji != null && eRejestracji.equals("eRejestracji"))
            query += ", 1";
        else
            query += ", 0";
        if(uRejestracji != null && uRejestracji.equals("uRejestracji"))
            query += ", 1";
        else
            query += ", 0";
        if(dRejestracji != null && dRejestracji.equals("dRejestracji"))
            query += ", 1";
        else
            query += ", 0";
        
        if(wHistorii != null && wHistorii.equals("wHistorii"))
            query += ", 1";
        else
            query += ", 0";
        if(eHistorii != null && eHistorii.equals("eHistorii"))
            query += ", 1";
        else
            query += ", 0";
        if(uHistorii != null && uHistorii.equals("uHistorii"))
            query += ", 1";
        else
            query += ", 0";
        if(dHistorii != null && dHistorii.equals("dHistorii"))
            query += ", 1";
        else
            query += ", 0";
        
        if(wWizyt != null && wWizyt.equals("wWizyt"))
            query += ", 1";
        else
            query += ", 0";
        if(eWizyt != null && eWizyt.equals("eWizyt"))
            query += ", 1";
        else
            query += ", 0";
        if(uWizyt != null && uWizyt.equals("uWizyt"))
            query += ", 1";
        else
            query += ", 0";
        if(dWizyt != null && dWizyt.equals("dWizyt"))
            query += ", 1";
        else
            query += ", 0";
        
        query += ")";
        statement.executeUpdate(query);
        Disconnect();
        
        response.sendRedirect("../../../admin/pages/roles.html");
    }
}
