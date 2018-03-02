
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Genre;


public class GenreDao implements Dao<Genre, Integer> {

    private Database database;

    public GenreDao(Database database) {
        this.database = database;
    }
    
    
    @Override
    public Genre findOne(Integer key) throws SQLException {
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Genre WHERE Genre.id = ?");
        stmt.setObject(1, key);
        ResultSet rs = stmt.executeQuery();
        
        // If empty
        if (!rs.next()) {
            rs.close();
            stmt.close();
            conn.close();
            return null;
        }

        Genre genre = new Genre(rs.getInt("id"), rs.getString("name"));

        rs.close();
        stmt.close();
        conn.close();

        return genre;
    }
    
    
    public Genre findOneWithName(String name) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Genre WHERE name = ?");
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        
        // If empty
        if (!rs.next()) {
            rs.close();
            stmt.close();
            conn.close();
            return null;
        }

        Genre genre = new Genre(rs.getInt("id"), rs.getString("name"));

        rs.close();
        stmt.close();
        conn.close();

        return genre;
    }
    
    
    // - Add explanation -
    public int getAndAddGenreId(String genre) throws SQLException {

        String query = "Select Genre.id from Genre where Genre.name = '" + genre + "';";

        Connection c = database.getConnection();
        
        PreparedStatement s = c.prepareStatement(query);
        ResultSet results = s.executeQuery();

        
        if (results.next()) { 
            
            int id = results.getInt("id");
            results.close();
            s.close();
            c.close();
            
            return id;         
        } else {
            
            results.close();
            s.close();
            
            PreparedStatement userStmt = c.prepareStatement("INSERT INTO Genre (name) values ('" + genre + "');");
            userStmt.execute();
            c.close();
            return getAndAddGenreId(genre);
        }
    }
    
    
    @Override
    public List<Genre> findAll() throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Genre Group By Genre.name");
        ResultSet rs = stmt.executeQuery();
        
        List<Genre> genres = new ArrayList<>();
        
        while (rs.next()) {
            genres.add(new Genre(rs.getInt("id"), rs.getString("name")));
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return genres;
    }
    
    
    // Does not include the default 'Unknown' genre
    public List<Genre> findAllButDefault() throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Genre WHERE id > 1");
        ResultSet rs = stmt.executeQuery();

        List<Genre> genres = new ArrayList<>();

        while (rs.next()) {
            genres.add(new Genre(rs.getInt("id"), rs.getString("name")));
        }

        rs.close();
        stmt.close();
        conn.close();

        return genres;
    }

    
    @Override
    public Genre saveOrUpdate(Genre genre) throws SQLException {
        if (findOneWithName(genre.getName()) == null) {
            return save(genre);
        }
        return update(genre);
    }
    
    private Genre save(Genre genre) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Genre (name) values (?)");
        stmt.setString(1, genre.getName());
        
        stmt.execute();
        stmt.close();
        
        
        stmt = conn.prepareStatement("SELECT * FROM Genre WHERE name = ?");
        stmt.setString(1, genre.getName());
        
        ResultSet rs = stmt.executeQuery();
        
        // If empty
        if (!rs.next()) {
            rs.close();
            stmt.close();
            conn.close();
            return null;
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return genre;
    }


    private Genre update(Genre genre) throws SQLException {
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE Genre SET name = ? WHERE id = ?)");
        stmt.setString(1, genre.getName());
        stmt.setInt(2, findOneWithName(genre.getName()).getId());
        
        stmt.executeUpdate();
        stmt.close();
        conn.close();
        
        return genre;
    }

    
    @Override
    public void delete(Integer key) throws SQLException {
        
        // Does the genre exist?
        if (findOne(key) == null) {
            System.out.println("QUERY WAS NOT EXECUTED!");;
        }

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Genre WHERE Genre.id = " + key);
        
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }   
}
