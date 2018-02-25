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

    private GenreDao genreDao;
    private PersonDao personDao;
    private Database database;

    public TitleDao(Database database) {
        this.database = database;
        genreDao = new GenreDao(database);
        personDao = new PersonDao(database);
    }

    @Override
    public Title findOne(Integer key) throws SQLException {
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Title WHERE Title.id = ?");
        stmt.setObject(1, key);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        Title title = new Title(rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("year"),
                rs.getInt("length"),
                rs.getString("description"));
        
        title.setDirector(personDao.findOne(rs.getInt("director_id")));
        title.setGenre(genreDao.findOne(rs.getInt("genre_id")));

        rs.close();
        stmt.close();
        conn.close();

        return title;
    }

    public Title findOneWithName(String name) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Title WHERE name = ?");
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        Title title = new Title(rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("year"),
                rs.getInt("length"),
                rs.getString("description"));

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
                    rs.getString("name"),
                    rs.getInt("year"),
                    rs.getInt("length"),
                    rs.getString("description"));
            
            title.setDirector(personDao.findOne(rs.getInt("director_id")));
            title.setGenre(genreDao.findOne(rs.getInt("genre_id")));

            titles.add(title);
        }

        rs.close();
        stmt.close();
        conn.close();

        return titles;
    }
    
    public List<Title> findTitlesWithPerson(int person_id) throws SQLException {
        List<Title> list = new ArrayList<>();
        
        Connection conn = database.getConnection();
        
        
        // Actor
        PreparedStatement stmt2 = conn.prepareStatement("SELECT title_id FROM ActorTitle WHERE ActorTitle.actor_id = ?");
        stmt2.setInt(1, person_id);
        
        ResultSet rs2 = stmt2.executeQuery();
        
        while (rs2.next()) {
            list.add(findOne(rs2.getInt("title_id")));
        }
        
        rs2.close();
        stmt2.close();
        
        
        // Director
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Title WHERE Title.director_id = ?");
        stmt.setInt(1, person_id);
        
        ResultSet rs = stmt.executeQuery();
        
        BASE:while (rs.next()) {
            Title title = new Title(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("year"),
                rs.getInt("length"),
                rs.getString("description"));
            
            for(int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().equals(title.getName())) {
                    continue BASE;
                }
            }
            
            list.add(title);
        }
        rs.close();
        stmt.close();
        
        
        // Director
        PreparedStatement stmt3 = conn.prepareStatement("SELECT title_id FROM WriterTitle WHERE WriterTitle.writer_id = ?");
        stmt3.setInt(1, person_id);
        
        ResultSet rs3 = stmt3.executeQuery();
        
        BASE2:while (rs3.next()) {
            Title title = findOne(rs3.getInt("title_id"));
            
            for(int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().equals(title.getName())) {
                    continue BASE2;
                }
            }
            
            list.add(title);
        }
        rs3.close();
        stmt3.close();
        
        conn.close();
        
        return list;
    }
  
    @Override
    public void delete(Integer key) throws SQLException {
        
        if (findOne(key) == null) {
            System.out.println("QUERY WAS NOT EXECUTED!");;
        }

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Title WHERE Title.id = " + key);
        
        stmt.executeUpdate();

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
        PreparedStatement stmt = conn.prepareStatement("SELECT Person.* FROM Person, Title, ActorTitle "
                + "where ActorTitle.title_id = Title.id "
                + "and ActorTitle.actor_id = Person.id "
                + "and Title.id = " + title);

        ResultSet results = stmt.executeQuery();

        while (results.next()) {

            Person newPerson = new Person(results.getInt("id"), results.getString("name"));
            newPerson.setBio(results.getString("bio"));

            persons.add(newPerson);

        }
        results.close();
        stmt.close();
        conn.close();

        return persons;

    }

    public List<Person> findWriters(int title) throws SQLException {

        ArrayList<Person> persons = new ArrayList<>();

        Connection conn = database.getConnection(); //Riittääkö ainoastaan writertitle.titleid = title parametri?
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Person, Title, WriterTitle "
                + "where WriterTitle.title_id = Title.id "
                + "and WriterTitle.writer_id = Person.id "
                + "and Title.id = " + title);

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
    
    public void addWriter(Integer title_id, Integer writer_id) throws SQLException {
        Connection conn = database.getConnection();
        
        PreparedStatement statement = conn.prepareStatement(
                "SELECT (title_id) FROM WriterTitle WHERE title_id = ? AND writer_id = ?");
        
        statement.setInt(1, title_id);
        statement.setInt(2, writer_id);
        
        ResultSet rs = statement.executeQuery();
        
        if (!rs.next()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO WriterTitle (title_id,writer_id) VALUES (?,?)");
        
            stmt.setInt(1, title_id);
            stmt.setInt(2, writer_id);

            stmt.executeUpdate();

            stmt.close();
        }
        
        rs.close();
        statement.close();
        conn.close();
        
    }
    
    public void removeWriter(Integer title_id, Integer writer_id) throws SQLException {
        Connection conn = database.getConnection();
        
        PreparedStatement statement = conn.prepareStatement(
                "DELETE FROM WriterTitle WHERE title_id = " + title_id + " AND writer_id = " + writer_id);
        
        try{
            statement.executeUpdate();
        } catch (SQLException e) {}
        statement.close();
        conn.close();
    }
    
    public void addActor(Integer title_id, Integer actor_id) throws SQLException {
        Connection conn = database.getConnection();
        
        PreparedStatement statement = conn.prepareStatement(
                "SELECT (title_id) FROM ActorTitle WHERE title_id = ? AND actor_id = ?");
        
        statement.setInt(1, title_id);
        statement.setInt(2, actor_id);
        
        ResultSet rs = statement.executeQuery();
        
        if (!rs.next()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO ActorTitle (title_id,actor_id) VALUES (?,?)");
        
            stmt.setInt(1, title_id);
            stmt.setInt(2, actor_id);

            stmt.executeUpdate();

            stmt.close();
        }
        
        rs.close();
        statement.close();
        conn.close();
    }
    
    public void removeActor(Integer title_id, Integer actor_id) throws SQLException {
        Connection conn = database.getConnection();
        
        PreparedStatement statement = conn.prepareStatement(
                "DELETE FROM ActorTitle WHERE title_id = " + title_id + " AND actor_id = " + actor_id);
        
        try{
            statement.executeUpdate();
        } catch (SQLException e) {}
        statement.close();
        conn.close();
    }
    
    public void removeStaff(Integer title_id) throws SQLException {
        Connection conn = database.getConnection();
        
        PreparedStatement statement = conn.prepareStatement(
                "DELETE FROM ActorTitle WHERE title_id = " + title_id);
        
        try{
            statement.executeUpdate();
        } catch (SQLException e) {}
        statement.close();
        
        PreparedStatement statement2 = conn.prepareStatement(
                "DELETE FROM WriterTitle WHERE title_id = " + title_id);
        
        try{
            statement2.executeUpdate();
        } catch (SQLException e) {}
        statement2.close();
        conn.close();
    }
    
    public void removePersonTitle(Integer person_id) throws SQLException {
        Connection conn = database.getConnection();
        
        PreparedStatement statement = conn.prepareStatement(
                "DELETE FROM ActorTitle WHERE actor_id = " + person_id);
        
        try{
            statement.executeUpdate();
        } catch (SQLException e) {}
        statement.close();
        
        PreparedStatement statement2 = conn.prepareStatement(
                "DELETE FROM WriterTitle WHERE writer_id = " + person_id);
        
        try{
            statement2.executeUpdate();
        } catch (SQLException e) {}
        statement2.close();
        conn.close();
    }
    
    public List<Title> searchTitlesByParameter(String parameter, String s) throws SQLException {
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM Title WHERE Title." + parameter + " LIKE '%" + s + "%'");
        ResultSet rs = stmt.executeQuery();

        List<Title> titles = new ArrayList<>();

        while (rs.next()) {
            Title title = new Title(rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("year"),
                    rs.getInt("length"),
                    rs.getString("description"));
            
            title.setDirector(personDao.findOne(rs.getInt("director_id")));
            title.setGenre(genreDao.findOne(rs.getInt("genre_id")));

            titles.add(title);
        }

        rs.close();
        stmt.close();
        conn.close();

        return titles;
        
    }  
    
    @Override
    public Title saveOrUpdate(Title title) throws SQLException {
        if (findOneWithName(title.getName()) == null) {
            return addTitle(title);
        }
        return update(title);
    }
    
    private Title addTitle(Title title) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Title (director_id, genre_id, name, year, description, length) values (?, ?, ?, ?, ?, ?)");
                
        stmt.setInt(1, title.getDirector().getId());
        stmt.setInt(2, title.getGenre().getId());
        stmt.setString(3, title.getName());
        stmt.setInt(4, title.getYear());
        stmt.setString(5, title.getDescription());
        stmt.setInt(6, title.getLength());
        
        stmt.execute();
        stmt.close();
        
        stmt = conn.prepareStatement("SELECT * FROM Title WHERE director_id = ? AND name = ?");
        
        stmt.setInt(1, title.getDirector().getId());
        stmt.setString(2, title.getName());
        
        ResultSet rs = stmt.executeQuery();
        
        if (!rs.next()) {
            return null;
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return title;
    }
    
    private Title update(Title title) throws SQLException {
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE Title SET director_id = ?, genre_id = ?, name = ?, year = ?, description = ?, length = ? WHERE id = ?");
        
        stmt.setInt(1, title.getDirector().getId());
        stmt.setInt(2, title.getGenre().getId());
        stmt.setString(3, title.getName());
        stmt.setInt(4, title.getYear());
        stmt.setString(5, title.getDescription());
        stmt.setInt(6, title.getLength());
        stmt.setInt(6, findOneWithName(title.getName()).getId());
        
        stmt.executeUpdate();
        
        stmt.close();
        conn.close();
        
        return title;
        
    }
    
    public void defaultGenre(Integer id) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE Title SET genre_id = 1 WHERE id = " + id);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }
    
    public void defaultDirector(Integer id) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE Title SET director_id = 1 WHERE id = " + id);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

}
