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

        Person person = new Person(rs.getInt("id"), rs.getString("name"));
        person.setBio(rs.getString("bio"));

        rs.close();
        stmt.close();
        conn.close();

        return person;
        
    }
    
        public Person findOneWithName(String name) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Person WHERE name = ?");
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        Person person = new Person(rs.getInt("id"), rs.getString("name"));

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
                    rs.getString("name"));

            persons.add(title);
        }

        rs.close();
        stmt.close();
        conn.close();

        return persons;
        
    }

    @Override
    public void delete(Integer key) throws SQLException {

        if (findOne(key) == null) {
            System.out.println("QUERY WAS NOT EXECUTED!");
        }

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Title WHERE Title.id = " + key);
        
        stmt.executeUpdate();

        stmt.close();
        conn.close();

    }
    
    public int getAndAddPersonId(String user) throws SQLException {

        String query = "Select Person.id from Person where Person.name = '" + user + "';";

        Connection c = database.getConnection();
        
        PreparedStatement s = c.prepareStatement(query);
        ResultSet results = s.executeQuery();

        
        if (results.next()) { 
            return results.getInt("id");         
        } else {
            PreparedStatement userStmt = c.prepareStatement("INSERT INTO Person (name, bio) values ('" + user + "', 'Default bio');");
            userStmt.execute();
            return getAndAddPersonId(user);
        }
    }
    
    public int defaultDirector() throws SQLException {  
        return getAndAddPersonId("Unknown director");        
    }
    
    @Override
    public Person saveOrUpdate(Person person) throws SQLException {
        if (findOneWithName(person.getName()) == null) {
            return save(person);
        }
        return update(person);
    }
    
    private Person save(Person person) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Person (name) values (?)");

        stmt.setString(1, person.getName());
        
        stmt.execute();
        stmt.close();
        
        stmt = conn.prepareStatement("SELECT * FROM Person WHERE name = ?");
        
        stmt.setString(1, person.getName());
        
        ResultSet rs = stmt.executeQuery();
        
        if (!rs.next()) {
            return null;
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return person;
    }


    private Person update(Person person) throws SQLException {
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE Person SET name = ? WHERE id = ?)");
        
        stmt.setString(1, person.getName());
        stmt.setInt(2, person.getId());
        
        stmt.executeUpdate();
        
        stmt.close();
        conn.close();
        
        return person;
        
    }
    
}
