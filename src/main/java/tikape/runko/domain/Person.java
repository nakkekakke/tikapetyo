package tikape.runko.domain;

public class Person {
    private int id;
    private String nimi;
    private String bio;
    
    public Person(int id, String nimi) {
        this.id = id;
        this.nimi = nimi;
        bio = "This person does not have a bio yet.";
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
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String newBio) {
        bio = newBio;
    }
    
    
    /*
    public Person defaultPerson() {
        
        Maybe useful
        Returns the default person if user inputs an empty name
        Use this template if needed
        
        return null;
    }
    */
    
}
