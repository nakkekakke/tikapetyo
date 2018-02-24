package tikape.runko;

import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.Spark;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.TitleDao;
import tikape.runko.domain.Title;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:imbd.db");

        TitleDao titleDao = new TitleDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("testi", "Tervehdys, cunts");
            map.put("feck", "THIS IS a kitten");
            
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

        get("/movie", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("titles", titleDao.findAll());

            return new ModelAndView(map, "sivu2");
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
            //new ModelAndView(map, "sivu" + title.getId());
        }, new ThymeleafTemplateEngine());
        
        post("/addMovie", (req, res) -> {
            
            Title title = new Title(
                    1, 
                    req.queryParams("name"), 
                    Integer.parseInt(req.queryParams("year")), 
                    Integer.parseInt(req.queryParams("length")), 
                    req.queryParams("description"));
            
            titleDao.saveOrUpdate(title);
            
            String nimi = req.queryParams("name");
            System.out.println(nimi);
            
            res.redirect("/add");
            return"";
        });
        
        post("/addPerson", (req, res) -> {
            /*
            Title title = new Title(
                    1, 
                    req.params("name"), 
                    Integer.parseInt(req.params("year")), 
                    Integer.parseInt(req.params("length")), 
                    req.params("description"));
            */
            //titleDao.saveOrUpdate(title);
            
            String nimi = req.queryParams("name");
            System.out.println(nimi);
            
            res.redirect("/add");
            return"";
        });
    }
}
