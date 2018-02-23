package tikape.runko.domain;

import java.util.List;
import java.util.ArrayList;

public class Title {

    private int id;
    private String nimi;
    private int julkaisuvuosi;
    private int kesto;
    private List<Person> henkilolista;

    public Title(int id, String nimi, int julkaisuvuosi, int kesto) {
        this.id = id;
        this.nimi = nimi;
        this.julkaisuvuosi = julkaisuvuosi;
        this.kesto = kesto;
        this.henkilolista = new ArrayList<>();
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

    public List<Person> getHenkilolista() {
        return henkilolista;
    }

}
