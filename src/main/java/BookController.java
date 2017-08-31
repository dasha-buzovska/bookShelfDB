/**
 * Created by user on 20.08.2017
 */

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.*;

@Controller
@EnableAutoConfiguration
public class BookController {
    private final static String URL = "jdbc:mysql://localhost:3306/bookshelf";
    private final static String username = "root";
    private final static String password = "root";
    Connection connection;


    @RequestMapping("book/list")
    @ResponseBody
    String getAllList() {
        String list = "";

        try {
            Driver driver = new FabricMySQLDriver();

            DriverManager.registerDriver(driver);

            connection = DriverManager.getConnection(URL,username, password);

            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery("SELECT * FROM bookshelf.books;");

            while (result.next()) {
                list = list.concat("<a href=" + "http://localhost:8080/book/" + result.getString("id") + "> " + result.getString("title") + "</a>  ");
                list = list.concat("<a href=" + "http://localhost:8080/book/delete/" + result.getString("id") + ">" + "delete" + "</a>"  + "<br>");
            }
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @RequestMapping("book/{id}")
    @ResponseBody
    String getById(@PathVariable("id") String id) {
        String book = "";
        try {
            Driver driver = new FabricMySQLDriver();

            DriverManager.registerDriver(driver);

            connection = DriverManager.getConnection(URL, username, password);

            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery("SELECT * FROM bookshelf.books WHERE id =" + id + ";");
            while (result.next()) {
                book = book.concat("<a href=" + result.getString("link") + "> " + result.getString("title") + "</a>");
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

            return book;
    }

    @RequestMapping("book/delete/{id}")
    @ResponseBody
    String delete(@PathVariable("id") String id) {
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, username, password);
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM bookshelf.books WHERE id =" + id + ";");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getAllList();
    }

    @RequestMapping("book/create")
    @ResponseBody
    String create() {
        return "You created new book";
    }

    @RequestMapping("book/update")
    @ResponseBody
    String update() {
        return "You changed this note";
    }
}