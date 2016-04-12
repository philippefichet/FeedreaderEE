/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.servlets;

import fr.feedreader.Migration;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author glopinous
 */
@WebServlet(name = "MigrationServlet", urlPatterns = {"/migration"})
public class MigrationServlet extends HttpServlet {

    static boolean doing = false;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected synchronized void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LogManager.getLogger(getClass()).info("Yo !");
        response.setContentType("text/html;charset=UTF-8");
        if (doing) {
            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet MigrationServlet</title>");            
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Servlet MigrationServlet at " + request.getContextPath() + " : Doing</h1>");
                out.println("</body>");
                out.println("</html>");
            }
        }
        doing = true;
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MigrationServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MigrationServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
        String source = request.getParameter("source");
        String target = request.getParameter("target");
        
        try {
            InitialContext ic = new InitialContext();
            DataSource sourceDs = (DataSource)ic.lookup("java:jboss/datasources/" + source);
            DataSource targetDs = (DataSource)ic.lookup("java:jboss/datasources/" + target);
            Connection connectionSource = sourceDs.getConnection();
            Connection connectionTarget = targetDs.getConnection();
            Migration.migration(connectionSource, connectionTarget);
            connectionSource.close();
            connectionTarget.close();
        } catch (NamingException ex) {
            LogManager.getLogger(getClass()).error(ex);
        } catch (SQLException ex) {
            LogManager.getLogger(getClass()).error(ex);
        }
        doing = false;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
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
     * Handles the HTTP <code>POST</code> method.
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
        return "Short description";
    }// </editor-fold>

}
