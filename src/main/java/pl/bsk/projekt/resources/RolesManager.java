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
                            @FormParam("mRol") String mRol,
                            @FormParam("wOsob") String wOsob,
                            @FormParam("mOsob") String mOsob,
                            @FormParam("wPacjentow") String wPacjentow,
                            @FormParam("mPacjentow") String mPacjentow,
                            @FormParam("wUzytkownikow") String wUzytkownikow,
                            @FormParam("mUzytkownikow") String mUzytkownikow,
                            @FormParam("wRejestracji") String wRejestracji,
                            @FormParam("mRejestracji") String mRejestracji,
                            @FormParam("wHistorii") String wHistorii,
                            @FormParam("mHistorii") String mHistorii,
                            @FormParam("wWizyt") String wWizyt,
                            @FormParam("mWizyt") String mWizyt) throws SQLException, IOException {
        Connect();        
        Statement statement = connection.createStatement();
        String query = "UPDATE Rola SET Nazwa='" + name + "', Opis='" + about + "'";
        if(wRol != null && wRol.equals("wRol"))
            query += ", RolaWyświetlanie=1";
        else
            query += ", RolaWyświetlanie=0";
        if(mRol != null && mRol.equals("mRol"))
            query += ", RolaModyfikacja=1";
        else
            query += ", RolaModyfikacja=0";
        if(wOsob != null && wOsob.equals("wOsob"))
            query += ", OsobaWyświetlanie=1";
        else
            query += ", OsobaWyświetlanie=0";
        if(mOsob != null && mOsob.equals("mOsob"))
            query += ", OsobaModyfikacja=1";
        else
            query += ", OsobaModyfikacja=0";
        if(wPacjentow != null && wPacjentow.equals("wPacjentow"))
            query += ", PacjentWyświetlanie=1";
        else
            query += ", PacjentWyświetlanie=0";
        if(mPacjentow != null && mPacjentow.equals("mPacjentow"))
            query += ", PacjentModyfikacja=1";
        else
            query += ", PacjentModyfikacja=0";
        if(wUzytkownikow != null && wUzytkownikow.equals("wUzytkownikow"))
            query += ", UżytkownikWyświetlanie=1";
        else
            query += ", UżytkownikWyświetlanie=0";
        if(mUzytkownikow != null && mUzytkownikow.equals("mUzytkownikow"))
            query += ", UżytkownikModyfikacja=1";
        else
            query += ", UżytkownikModyfikacja=0";
        if(wRejestracji != null && wRejestracji.equals("wRejestracji"))
            query += ", RejestracjaWyświetlanie=1";
        else
            query += ", RejestracjaWyświetlanie=0";
        if(mRejestracji != null && mRejestracji.equals("mRejestracji"))
            query += ", RejestracjaModyfikacja=1";
        else
            query += ", RejestracjaModyfikacja=0";
        if(wHistorii != null && wHistorii.equals("wHistorii"))
            query += ", HistoriaLeczeniaWyświetlanie=1";
        else
            query += ", HistoriaLeczeniaWyświetlanie=0";
        if(mHistorii != null && mHistorii.equals("mHistorii"))
            query += ", HistoriaLeczeniaModyfikacja=1";
        else
            query += ", HistoriaLeczeniaModyfikacja=0";
        if(wWizyt != null && wWizyt.equals("wWizyt"))
            query += ", WizytaWyświetlanie=1";
        else
            query += ", WizytaWyświetlanie=0";
        if(mWizyt != null && mWizyt.equals("mWizyt"))
            query += ", WizytaModyfikacja=1";
        else
            query += ", WizytaModyfikacja=0";
        
        
        query += " WHERE ID = " + id.toString();
        statement.executeUpdate(query);           
        Disconnect();
        
        response.sendRedirect("../../../../admin/pages/roles.html");
    }
    
    // Funkcja do dodawania roli
    @POST
    @Path("roles/add")
    public void addRole(@FormParam("ID") String ID,
                    @FormParam("nazwaRoli") String name,
                    @FormParam("opisRoli") String about,
                    @FormParam("wRol") String wRol,
                    @FormParam("mRol") String mRol,
                    @FormParam("wOsob") String wOsob,
                    @FormParam("mOsob") String mOsob,
                    @FormParam("wPacjentow") String wPacjentow,
                    @FormParam("mPacjentow") String mPacjentow,
                    @FormParam("wUzytkownikow") String wUzytkownikow,
                    @FormParam("mUzytkownikow") String mUzytkownikow,
                    @FormParam("wRejestracji") String wRejestracji,
                    @FormParam("mRejestracji") String mRejestracji,
                    @FormParam("wHistorii") String wHistorii,
                    @FormParam("mHistorii") String mHistorii,
                    @FormParam("wWizyt") String wWizyt,
                    @FormParam("mWizyt") String mWizyt) throws IOException, SQLException
    {
        Connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO Rola VALUES ('" + name + "', '" + about + "'";
        
        if(wRol != null && wRol.equals("wRol"))
            query += ", 1";
        else
            query += ", 0";
        if(mRol != null && mRol.equals("mRol"))
            query += ", 1";
        else
            query += ", 0";
        if(wOsob != null && wOsob.equals("wOsob"))
            query += ", 1";
        else
            query += ", 0";
        if(mOsob != null && mOsob.equals("mOsob"))
            query += ", 1";
        else
            query += ", 0";
        if(wPacjentow != null && wPacjentow.equals("wPacjentow"))
            query += ", 1";
        else
            query += ", 0";
        if(mPacjentow != null && mPacjentow.equals("mPacjentow"))
            query += ", 1";
        else
            query += ", 0";
        if(wUzytkownikow != null && wUzytkownikow.equals("wUzytkownikow"))
            query += ", 1";
        else
            query += ", 0";
        if(mUzytkownikow != null && mUzytkownikow.equals("mUzytkownikow"))
            query += ", 1";
        else
            query += ", 0";
        if(wRejestracji != null && wRejestracji.equals("wRejestracji"))
            query += ", 1";
        else
            query += ", 0";
        if(mRejestracji != null && mRejestracji.equals("mRejestracji"))
            query += ", 1";
        else
            query += ", 0";
        if(wHistorii != null && wHistorii.equals("wHistorii"))
            query += ", 1";
        else
            query += ", 0";
        if(mHistorii != null && mHistorii.equals("mHistorii"))
            query += ", 1";
        else
            query += ", 0";
        if(wWizyt != null && wWizyt.equals("wWizyt"))
            query += ", 1";
        else
            query += ", 0";
        if(mWizyt != null && mWizyt.equals("mWizyt"))
            query += ", 1";
        else
            query += ", 0";
        
        query += ")";
        statement.executeUpdate(query);
        Disconnect();
        
        response.sendRedirect("../../../admin/pages/roles.html");
    }
}
