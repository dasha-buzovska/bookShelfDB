import org.springframework.boot.SpringApplication;

/**
 * Created by user on 21.08.2017
 */
public class BookShelf {
    private final static String URL = "jdbc:mysql://localhost:3306/bookshelf";
    private final static String username = "root";
    private final static String password = "root";

    public static void main(String[] args) {
        SpringApplication.run(BookController.class, args);
    }
}
