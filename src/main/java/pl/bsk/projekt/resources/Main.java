package pl.bsk.projekt.resources;

import java.io.IOException;
import java.sql.*;
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
import java.util.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

/**
 * Created by Bartosz on 08.04.2017.
 */

@Path("/")
public class Main {
    
    private Connection connection = null;
    
    public List<String> listOfRoles=new ArrayList<>();
    
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
    
    public String getRoles() throws IOException, SQLException {
        Connect();
        PreparedStatement query = connection.prepareStatement("SELECT Name FROM Roles");
        ResultSet rs = query.executeQuery();
        
        while(rs.next()){
            listOfRoles.add(rs.getString("Name"));
        }
       return "aaa";
        
    }
    
    private boolean checkRole(String username, String role) throws IOException, SQLException{
        PreparedStatement query = connection.prepareStatement("SELECT Name FROM Roles WHERE ID IN(SELECT RoleID FROM UsersRoles WHERE UserID IN(SELECT ID FROM Users WHERE Username='"+username+"'))");
        ResultSet rs = query.executeQuery();
        while(rs.next()){
            if(role.equals(rs.getString("Name"))){
            return true;
            }
        }
        return false;
    }

    
    @Context
    HttpServletResponse response;

    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMessage() {
        return "Hello world!";
    }
    
    // Funkcja uruchamiana w momencie wchodzenia na stronę logowania.
    // 1. Jeśli użytkownik jest już zalogowany, to przerzuca do panelu.
    // 2. Jeśli użytkownik nie jest zalogowany, zostaje przy oknie logowania.
    @GET
    @Path("login/check")
    public String checkIfLogged(@Context HttpServletRequest req) throws IOException {
        // Sprawdzamy, czy istnieje sesja dla użytkownika i jeśli tak to przerzucamy od razu do panelu
        HttpSession session = req.getSession(false);
        Object user = session.getAttribute("user");
        if(user != null)
           return "logged";
        
        return "unlogged";            
    }
    
    // Funkcja zwraca z sesji dane o zalogowanym użytkowniku.
    @GET
    @Path("login/info/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getInfoAboutLoggedUser(@Context HttpServletRequest req){
        // Sprawdzamy, czy istnieje sesja dla użytkownika i jeśli tak to przerzucamy od razu do panelu
        HttpSession session = req.getSession(false);
        Object user = session.getAttribute("user");
        List<String> info = new ArrayList<String>();
        if(user != null){
            info.add(user.toString());
            info.add(session.getAttribute("role").toString());
        }
        
        return info;
    }

    // Funkcja do obsługi formularza logowania
    @POST
    @Path("login/form")
    public void logIn(@Context HttpServletRequest req,
                      @FormParam("lg_username") String username,
                      @FormParam("lg_password") String password,
                      @FormParam("lg_role") String role) throws IOException, SQLException
    {
        Connect();
        boolean isLoginSuccesfull = false;
        Statement statement = connection.createStatement();
        PreparedStatement query = connection.prepareStatement("SELECT * FROM Users");
        ResultSet rs = query.executeQuery();
        while(rs.next()) {
            // Sprawdzamy, czy jest już zalogowany z określoną rolą i jeśli tak, to z nią logujemy
            if (username.equals(rs.getString("Username")) && password.equals(rs.getString("Password"))
                    && !"null".equals(rs.getString("CurrentRole")) && !"NULL".equals(rs.getString("CurrentRole")))
            {
                //sprawdzam czy posiada role, z którą próbuje się zalogować
                if(checkRole(rs.getString("Username"),role)){
                
                    isLoginSuccesfull = true;

                    // Ustawiamy sesję dla danego użytkownika, jeżeli aktualnie jej nie ma
                    HttpSession session = req.getSession(true);
                    session.setAttribute("user", rs.getString("Username"));
                    session.setAttribute("role", rs.getString("CurrentRole"));

                    Disconnect();
                    break;
                }
            }
            // Sprawdzamy czy login i hasło się zgadzają.
            else if (username.equals(rs.getString("Username")) && password.equals(rs.getString("Password"))){
                // Jeśli aktualna rola (dla sesji użytkownika) się zgadza lub aktualnie nie ma roli (brak
                // sesji) to logowanie poprawne.
                if(role.equals(rs.getString("CurrentRole")) || "null".equals(rs.getString("CurrentRole")) || "NULL".equals(rs.getString("CurrentRole"))){
                    if(checkRole(rs.getString("Username"),role)){    
                        isLoginSuccesfull = true;
                        
                        // Ustawiamy sesję dla danego użytkownika
                        HttpSession session = req.getSession(true);
                        session.setAttribute("user", rs.getString("Username"));
                        session.setAttribute("role", rs.getString("CurrentRole"));
                            
                        // Ustawiamy aktualną rolę  
                        statement.executeUpdate("UPDATE Users SET CurrentRole = '" + role + "' WHERE ID = "
                                + Integer.parseInt(rs.getString("ID")));

                        Disconnect();
                        break;
                    }
                }       
            }
        }
        if(isLoginSuccesfull)
            response.sendRedirect("../../admin/index.html");
        else
            response.sendRedirect("../../login.html?logged=failed");
    }
    
    // Funkcja do obsługi wylogowywania
    @GET
    @Path("logout")
    public void logout(@Context HttpServletRequest req) throws IOException, SQLException
    {
        // Pobieramy nazwę użytkownika z sesji
        HttpSession session = req.getSession(false);
        Object user = session.getAttribute("user");
        
        // Jeżeli mamy zalogowanego użytkownika, to dokonujemy zmian
        if(user != null)
        {
            Connect();
            Statement statement = connection.createStatement();
            PreparedStatement query = connection.prepareStatement("SELECT * FROM Users WHERE Username='" +
                    user.toString() + "';");
            ResultSet rs = query.executeQuery();
            
            while(rs.next()) {
                // Sprawdzamy, czy taki użytkownik istnieje w BD i czy jest aktualnie zalogowany
                // (ma ustawioną aktualną rolę)
                if (user.toString().equals(rs.getString("Username")) && rs.getString("CurrentRole") != "null")
                {
                    // Jeśli znaleziono użytkownika, to ustawiamy jego aktualną rolę na null
                    statement.executeUpdate("UPDATE Users SET CurrentRole = 'null' WHERE Username = '" + 
                                    rs.getString("Username") + "';");
                    
                    // Usuwamy atrybut użytkownik z sesji
                    session.removeAttribute("user");
                    session.removeAttribute("role");
                    
                    Disconnect();
                    break;
                }
            }
            
            response.sendRedirect("../login.html");
        }
    }
}
