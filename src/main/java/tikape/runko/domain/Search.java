package tikape.runko.domain;

import java.util.ArrayList;
import java.util.List;



public class Search {
    private String type;
    private List<Title> titles;
    private List<Person> people;
    
    public Search() {
        this.type = "Title";
        this.titles = new ArrayList<>();
        this.people = new ArrayList<>();
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    public void setTitles(List<Title> titles) {
        this.titles = titles;
    }
    
    public List<Title> getTitles() {
        return titles;
    }
    
    public void setPeople(List<Person> people) {
        this.people = people;
    }
    
    public List<Person> getPeople() {
        return people;
    }
}
