/**
 * Created by user on 20.08.2017
 */
import com.mysql.fabric.jdbc.FabricMySQLDriver;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.*;

@Controller
@EnableAutoConfiguration
public class BookController {
    private final static String URL = "jdbc:mysql://localhost:3306/bookshelf";
    private final static String username = "root";
    private final static String password = "root";

    @RequestMapping("book/list")
    @ResponseBody
    String getAllList() {
        String list = "";
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();

            DriverManager.registerDriver(driver);

            connection = DriverManager.getConnection(URL,username, password);

            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery("SELECT * FROM bookshelf.books;");

            int count = 0;
            while (result.next()) {
                String title = result.getString("title");
                list = list.concat(title + "<br>");
                count++;
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @RequestMapping("book/{id}")
    @ResponseBody
    String getById() {
        return "This is book";
    }

    @RequestMapping("book/create")
    @ResponseBody
    String create() {
        return "You created new book";
    }

    @RequestMapping("book/delete")
    @ResponseBody
    String delete() {
        return "You successfully deleted this book";
    }

    @RequestMapping("book/update")
    @ResponseBody
    String update() {
        return "You changed this note";
    }
}