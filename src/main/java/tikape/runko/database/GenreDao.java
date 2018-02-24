/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import tikape.runko.domain.Genre;
import tikape.runko.domain.Person;

/**
 *
 * @author Eetu
 */
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

        if (!rs.next()) {
            return null;
        }

        Genre genre = new Genre(rs.getInt("id"), rs.getString("name"));

        rs.close();
        stmt.close();
        conn.close();

        return genre;
        
    }

    public int getAndAddGenreId(String genre) throws SQLException {

        String query = "Select Genre.id from Genre where Genre.name = '" + genre + "';";

        Connection c = database.getConnection();
        
        PreparedStatement s = c.prepareStatement(query);
        ResultSet results = s.executeQuery();

        
        if (results.next()) { 
            return results.getInt("id");         
        } else {
            PreparedStatement userStmt = c.prepareStatement("INSERT INTO Genre (name) values ('" + genre + "');");
            userStmt.execute();
            return getAndAddGenreId(genre);
        }
    }
    
    
    @Override
    public List<Genre> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Genre saveOrUpdate(Genre object) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
