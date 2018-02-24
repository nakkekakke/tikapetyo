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
import tikape.runko.domain.Person;
import tikape.runko.domain.Title;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:imbd.db");

        TitleDao titleDao = new TitleDao(database);
        PersonDao personDao = new PersonDao(database);
        GenreDao genreDao = new GenreDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("testi", "Tervehdys, cunts");
            map.put("feck", "THIS IS a kitten");
            map.put("people", personDao.findAll());
            
            /*
            String kohde;
            int rnd = (int)(Math.random()*2);
            if (rnd == 0) {
                kohde = "elokuva1";
            } else {
                kohde = "elokuva2";
            }
            
            map.put("kohde", kohde);
            */
            
            List<Title> titles = titleDao.findAll();
            if (titles.isEmpty()) {
                map.put("kohdeNimi", "Movie list empty.");
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

            return new ModelAndView(map, "lisayssivu");
        }, new ThymeleafTemplateEngine());

        get("/titles/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Title title = titleDao.findOne(Integer.parseInt(req.params("id")));
            map.put("name", title.getName());
            map.put("year", title.getYear());
            map.put("genre", title.getGenre().getName());
            map.put("director", title.getDirector().getName());
            map.put("length", title.getLength());
            map.put("desc", title.getDescription());
            map.put("delete", "/titles/" + title.getId() + "/delete");

            return new ModelAndView(map, "title");
        }, new ThymeleafTemplateEngine());
        
        get("/titles/:id/delete", (req, res) -> {
            HashMap map = new HashMap<>();
            Title title = titleDao.findOne(Integer.parseInt(req.params("id")));
            
            titleDao.delete(Integer.parseInt(req.params("id")));
            
            res.redirect("/");
            return null;
        }, new ThymeleafTemplateEngine());
        
        get("/people/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Person person = personDao.findOne(Integer.parseInt(req.params("id")));
            map.put("name", person.getName());
            map.put("bio", person.getBio());
            
            map.put("delete", "/people/" + person.getId() + "/delete");

            return new ModelAndView(map, "person");
        }, new ThymeleafTemplateEngine());
        
        get("/people/:id/delete", (req, res) -> {
            
            if (Integer.parseInt(req.params("id")) == 1) {
                res.redirect("/deleteError");
                return null;
            }
            
            HashMap map = new HashMap<>();
            Person person = personDao.findOne(Integer.parseInt(req.params("id")));
            
            personDao.delete(Integer.parseInt(req.params("id")));
            
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
            
            HashMap map = new HashMap<>();
            Genre genre = genreDao.findOne(Integer.parseInt(req.params("id")));
            
            genreDao.delete(Integer.parseInt(req.params("id")));
            
            res.redirect("/");
            return null;
        }, new ThymeleafTemplateEngine());
        
        get("/deleteError", (req, res) -> {
            HashMap map = new HashMap<>();
            
            return new ModelAndView(map, "deleteError");
        }, new ThymeleafTemplateEngine());
        
        post("/addMovie", (req, res) -> {
            
            Title title = new Title(
                    1, 
                    req.queryParams("name"), 
                    Integer.parseInt(req.queryParams("year")), 
                    Integer.parseInt(req.queryParams("length")), 
                    req.queryParams("description"));
            
            titleDao.saveOrUpdate(title);
            
            res.redirect("/add");
            return"";
        });
        
        post("/addPerson", (req, res) -> {
            
            Person person = new Person(2, req.queryParams("name"));
            
            personDao.saveOrUpdate(person);
            
            res.redirect("/add");
            return"";
        });
        
        post("/addGenre", (req, res) -> {
            
            Genre genre = new Genre(2, req.queryParams("name"));
            
            genreDao.saveOrUpdate(genre);
            
            res.redirect("/add");
            return"";
        });
    }
}
