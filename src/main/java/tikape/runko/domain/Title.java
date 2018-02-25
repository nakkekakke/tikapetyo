package tikape.runko.domain;

import java.util.List;
import java.util.ArrayList;

public class Title {

    private int id;
    private Person director;
    private Genre genre;
    private String name;
    private int year;
    private String description;
    private int length;
    private List<Person> actors;
    private List<Person> writers;
    

    public Title(int id, String nimi, int julkaisuvuosi, int kesto, String description) {
        this.id = id;
        this.name = nimi;
        this.year = julkaisuvuosi;
        this.length = kesto;
        this.description = description;
        this.actors = new ArrayList<>();
        this.writers = new ArrayList<>();
        
        // Place defaults
        this.genre = new Genre(1, "Unknown");
        this.director = new Person(1, "Unknown");
    }

    public int getLength() {
        return length;
    }

    public void setLength(int kesto) {
        this.length = kesto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nimi) {
        this.name = nimi;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int julkaisuvuosi) {
        this.year = julkaisuvuosi;
    }

    public List<Person> getActors() {
        return actors;
    }
    
    public List<Person> getWriters() {
        return writers;
    }
    
    public Genre getGenre() {
        return genre;
    }
    
    public void setGenre(Genre genre) {
        this.genre = genre;
    }
    
    public Person getDirector() {
        return director;
    }
    
    public void setDirector(Person ohjaaja) {
        this.director = ohjaaja;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String desc) {
        this.description = desc;
    }
}
