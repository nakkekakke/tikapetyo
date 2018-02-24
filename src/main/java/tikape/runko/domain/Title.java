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
    }

    public int getPituus() {
        return length;
    }

    public void setPituus(int kesto) {
        this.length = kesto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNimi() {
        return name;
    }

    public void setNimi(String nimi) {
        this.name = nimi;
    }

    public int getJulkaisuvuosi() {
        return year;
    }

    public void setJulkaisuvuosi(int julkaisuvuosi) {
        this.year = julkaisuvuosi;
    }

    public List<Person> getNayttelijalista() {
        return actors;
    }
    
    public List<Person> getKirjoittajalista() {
        return writers;
    }
    
    public Genre getGenre() {
        return genre;
    }
    
    public void setGenre(Genre genre) {
        this.genre = genre;
    }
    
    public Person getOhjaaja() {
        return director;
    }
    
    public void setOhjaaja(Person ohjaaja) {
        this.director = ohjaaja;
    }

}
