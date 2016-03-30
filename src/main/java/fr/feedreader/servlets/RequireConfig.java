/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.servlets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author philipefichet
 */
@WebServlet(
    name = "RequireConfig",
    urlPatterns = {"/require-config"},
    loadOnStartup = -1
)
public class RequireConfig extends HttpServlet {

    static private Map<String, String> requireJsConfig = new HashMap<>();
//    static private String min = "min.";
    static private String min = "";
    
    static {
        long millis = System.currentTimeMillis();
        requireJsConfig.put("jquery", "/webjars/jquery/2.2.1/dist/jquery." + min + "js?");
        requireJsConfig.put("react", "/webjars/react/0.14.7/dist/react." + min + "js?");
        requireJsConfig.put("reactDOM", "/webjars/react-dom/0.14.7/dist/react-dom." + min + "js?");
        requireJsConfig.put("Feed/List", "/js/FeedList.js?" + millis);
        requireJsConfig.put("Feed/Item", "/js/FeedItem.js?" + millis);
        requireJsConfig.put("Feed/Item/List", "/js/FeedItemList.js?" + millis);
        requireJsConfig.put("Feed/Edit", "/js/admin/FeedEdit.js?" + millis);
        requireJsConfig.put("BS3/Badge", "/js/bs3/Badge.js?" + millis);
    }
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(resp.SC_OK);
        resp.setContentType("text/javascript");
        String base = req.getContextPath();
        resp.getWriter().write(
            "require.config({\n" +
            "    paths: {\n" +
                    requireJsConfig.entrySet().stream().map((Map.Entry<String, String> t) -> {
                        return "        \"" + t.getKey() + "\": \"" + base + t.getValue() + "\",\n";
                    }).reduce((String t, String u) -> t + u).get() +
            "    }\n" +
            "});"
        );
    }

}
