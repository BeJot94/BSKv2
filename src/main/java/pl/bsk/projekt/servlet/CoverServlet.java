package pl.bsk.projekt.servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet serving dynamically made image.
 *
 * @author psysiu
 */
@WebServlet(name = "CoverServlet", urlPatterns = {"/cover"})
public class CoverServlet extends HttpServlet {

    public static final String AUTHOR = "author";
    
    public static final String TITLE = "title";
    
    public static final String FONT_NAME = "Serif";
    
    /**
     * 
     * @return Dynamically create image.
     */
     private RenderedImage createCover(String author, String title) {
        BufferedImage graph = new BufferedImage(170, 250, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) graph.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, 169, 249);
        
        g2d.setColor(Color.RED);
        g2d.fillRect(40, 90, 90, 130);
        
        g2d.setColor(Color.BLUE);
        Font font = new Font(FONT_NAME, Font.ITALIC, 12);
        g2d.setFont(font);
        g2d.drawString(author, 10, 30);
        
        g2d.setColor(Color.MAGENTA);
        font = new Font(FONT_NAME, Font.ITALIC, 14);
        g2d.setFont(font);
        g2d.drawString(title, 10, 60);
        
        return graph;
    }
     
    /**
     * Processes requests for both HTTP GET and POST methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        RenderedImage image = createCover(request.getParameter(AUTHOR), request.getParameter(TITLE));
        
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                OutputStream out = response.getOutputStream();) {
            ImageIO.write(image, "PNG", buffer);
            response.setContentLength(buffer.size());
            response.setContentType("image/png");
            out.write(buffer.toByteArray());
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    /**
     * Handles the HTTP GET method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP POST method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Dynamic book cover.";
    }

}
