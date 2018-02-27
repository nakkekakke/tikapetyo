package tikape.runko.database;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import tikape.runko.domain.IMDBReader;

public class Database {

    private String databaseAddress;
    String[] tables;
    String[] movies;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;

        //Default tables
        tables = new String[5];
        tables[0] = "ActorTitle (title_id integer, actor_id integer, FOREIGN KEY (title_id) REFERENCES Title(id), FOREIGN KEY (actor_id) REFERENCES Person(id));";
        tables[1] = "Genre (id integer PRIMARY KEY, name varchar(50));";
        tables[2] = "Person (id integer PRIMARY KEY, name varchar(50), bio varchar(500));";
        tables[3] = "Title (id integer PRIMARY KEY, director_id integer, genre_id integer, name varchar(50), year integer, description varchar(500), length integer, FOREIGN KEY (director_id) REFERENCES Person(id), FOREIGN KEY (genre_id) REFERENCES Genre(id));";
        tables[4] = "WriterTitle (title_id integer, writer_id integer, FOREIGN KEY (title_id) REFERENCES Title(id), FOREIGN KEY (writer_id) REFERENCES Person(id));";

        //Default movies
        movies = new String[5];
        movies[0] = "http://www.imdb.com/title/tt0110912/?ref_=nv_sr_1";
        movies[1] = "http://www.imdb.com/title/tt0468569/?ref_=nv_sr_1";
        movies[2] = "http://www.imdb.com/title/tt0167260/?ref_=nv_sr_2";
        movies[3] = "http://www.imdb.com/title/tt0111161/?ref_=nv_sr_1";
        movies[4] = "http://www.imdb.com/title/tt0109830/?ref_=tt_rec_tt";

    }

    public Connection getConnection() throws SQLException {
        System.out.println("Got connection");
        return DriverManager.getConnection(databaseAddress);
    }

    public void checkDatabaseValidity() throws Exception {

        System.out.println("Checking database validity:");

        Connection c = getConnection();
        PreparedStatement stmt = null;

        for (String table : tables) {
            String tableName = table.split(" ")[0];
            try {
                stmt = c.prepareStatement("Select * from " + tableName);
                stmt.execute();
                stmt.close();

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Missing table: " + tableName);
                System.out.println("Database is not valid. Resetting database...");
                System.out.println("");
                c.close();              
                resetDatabase(true);        
                break;
            }
        }

        c.close();   
        System.out.println("Database is valid.");

    }

    public void resetDatabase(boolean leaveEmpty) throws Exception {

        System.out.println("--------------------------------------------------------------");
        System.out.println("Starting to reset database.");

        if (!leaveEmpty) {
            System.out.println("Deleting database.");

            File current = new File("db/imbd.db");
            File template = new File("db/imdbBig.db");

            current.delete();

            Files.copy(template.toPath(), current.toPath(), StandardCopyOption.REPLACE_EXISTING);

            //Files.copy(template.toPath(), current.toPath());
            System.out.println("Database successfully reset");
            System.out.println("---------------------------------------------------------------");
            return;
        }
        
        IMDBReader imdb = new IMDBReader(this);
        GenreDao g = new GenreDao(this);
        TitleDao t = new TitleDao(this);
        PersonDao p = new PersonDao(this);

        Connection c = getConnection();
        PreparedStatement stmt = null;

        //Drop all tables
        for (String table : tables) {
            try {
                String tableName = table.split(" ")[0];
                System.out.println("Dropping table: " + tableName);
                stmt = c.prepareStatement("DROP TABLE " + tableName + ";");
                stmt.execute();
                stmt.close();
            } catch (Exception e) {
            }
        }

        //Create new tables
        for (String table : tables) {
            String[] split = table.split(" ");
            System.out.println("Creating table: " + split[0]);
            stmt = c.prepareStatement("CREATE TABLE " + table);
            stmt.execute();
            stmt.close();
        }

        System.out.println("");

        System.out.println("Adding default rows: ");

        // Insert defaults
        stmt = c.prepareStatement("INSERT INTO Person (id,name,bio) VALUES (1,'Unknown','This person is not known.');");
        stmt.execute();
        stmt.close();

        stmt = c.prepareStatement("INSERT INTO Genre (id,name) VALUES (1,'Unknown');");
        stmt.execute();
        stmt.close();
        c.close();

        System.out.println("Leaving database empty.");
        System.out.println("Database successfully reset");
        System.out.println("---------------------------------------------------------------");
    }
}
