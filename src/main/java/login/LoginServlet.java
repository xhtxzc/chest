package login;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.security.MessageDigest;
@WebServlet(name = "LoginServlet" ,urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        String psw = request.getParameter("psw");
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection =DriverManager.getConnection("jdbc:sqlite:E:/JAVA_codes/chest/users.sqlite");
            Statement statement =connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users where id='"+id+"' and psw='"+psw+"'");
            PrintWriter out = response.getWriter();
            if(resultSet.next()){
                out.println(resultSet.getString("id")+"<br>");
                out.println(resultSet.getString("psw"));
            }
            else {
                out.print("error");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        doPost(request,response);
    }
}