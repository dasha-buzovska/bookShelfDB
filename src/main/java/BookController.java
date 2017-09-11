/**
 * Created by user on 20.08.2017
 */

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
@EnableAutoConfiguration
public class BookController {
    private final static String URL = "jdbc:mysql://localhost:3306/bookshelf";
    private final static String username = "root";
    private final static String password = "root";

    @RequestMapping("book/list")
    String getAllList(ModelMap model) {
        ArrayList<HashMap> books = new ArrayList<HashMap>();
        try {
            Connection connection = getCurrentConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM bookshelf.books;");
            while (result.next()) {
                HashMap<String, String> book = new HashMap<String, String>();
                book.put("link", "http://localhost:8080/book/" + result.getString("id"));
                book.put("title", result.getString("title"));
                book.put("deleteLink", "http://localhost:8080/book/delete/" + result.getString("id"));
                book.put("updateLink", "http://localhost:8080/book/update/" + result.getString("id"));
                books.add(book);
            }
            model.addAttribute("books", books);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "booklist";
    }

    @RequestMapping("book/{id}")
    public String indexAction (ModelMap model, @PathVariable("id") String id) {
        HashMap<String, String> book;
        try {
            Connection connection = getCurrentConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM bookshelf.books WHERE id =" + id + ";");
            book = getBook(result);
            model.addAttribute("book", book);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "example";
    }

    @RequestMapping(value = "book/delete/{id}")
    String delete(@PathVariable("id") String id) {
        try {
            Connection connection = getCurrentConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM bookshelf.books WHERE id =" + id + ";");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/book/list";
    }

    @RequestMapping(value = "book/create", method = RequestMethod.POST)
    String create(HttpServletRequest request) {
        try {
            String sql = "INSERT INTO books " + "(title, author, link, publish_year) VALUES (?, ?, ?, ?)";
            Connection connection = getCurrentConnection();
            setDates(sql, connection, request);
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/book/list";
    }

    @RequestMapping(value = "book/create", method = RequestMethod.GET)
    String makeForm() {
        return "form";
    }

    @RequestMapping(value = "book/update/{id}", method = RequestMethod.GET)
    String getUpdateForm(ModelMap model, @PathVariable("id") String id) {
        HashMap<String, String> book;
        try {
            Connection connection = getCurrentConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM bookshelf.books WHERE id =" + id + ";");
            book = getBook(result);
            book.put("id", id);
            model.addAttribute("book", book);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "updateForm";
    }

    @RequestMapping(value = "book/update/{id}", method = RequestMethod.POST)
    String update(HttpServletRequest request, @PathVariable("id") String id) {
        try {
            Connection connection = getCurrentConnection();
            String sql = "UPDATE books SET title=?, author=?, link=?, publish_year=?  WHERE id=" + id + ";";
            setDates(sql, connection, request);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/book/list";
    }

    @RequestMapping("book/authors")
    String getOrderByAuthors(ModelMap model) {
        ArrayList<HashMap> books = new ArrayList<HashMap>();
        try {
            int counter = 0;
            Connection connection = getCurrentConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT author, count(title) AS bookNumber from books group by author order by author asc;");
            while (result.next()) {
                HashMap<String, String> book = new HashMap<String, String>();
                book.put("author", result.getString("author"));
                book.put("encodedAuthor", URLEncoder.encode(result.getString("author"), "UTF-8"));
                book.put("bookNumber", result.getString("bookNumber"));
                counter++;
                book.put("authorId","" + counter);
                books.add(book);
            }
            model.addAttribute("books", books);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "authors";
    }

    @RequestMapping("book/author/{author:.+}")
    String getOrderByAuthors(ModelMap model, @PathVariable("author") String author) {
        ArrayList<HashMap> books = new ArrayList<HashMap>();
        try {
            Connection connection = getCurrentConnection();
            Statement statement = connection.createStatement();
            System.out.println(author);
            String authorDecode = java.net.URLDecoder.decode(author, "UTF-8");
            System.out.println(authorDecode);
            ResultSet result = statement.executeQuery("select * from books where author = '" + authorDecode + "';");
            while (result.next()) {
                HashMap<String, String> book = new HashMap<String, String>();
                book.put("title", result.getString("title"));
                book.put("id", result.getString("id"));
                books.add(book);
            }
            model.addAttribute("books", books);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "author";
    }

    private static Connection getCurrentConnection() {
        Connection connection = null;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL,username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static HashMap<String, String> getBook(ResultSet result) {
        HashMap<String, String> book = new HashMap<String, String>();
        try {
            while (result.next()) {
                book.put("title", result.getString("title"));
                book.put("link", result.getString("link"));
                book.put("year", result.getString("publish_year"));
                book.put("author", result.getString("author"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }

    private static void setDates(String sql, Connection connection, HttpServletRequest request) {
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, request.getParameter("title"));
            ps.setString(2, request.getParameter("author"));
            ps.setString(3, request.getParameter("link"));
            ps.setString(4, request.getParameter("year"));
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
