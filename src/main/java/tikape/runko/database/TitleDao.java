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
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Title;

public class TitleDao implements Dao<Title, Integer> {

    private Database database;

    public TitleDao(Database database) {
        this.database = database;
    }

    @Override
    public Title findOne(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Title WHERE id = ?");
        stmt.setObject(1, key);
        ResultSet rs = stmt.executeQuery();
        
        if (!rs.next()) {
            return null;
        }

        Title title = new Title(rs.getInt("id"), 
                rs.getString("nimi"), 
                rs.getInt("julkaisuvuosi"), 
                rs.getInt("pituus"));

        rs.close();
        stmt.close();
        conn.close();

        return title;
    }

    @Override
    public List<Title> findAll() throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Title");
        ResultSet rs = stmt.executeQuery();
        
        List<Title> titles = new ArrayList<>();
        
        while (rs.next()) {
            Title title = new Title(rs.getInt("id"), 
                rs.getString("nimi"), 
                rs.getInt("julkaisuvuosi"), 
                rs.getInt("pituus"));

            titles.add(title);
        }

        rs.close();
        stmt.close();
        conn.close();

        return titles;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }

    @Override
    public Title saveOrUpdate(Title object) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private Title save(Title title) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private Title update(Title title) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
