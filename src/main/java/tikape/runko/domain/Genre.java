package tikape.runko.domain;

public class Genre {
    private int id;
    private String nimi;
    
    public Genre (int id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return nimi;
    }

    public void setName(String nimi) {
        this.nimi = nimi;
    }
}
