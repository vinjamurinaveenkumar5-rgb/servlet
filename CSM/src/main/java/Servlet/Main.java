package Servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(
    urlPatterns = {
        "/Sample",
        "/insert",
        "/update",
        "/loginpage"
    },
    initParams = {
        @WebInitParam(
            name = "username",
            value = "root",
            description = "Database user"
        ),
        @WebInitParam(
            name = "password",
            value = "root",
            description = "Database Password"
        )
    }
)
public class Main extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // ---------------- GET METHOD ----------------
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Cookies store information at client side

        Cookie cookie = new Cookie("language", "english");
        Cookie cookie1 = new Cookie("name", "revanth");

        cookie.setMaxAge(0);

        response.addCookie(cookie);
        response.addCookie(cookie1);

        response.getWriter().println("Cookie is added");
        response.getWriter().println(cookie.getValue());
        response.getWriter().println(cookie.getName());

        // Retrieve cookies
        Cookie[] array = request.getCookies();

        if (array != null) {
            for (Cookie i : array) {
                response.getWriter().println(
                    i.getName() + " " + i.getValue()
                );
            }

            response.getWriter().println(
                "Number of cookies: " + array.length
            );
        }
    }

    // ---------------- POST METHOD ----------------
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // -------- REGISTER / INSERT --------
        if (path.equals("/insert")) {

            String bankname = request.getParameter("bankname");
            int accountno =
                Integer.parseInt(request.getParameter("accountno"));
            String accountname =
                request.getParameter("accountname");

            ServletConfig config = getServletConfig();

            try {

                Class.forName("com.mysql.cj.jdbc.Driver");

                String url = "jdbc:mysql://localhost:3306/na";

                String username =
                    config.getInitParameter("username");

                String password =
                    config.getInitParameter("password");

                Connection con =
                    DriverManager.getConnection(
                        url, username, password
                    );

                PreparedStatement st =
                    con.prepareStatement(
                        "INSERT INTO bank VALUES (?, ?, ?)"
                    );

                st.setString(1, bankname);
                st.setInt(2, accountno);
                st.setString(3, accountname);

                int rows = st.executeUpdate();

                if (rows >= 1) {
                    response.sendRedirect("login.html");
                }

                st.close();
                con.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // -------- LOGIN --------
        else if (path.equals("/loginpage")) {

            try {

                Class.forName("com.mysql.cj.jdbc.Driver");

                Connection con =
                    DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/qis",
                        "root",
                        "YOUR_MYSQL_PASSWORD"
                    );

                PreparedStatement st =
                    con.prepareStatement(
                        "SELECT * FROM bank " +
                        "WHERE accountno=? AND accountname=?"
                    );

                int accountno =
                    Integer.parseInt(
                        request.getParameter("accountno")
                    );

                String accountname =
                    request.getParameter("accountname");

                st.setInt(1, accountno);
                st.setString(2, accountname);

                ResultSet rs = st.executeQuery();

                if (rs.next()) {

                    RequestDispatcher rd =
                        request.getRequestDispatcher("home.html");

                    rd.forward(request, response);

                } else {

                    RequestDispatcher rd =
                        request.getRequestDispatcher("login.html");

                    rd.forward(request, response);
                }

                rs.close();
                st.close();
                con.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}