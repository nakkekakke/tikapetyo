package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Person;


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
        
        // If empty
        if (!rs.next()) {
            rs.close();
            stmt.close();
            conn.close();
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
        
        // If empty
        if (!rs.next()) {
            rs.close();
            stmt.close();
            conn.close();
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
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Person Group By Person.name");
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
    
    
    // Does not include the default 'Unknown' person
    public List<Person> findAllButDefault() throws SQLException {
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Person WHERE id > 1 Group By Person.name");
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
        
        // Does the person exist?
        if (findOne(key) == null) {
            System.out.println("QUERY WAS NOT EXECUTED!");
        }

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Person WHERE Person.id = " + key);
        
        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }
    
    
    // - Add explanation -
    public int getAndAddPersonId(String user) throws SQLException {

        String query = "Select Person.id from Person where Person.name = '" + user + "';";

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
            
            PreparedStatement userStmt = c.prepareStatement("INSERT INTO Person (name, bio) values ('" + user + "', 'Default bio');");
            userStmt.execute();
            c.close();
            return getAndAddPersonId(user);
        }
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
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Person (name,bio) values (?,?)");

        stmt.setString(1, person.getName());
        stmt.setString(2, person.getBio());
        
        stmt.execute();
        stmt.close();
        
        stmt = conn.prepareStatement("SELECT * FROM Person WHERE name = ?");
        
        stmt.setString(1, person.getName());
        
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
        
        return person;
    }
    
    
    public List<Person> searchPersonsByName(String name) throws SQLException {
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM Person WHERE Person.name LIKE '%" + name + "%' Group By Person.name;");
        ResultSet rs = stmt.executeQuery();

        List<Person> persons = new ArrayList<>();

        while (rs.next()) {
            Person title = new Person(rs.getInt("id"), rs.getString("name"));

            persons.add(title);
        }

        rs.close();
        stmt.close();
        conn.close();

        return persons;
    }


    private Person update(Person person) throws SQLException {
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE Person SET name = ?, bio = ? WHERE id = ?");
        
        stmt.setString(1, person.getName());
        stmt.setString(2, person.getBio());
        stmt.setInt(3, findOneWithName(person.getName()).getId());
        
        stmt.executeUpdate();
        stmt.close();
        conn.close();
        
        return person;
    }
    
    /*
    ////////// Check this, maybe delete? //////////
    public int defaultDirector() throws SQLException {  
        return getAndAddPersonId("Unknown director");        
    }
    */
}
