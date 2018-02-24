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
import tikape.runko.domain.Person;

/**
 *
 * @author Eetu
 */
public class PersonDao implements Dao<Person, Integer> {

    private Database database;

    public PersonDao(Database database) {
        this.database = database;
    }
    
    @Override
    public Person findOne(Integer key) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Person WHERE Person.id = ?");
        stmt.setObject(1, key);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        Person person = new Person(rs.getInt("id"), rs.getString("nimi"));
        person.setBio(rs.getString("bio"));

        rs.close();
        stmt.close();
        conn.close();

        return person;
        
    }

    @Override
    public List<Person> findAll() throws SQLException {
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Person");
        ResultSet rs = stmt.executeQuery();

        List<Person> persons = new ArrayList<>();

        while (rs.next()) {
            Person title = new Person(rs.getInt("id"),
                    rs.getString("nimi"));

            persons.add(title);
        }

        rs.close();
        stmt.close();
        conn.close();

        return persons;
        
    }

    @Override
    public void delete(Integer key) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Person WHERE Person.id = " + key + ";");
        if (!stmt.execute()) {
            System.out.println("QUERY WAS NOT EXECUTED!");
        }

        stmt.close();
        conn.close();

    }
    
    public int getAndAddUserId(String user) throws SQLException {

        String query = "Select Person.id from Person where Person.name = '" + user + "';";

        Connection c = database.getConnection();
        
        PreparedStatement s = c.prepareStatement(query);
        ResultSet results = s.executeQuery();

        
        if (results.next()) { 
            return results.getInt("id");         
        } else {
            PreparedStatement userStmt = c.prepareStatement("INSERT INTO Person (name, bio) values ('" + user + "', 'Default bio');");
            userStmt.execute();
            return getAndAddUserId(user);
        }
    }
    
    public int defaultDirector() throws SQLException {  
        return getAndAddUserId("Unknown director");        
    }
    
    @Override
    public Person saveOrUpdate(Person object) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
