package SERVER.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL      = "jdbc:postgresql://localhost:5432/dtdatabase";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "Nicholas102405";

    public static Connection getConnection(){

        Connection connection = null;

        try {

              Class.forName("org.postgresql.Driver");
              connection = DriverManager.getConnection(URL, USER, PASSWORD);

            } catch (SQLException e) {
                    throw new RuntimeException(e);}

              catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);}

        return connection;
    }

}
