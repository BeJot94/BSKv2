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

@Path("/registration")
public class RegistrationManager {
    
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
    
    // Funkcja pobiera z bazy danych wszystkich lekarzy
    @GET
    @Path("getDoctors")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getAllDoctors() throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT * FROM Osoba where TypOsoby='Lekarz'");
        ResultSet rs = query.executeQuery();
        int count = 0;        
   
        while(rs.next())
            count++;
        
        ArrayList list = new ArrayList(count);
        if(count > 0)
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
    
     // Funkcja pobiera z bazy danych wysztkie rejestracje 
    @GET
    @Path("getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getRegistration() throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT * From Wizyta;");
        ResultSet rs = query.executeQuery();
        int count = 0;        
   
        while(rs.next())
            count++;
        
        ArrayList list = new ArrayList(count);
        if(count > 0)
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
    
    //dodanie rejestracji
    @POST
    @Path("add")
    public void addRegistration(@FormParam("ID") String ID,
                      @FormParam("idPacjenta") String patientID,
                      @FormParam("idLekarza") String doctorID,
                      @FormParam("dataWizyty") String date,
                      @FormParam("godzinaWizyty") String hour,
                      @FormParam("CzyOdbyta") String isDone) throws IOException, SQLException
    {
        Connect();
        Statement statement = connection.createStatement();
        
        statement.executeUpdate("INSERT INTO Wizyta VALUES ('"+patientID+"','"+doctorID+"','"+date+"','"+hour+"','NIE')");

        Disconnect();
        
        response.sendRedirect("../../admin/pages/patients.html");
    }
    
    //usuwanie rejestracji po id
    @POST
    @Path("{id}/delete")
    public void deleteRegistration(@PathParam("id") Integer IDRejestracja) throws SQLException, IOException {
        Connect();        
        Statement statement = connection.createStatement(); 
        
        statement.executeUpdate("DELETE FROM Wizyta WHERE ID = " + IDRejestracja.toString());               
        Disconnect();
        
        response.sendRedirect("../../../../admin/pages/registration.html");
    }
    
    //Zwraca info o rejestracji
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getInfoAboutPerson(@PathParam("id") int id) throws SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT * from Wizyta WHERE ID="+id);
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
    
    // Funkcja edytuje w bazie danych info o rejestracji
    @POST
    @Path("{id}/edit")
        public void editPersonWithID(@PathParam("id") Integer IDRejestracja,
                        @FormParam("idPacjent") String IDPacjent,
                        @FormParam("idLekarz") String IDLekarz,
                        @FormParam("dataWizyty") String dataWizyty,
                        @FormParam("godzinaWizyty") String godzinaWizyty,
                        @FormParam("czyOdbyta") String czyOdbyta ) throws IOException, SQLException
    {
        Connect();        
        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE Wizyta SET ID_Pacjent='" + IDPacjent + "', ID_Lekarz='" +
                IDLekarz + "', DataWizyty='" + dataWizyty + "', GodzinaWizyty='" + godzinaWizyty + 
                "', CzyOdbyta='" + czyOdbyta + "' WHERE ID = " + IDRejestracja);           
        Disconnect();
        
        response.sendRedirect("../../../../rest/admin/pages/registration.html");
    }
    
    
    
}