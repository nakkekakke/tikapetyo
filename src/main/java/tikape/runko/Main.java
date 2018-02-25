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
        
        IMDBReader imdb = new IMDBReader(database);
        
        TitleDao titleDao = new TitleDao(database);
        
        System.out.println("list size: " + titleDao.findActors(2).size());
        
        PersonDao personDao = new PersonDao(database);
        GenreDao genreDao = new GenreDao(database);
        
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

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("testi", "Tervehdys, cunts");
            map.put("feck", "THIS IS a kitten");
            map.put("people", personDao.findAll());
            
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
        
        get("/add", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("titles", titleDao.findAll());
            map.put("genres", genreDao.findAll());
            map.put("people", personDao.findAll());

            return new ModelAndView(map, "lisayssivu");
        }, new ThymeleafTemplateEngine());

        get("/titles/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Title title = titleDao.findOne(Integer.parseInt(req.params("id")));
            map.put("name", title.getName());
            map.put("year", title.getYear());
            try {
                map.put("genre", title.getGenre().getName());
            } catch (Exception e) {
                map.put("genre", "Unknown");
                titleDao.defaultGenre(title.getId());
                System.out.println("GENRE NOT FOUND");
            }
            try {
                map.put("director", title.getDirector().getName());
            } catch (Exception e) {
                map.put("director", "Unknown");
                titleDao.defaultDirector(title.getId());
                System.out.println("DIRECTOR NOT FOUND");
            }
            map.put("length", title.getLength());
            map.put("desc", title.getDescription());
            map.put("delete", "/titles/" + title.getId() + "/delete");
            
            map.put("people", personDao.findAllButDefault());
            map.put("actors", titleDao.findActors(title.getId()));
            map.put("writers", titleDao.findWriters(title.getId()));
            
            map.put("control", "/controlStaff/" + title.getId());
            map.put("addWriter", "/addWriter/" + title.getId());

            return new ModelAndView(map, "title");
        }, new ThymeleafTemplateEngine());
        
        get("/titles/:id/delete", (req, res) -> {
            HashMap map = new HashMap<>();
            
            titleDao.delete(Integer.parseInt(req.params("id")));
            titleDao.removeStaff(Integer.parseInt(req.params("id")));
            
            res.redirect("/");
            return null;
        }, new ThymeleafTemplateEngine());
        
        get("/people/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Person person = personDao.findOne(Integer.parseInt(req.params("id")));
            map.put("name", person.getName());
            map.put("bio", person.getBio());
            map.put("movies", titleDao.findTitlesWithPerson(person.getId()));
            
            map.put("delete", "/people/" + person.getId() + "/delete");
            
            if (Integer.parseInt(req.params("id")) == 1) {
                return new ModelAndView(map, "personUnknown");
            }
            
            return new ModelAndView(map, "person");
        }, new ThymeleafTemplateEngine());
        
        get("/people/:id/delete", (req, res) -> {
            
            if (Integer.parseInt(req.params("id")) == 1) {
                res.redirect("/deleteError");
                return null;
            }
            
            personDao.delete(Integer.parseInt(req.params("id")));
            titleDao.removePersonTitle(Integer.parseInt(req.params("id")));
            
            res.redirect("/");
            return null;
        }, new ThymeleafTemplateEngine());
        
        get("/genre/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Genre genre = genreDao.findOne(Integer.parseInt(req.params("id")));
            map.put("name", genre.getName());
            
            map.put("delete", "/genre/" + genre.getId() + "/delete");

            return new ModelAndView(map, "genre");
        }, new ThymeleafTemplateEngine());
        
        get("/genre/:id/delete", (req, res) -> {
            
            if (Integer.parseInt(req.params("id")) == 1) {
                res.redirect("/deleteError");
                return null;
            }
            
            genreDao.delete(Integer.parseInt(req.params("id")));
            
            res.redirect("/");
            return null;
        }, new ThymeleafTemplateEngine());
        
        get("/deleteError", (req, res) -> {
            return new ModelAndView(new HashMap<>(), "deleteError");
        }, new ThymeleafTemplateEngine());
        
        get("/addError", (req, res) -> {
            return new ModelAndView(new HashMap<>(), "addError");
        }, new ThymeleafTemplateEngine());
        
        post("/addMovie", (req, res) -> {
            
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
        
        post("/addPerson", (req, res) -> {
            
            Person person = new Person(2, req.queryParams("name"));
            
            personDao.saveOrUpdate(person);
            
            res.redirect("/add");
            return"";
        });
        
        post("/addIMDB", (req, res) -> {
            
            imdb.addTitleFromIMDB(req.queryParams("link"));
            
            res.redirect("/add");
            return"";
        });
        
        post("/addGenre", (req, res) -> {
            
            Genre genre = new Genre(2, req.queryParams("name"));
            
            genreDao.saveOrUpdate(genre);
            
            res.redirect("/add");
            return"";
        });
        
        post("/controlStaff/:id", (req, res) -> {
            
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
        
        post("/people/:id", (req, res) -> {
            
            Person person = personDao.findOne(Integer.parseInt(req.params("id")));
            
            person.setBio(req.queryParams("bio"));
            
            personDao.saveOrUpdate(person);
            
            res.redirect("/people/" + req.params("id"));
            return"";
        });
    }
}
