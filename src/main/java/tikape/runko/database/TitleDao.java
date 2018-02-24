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

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Title WHERE Title.id = " + key + ";");
        if (!stmt.execute()) {
            System.out.println("QUERY WAS NOT EXECUTED!");
        }

        stmt.close();
        conn.close();

    }
      
    public Person findDirector(int title) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement s = conn.prepareStatement("SELECT * FROM Person WHERE Person.id = Title.id "
                + "and Title.id = " + title + ";");

        ResultSet r = s.executeQuery();

        Person director = null;
        
        if (r.next()) {

            director = new Person(r.getInt("id"), r.getString("name"));
            director.setBio(r.getString("bio"));

        }

        r.close();
        s.close();
        conn.close();
        
        return director;
        
    }
    public List<Person> findActors(int title) throws SQLException {

        ArrayList<Person> persons = new ArrayList<>();

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Person, Title, ActorTitle "
                + "where ActorTitle.title_id = Title.id "
                + "and ActorTitle.actor_id = Person.id "
                + "and Title.id = " + title + ";");

        ResultSet results = stmt.executeQuery();

        while (results.next()) {

            Person newPerson = new Person(results.getInt("id"), results.getString("name"));
            newPerson.setBio(results.getString("bio"));

            persons.add(newPerson);

        }

        stmt.close();
        conn.close();

        return persons;

    }
    public List<Person> findWriters(int title) throws SQLException {

        ArrayList<Person> persons = new ArrayList<>();

        Connection conn = database.getConnection(); //Riittääkö ainoastaan writertitle.titleid = title parametri?
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Person, Title, WriterTitle "
                + "where WriterTitle.title_id = Title "
                + "and WriterTitle.writer_id = Person.id "
                + "and Title.id = " + title + ";");

        ResultSet results = stmt.executeQuery();

        while (results.next()) {

            Person newPerson = new Person(results.getInt("id"), results.getString("name"));
            newPerson.setBio(results.getString("bio"));

            persons.add(newPerson);

        }

        stmt.close();
        conn.close();

        return persons;

    } 
    public List<Person> findPersons(int title) throws SQLException {
        
        ArrayList<Person> persons = new ArrayList<>();
        
        persons.add(findDirector(title));
        
        for (Person p : findActors(title)) {
            persons.add(p);
        }
        
        for (Person p : findWriters(title)) {
            persons.add(p);
        }
        
        return persons;
        
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
