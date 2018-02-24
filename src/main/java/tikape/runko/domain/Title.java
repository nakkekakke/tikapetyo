package tikape.runko.domain;

import java.util.List;
import java.util.ArrayList;

public class Title {

    private int id;
    private String nimi;
    private int julkaisuvuosi;
    private int kesto;
    private List<Person> nayttelijalista;
    private List<Person> kirjoittajalista;
    private Person ohjaaja;
    private Genre genre;
    private String kuvaus;

    public Title(int id, String nimi, int julkaisuvuosi, int kesto) {
        this.id = id;
        this.nimi = nimi;
        this.julkaisuvuosi = julkaisuvuosi;
        this.kesto = kesto;
        this.nayttelijalista = new ArrayList<>();
        this.kirjoittajalista = new ArrayList<>();
    }

    public int getPituus() {
        return kesto;
    }

    public void setPituus(int kesto) {
        this.kesto = kesto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public int getJulkaisuvuosi() {
        return julkaisuvuosi;
    }

    public void setJulkaisuvuosi(int julkaisuvuosi) {
        this.julkaisuvuosi = julkaisuvuosi;
    }

    public List<Person> getNayttelijalista() {
        return nayttelijalista;
    }
    
    public List<Person> getKirjoittajalista() {
        return kirjoittajalista;
    }
    
    public Genre getGenre() {
        return genre;
    }
    
    public void setGenre(Genre genre) {
        this.genre = genre;
    }
    
    public Person getOhjaaja() {
        return ohjaaja;
    }
    
    public void setOhjaaja(Person ohjaaja) {
        this.ohjaaja = ohjaaja;
    }
    
    public String getKuvaus() {
        return kuvaus;
    }
    
    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

}
