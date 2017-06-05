/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.bsk.projekt.resources;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Grzegorz
 */
@Path("/visit")
public class VisitManager {
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
    
    @GET
    @Path("getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getVisit() throws SQLException {
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
    public void addVisit(@FormParam("idRejestracji") int registerID,
                      @FormParam("idPacjenta") String patientID,
                      @FormParam("idLekarza") String doctorID,
                      @FormParam("dataWizyty") String date,
                      @FormParam("godzinaWizyty") String hour) throws IOException, SQLException
    {
        Connect();
        Statement statement = connection.createStatement();
        
        statement.executeUpdate("INSERT INTO Wizyta VALUES ("+patientID+","+doctorID+",'"+date+"','"+hour+"')");
        
        if(registerID!=0){
            statement.executeUpdate("UPDATE Rejestracja SET CzyOdbyta='TAK' where ID="+registerID );
        }
        Disconnect();
        
        response.sendRedirect("../../admin/pages/visit.html");
    }
    
    //usuwanie rejestracji po id
    @POST
    @Path("{id}/delete")
    public void deleteVisit(@PathParam("id") Integer IDWizyta) throws SQLException, IOException {
        Connect();        
        Statement statement = connection.createStatement(); 
        
        statement.executeUpdate("DELETE FROM Wizyta WHERE ID = " + IDWizyta.toString());               
        Disconnect();
        
        response.sendRedirect("../../../../admin/pages/visit.html");
    }
    
    //Zwraca info o rejestracji
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList getInfoAboutVisit(@PathParam("id") int id) throws SQLException {
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
        public void editVisitWithID(@PathParam("id") Integer IDRejestracja,
                        @FormParam("idPacjent") String IDPacjent,
                        @FormParam("idLekarz") String IDLekarz,
                        @FormParam("dataWizyty") String dataWizyty,
                        @FormParam("godzinaWizyty") String godzinaWizyty ) throws IOException, SQLException
    {
        Connect();        
        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE Wizyta SET ID_Pacjent='" + IDPacjent + "', ID_Lekarz='" +
                IDLekarz + "', DataWizyty='" + dataWizyty + "', GodzinaWizyty='" + godzinaWizyty + "' WHERE ID = " + IDRejestracja);           
        Disconnect();
        
        response.sendRedirect("../../../admin/pages/visit.html");
    }
}
