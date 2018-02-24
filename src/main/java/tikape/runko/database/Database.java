package tikape.runko.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseAddress);
    }
}    