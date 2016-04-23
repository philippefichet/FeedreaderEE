/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author philippefichet
 */
@WebServlet(name = "SingleWebPageServlet", urlPatterns = {
    "/feed/*",
    "/feedItem/*"
}, loadOnStartup = -1)
public class SingleWebPageServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("resp = " + resp);
        getServletContext().getRequestDispatcher("/").forward(req, resp);
    }
}
