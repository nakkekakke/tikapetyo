package tikape.runko;

import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.GenreDao;
import tikape.runko.database.PersonDao;
import tikape.runko.database.TitleDao;
import tikape.runko.domain.Genre;
import tikape.runko.domain.IMDBReader;
import tikape.runko.domain.Person;
import tikape.runko.domain.Title;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:imbd.db");
        database.checkDatabaseValidity();

        IMDBReader imdb = new IMDBReader(database);
        
        TitleDao titleDao = new TitleDao(database);
        
        System.out.println("list size: " + titleDao.findActors(2).size());
        
        PersonDao personDao = new PersonDao(database);
        GenreDao genreDao = new GenreDao(database);
        
        // Reset tool
        get("/resetDatabase", (req, res) -> {
            
            database.resetDatabase(false);
            
            res.redirect("/");
            return null;
        }, new ThymeleafTemplateEngine());
        
        get("/resetDatabase/hard", (req, res) -> {
            
            database.resetDatabase(true);
            
            res.redirect("/");
            return null;
        }, new ThymeleafTemplateEngine());
        
        get("/addTemplates/:amount", (req, res) -> {
            System.out.println(req.params("amount"));
            System.out.println("asd");
            imdb.addTemplateMovies(Integer.parseInt(req.params("amount")));
            
            res.redirect("/");
            return null;
        }, new ThymeleafTemplateEngine());

        
        // Main page
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            
            // Featured movie
            List<Title> titles = titleDao.findAll();
            if (titles.isEmpty()) {
                map.put("kohdeNimi", "Movie list empty.");
                map.put("kohde", "/");
            } else {
                int rnd = titles.get((int)(Math.random()*titles.size())).getId();
                String kohde = "/titles/" + rnd;
                map.put("kohde", kohde);
                String kohdeNimi = titleDao.findOne(rnd).getName();
                map.put("kohdeNimi", kohdeNimi);
            }
            
            
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        // Add stuff to Database
        get("/add", (req, res) -> {
            HashMap map = new HashMap<>();
            
            map.put("titles", titleDao.findAll());
            map.put("genres", genreDao.findAll());
            map.put("people", personDao.findAll());

            return new ModelAndView(map, "lisayssivu");
        }, new ThymeleafTemplateEngine());

        
        // Movie page
        get("/titles/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Title title = titleDao.findOne(Integer.parseInt(req.params("id")));
            
            // Check genre validity
            try {
                map.put("genre", title.getGenre().getName());
            } catch (Exception e) {
                map.put("genre", "Unknown");
                titleDao.defaultGenre(title.getId());
                System.out.println("GENRE NOT FOUND");
            }
            
            // Check director validity
            try {
                map.put("director", title.getDirector().getName());
            } catch (Exception e) {
                map.put("director", "Unknown");
                titleDao.defaultDirector(title.getId());
                System.out.println("DIRECTOR NOT FOUND");
            }
            
            // Other variables
            map.put("name", title.getName());
            map.put("year", title.getYear());
            map.put("length", title.getLength());
            map.put("desc", title.getDescription());
            map.put("delete", "/titles/" + title.getId() + "/delete");
            
            map.put("people", personDao.findAllButDefault());
            map.put("actors", titleDao.findActors(title.getId()));
            map.put("writers", titleDao.findWriters(title.getId()));
            
            map.put("control", "/controlStaff/" + title.getId());
            
            
            return new ModelAndView(map, "title");
        }, new ThymeleafTemplateEngine());
        
        // Delete movie
        get("/titles/:id/delete", (req, res) -> {
            
            titleDao.delete(Integer.parseInt(req.params("id")));
            titleDao.removeStaff(Integer.parseInt(req.params("id")));
            
            res.redirect("/");
            return null;
        }, new ThymeleafTemplateEngine());
        
        
        // Person page
        get("/people/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Person person = personDao.findOne(Integer.parseInt(req.params("id")));
            
            map.put("name", person.getName());
            map.put("bio", person.getBio());
            map.put("movies", titleDao.findTitlesWithPerson(person.getId()));
            map.put("delete", "/people/" + person.getId() + "/delete");
            
            // Default person page
            if (Integer.parseInt(req.params("id")) == 1) {
                System.out.println("Giving unknown page");
                return new ModelAndView(map, "personUnknown");
            }
            System.out.println("Giving normal page");
            return new ModelAndView(map, "person");
        }, new ThymeleafTemplateEngine());
        
        // Delete person
        get("/people/:id/delete", (req, res) -> {
            
            // Default person cannot be deleted
            if (Integer.parseInt(req.params("id")) == 1) {
                res.redirect("/deleteError");
                return null;
            }
            
            personDao.delete(Integer.parseInt(req.params("id")));
            titleDao.removePersonTitle(Integer.parseInt(req.params("id")));
            
            res.redirect("/");
            return null;
        }, new ThymeleafTemplateEngine());
        
        
        // Genre page
        get("/genre/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Genre genre = genreDao.findOne(Integer.parseInt(req.params("id")));
            
            map.put("name", genre.getName());
            map.put("delete", "/genre/" + genre.getId() + "/delete");
            
            // Default genre page
            if (Integer.parseInt(req.params("id")) == 1) {
                return new ModelAndView(map, "genreUnknown");
            }

            return new ModelAndView(map, "genre");
        }, new ThymeleafTemplateEngine());
        
        // Delete genre
        get("/genre/:id/delete", (req, res) -> {
            
            // Default genre cannot be deleted
            if (Integer.parseInt(req.params("id")) == 1) {
                res.redirect("/deleteError");
                return null;
            }
            
            genreDao.delete(Integer.parseInt(req.params("id")));
            
            res.redirect("/");
            return null;
        }, new ThymeleafTemplateEngine());
        
        
        // Add movie - command
        post("/addMovie", (req, res) -> {
            
            // Makes sure inputted values are valid
            try {
                int year = Integer.parseInt(req.queryParams("year"));
            } catch (Exception e) {
                res.redirect("/addError");
                return"";
            }
            try {
                int length = Integer.parseInt(req.queryParams("length"));
            } catch (Exception e) {
                res.redirect("/addError");
                return"";
            }
            if (req.queryParams("name").isEmpty()) {
                res.redirect("/addError");
                return"";
            }
            if (req.queryParams("year").isEmpty()) {
                res.redirect("/addError");
                return"";
            }
            if (req.queryParams("length").isEmpty()) {
                res.redirect("/addError");
                return"";
            }
            // Description can be empty
            
            
            Title title = new Title(
                    1, 
                    req.queryParams("name"), 
                    Integer.parseInt(req.queryParams("year")), 
                    Integer.parseInt(req.queryParams("length")), 
                    req.queryParams("description"));
            title.setDirector(personDao.findOneWithName(req.queryParams("directorDrop")));
            title.setGenre(genreDao.findOneWithName(req.queryParams("genreDrop")));
            
            titleDao.saveOrUpdate(title);
            
            res.redirect("/titles/" + titleDao.findOneWithName(req.queryParams("name")).getId());
            return"";
        });
        
        // Add information from IMBD site
        post("/addIMDB", (req, res) -> {
            
            imdb.addTitleFromIMDB(req.queryParams("link"));
            
            res.redirect("/add");
            return"";
        });
        
        // Add person - command
        post("/addPerson", (req, res) -> {
            
            // Check if name is empty
            if (req.queryParams("name").isEmpty()) {
                res.redirect("/addError");
                return"";
            }
            
            Person person = new Person(2, req.queryParams("name"));
            
            personDao.saveOrUpdate(person);
            
            res.redirect("/add");
            return"";
        });
        
        // Add genre - command
        post("/addGenre", (req, res) -> {
            
            // Check if name is empty
            if (req.queryParams("name").isEmpty()) {
                res.redirect("/addError");
                return"";
            }
            
            Genre genre = new Genre(2, req.queryParams("name"));
            
            genreDao.saveOrUpdate(genre);
            
            res.redirect("/add");
            return"";
        });
        
        // Add/remove actors and writers
        post("/controlStaff/:id", (req, res) -> {
            
            // Choose action based on which button was pressed
            if (req.queryParams("button").equals("Add actor")) {
                titleDao.addActor(Integer.parseInt(req.params("id")), personDao.findOneWithName(req.queryParams("peopleDrop")).getId());
            } else if (req.queryParams("button").equals("Remove actor")) {
                titleDao.removeActor(Integer.parseInt(req.params("id")), personDao.findOneWithName(req.queryParams("peopleDrop")).getId());
            } else if (req.queryParams("button").equals("Add writer")) {
                titleDao.addWriter(Integer.parseInt(req.params("id")), personDao.findOneWithName(req.queryParams("peopleDrop")).getId());
            } else {
                titleDao.removeWriter(Integer.parseInt(req.params("id")), personDao.findOneWithName(req.queryParams("peopleDrop")).getId());
            }
            
            res.redirect("/titles/" + req.params("id"));
            return"";
        });
        
        
        // Edit person biograph
        post("/people/:id", (req, res) -> {
            
            Person person = personDao.findOne(Integer.parseInt(req.params("id")));
            
            person.setBio(req.queryParams("bio"));
            
            personDao.saveOrUpdate(person);
            
            res.redirect("/people/" + req.params("id"));
            return"";
        });
        
        
        // Errors
        get("/deleteError", (req, res) -> {
            return new ModelAndView(new HashMap<>(), "deleteError");
        }, new ThymeleafTemplateEngine());
        
        get("/addError", (req, res) -> {
            return new ModelAndView(new HashMap<>(), "addError");
        }, new ThymeleafTemplateEngine());
    }
}
