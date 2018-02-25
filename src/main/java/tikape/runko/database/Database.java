package tikape.runko.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import tikape.runko.domain.IMDBReader;

public class Database {

    private String databaseAddress;

    
    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }

    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseAddress);
    }

    
    public void resetDatabase(boolean leaveEmpty) throws SQLException, IOException {

        System.out.println("--------------------------------------------------------------");
        System.out.println("Starting to reset database.");

        IMDBReader imdb = new IMDBReader(this);
        GenreDao g = new GenreDao(this);
        TitleDao t = new TitleDao(this);
        PersonDao p = new PersonDao(this);
        
        
        //Drop all tables
        System.out.println("Dropping table: ActorTitle");
        Connection c = getConnection();
        PreparedStatement stmt = c.prepareStatement("DROP TABLE ActorTitle");
        stmt.execute();
        stmt.close();

        System.out.println("Dropping table: Genre");
        stmt = c.prepareStatement("DROP TABLE Genre");
        stmt.execute();
        stmt.close();

        System.out.println("Dropping table: Person");
        stmt = c.prepareStatement("DROP TABLE Person");
        stmt.execute();
        stmt.close();

        System.out.println("Dropping table: Title");
        stmt = c.prepareStatement("DROP TABLE Title");
        stmt.execute();
        stmt.close();

        System.out.println("Dropping table: WriterTitle");
        stmt = c.prepareStatement("DROP TABLE WriterTitle");
        stmt.execute();
        stmt.close();
        
        

        //Create new tables
        System.out.println("Creating table: ActorTitle");
        stmt = c.prepareStatement("CREATE TABLE ActorTitle (title_id integer, actor_id integer, FOREIGN KEY (title_id) REFERENCES Title(id), FOREIGN KEY (actor_id) REFERENCES Person(id));");
        stmt.execute();
        stmt.close();

        System.out.println("Creating table: Genre");
        stmt = c.prepareStatement("CREATE TABLE Genre(id integer PRIMARY KEY, name varchar(50));");
        stmt.execute();
        stmt.close();

        System.out.println("Creating table: Person");
        stmt = c.prepareStatement("CREATE TABLE Person (id integer PRIMARY KEY, name varchar(50), bio varchar(500));");
        stmt.execute();
        stmt.close();

        System.out.println("Creating table: Title");
        stmt = c.prepareStatement("CREATE TABLE Title (id integer PRIMARY KEY, director_id integer, genre_id integer, name varchar(50), year integer, description varchar(500), length integer, FOREIGN KEY (director_id) REFERENCES Person(id), FOREIGN KEY (genre_id) REFERENCES Genre(id));");
        stmt.execute();
        stmt.close();

        System.out.println("Creating table: WriterTitle");
        stmt = c.prepareStatement("CREATE TABLE WriterTitle (title_id integer, writer_id integer, FOREIGN KEY (title_id) REFERENCES Title(id), FOREIGN KEY (writer_id) REFERENCES Person(id));");
        stmt.execute();
        stmt.close();
        

        System.out.println("");

        System.out.println("Adding default rows: ");
        

        // Insert defaults
        stmt = c.prepareStatement("INSERT INTO Person (id,name,bio) VALUES (1,'Unknown','This person is not known.');");
        stmt.execute();
        stmt.close();

        stmt = c.prepareStatement("INSERT INTO Genre (id,name) VALUES (1,'Unknown');");
        stmt.execute();
        stmt.close();
        
        if (leaveEmpty) {
            System.out.println("Leaving database empty.");
            System.out.println("Database successfully reset");
            System.out.println("---------------------------------------------------------------");
            return;
        }
        
        // Soft reset information fill
        System.out.println("");
        System.out.println("Adding template information to database:");

        System.out.println("Adding Genres: ");

        g.getAndAddGenreId("Action");
        g.getAndAddGenreId("Comedy");
        g.getAndAddGenreId("Drama");
        g.getAndAddGenreId("Thriller");
        g.getAndAddGenreId("Documentary");

        System.out.println("");
        System.out.println("Adding movies: ");
        System.out.println("");

        // Adding default movies from IMBD
        System.out.println("Adding 'Pulp Fiction'");
        imdb.addTitleFromIMDB("http://www.imdb.com/title/tt0110912/?ref_=nv_sr_1");
        System.out.println("Adding 'Dark Knight'");
        imdb.addTitleFromIMDB("http://www.imdb.com/title/tt0468569/?ref_=nv_sr_1");
        System.out.println("Adding 'Lord of the Rings: The Return of the King'");
        imdb.addTitleFromIMDB("http://www.imdb.com/title/tt0167260/?ref_=nv_sr_2");
        System.out.println("Adding 'The Shawshank Redemption'");
        imdb.addTitleFromIMDB("http://www.imdb.com/title/tt0111161/?ref_=nv_sr_1");
        System.out.println("Adding 'Forrest Gump'");
        imdb.addTitleFromIMDB("http://www.imdb.com/title/tt0109830/?ref_=tt_rec_tt");

        System.out.println("Database successfully reset");
        System.out.println("---------------------------------------------------------------");
    }
}
