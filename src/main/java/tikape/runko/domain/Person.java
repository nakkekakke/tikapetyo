package tikape.runko.domain;

public class Person {
    private int id;
    private String nimi;
    private String bio;
    
    public Person(int id, String nimi) {
        this.nimi = nimi;
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
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String newBio) {
        bio = newBio;
    }
    
    public Person defaultPerson() {
        
        //Ehkä tarpeellinen
        //Palauttaa default personin eli jos käyttäjä syöttää vaikka tyhjän nimen yms
        //niin tästä metodista saa sellasen templaten
        
        return null;
        
    }
    
}
