package tikape.runko;

import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
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
            if (titles.size() == 0) {
                map.put("kohdeNimi", "Movie list empty.");
            } else {
                int rnd = (int)(Math.random()*titles.size())+1;
                String kohde = "/titles/" + rnd;
                map.put("kohde", kohde);
                String kohdeNimi = titleDao.findOne(rnd).getNimi();
                map.put("kohdeNimi", kohdeNimi);
            }
            
            
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/movie", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("titles", titleDao.findAll());

            return new ModelAndView(map, "sivu2");
        }, new ThymeleafTemplateEngine());
        
        get("/titles", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("titles", titleDao.findAll());

            return new ModelAndView(map, "sivu1");
        }, new ThymeleafTemplateEngine());

        get("/titles/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Title title = titleDao.findOne(Integer.parseInt(req.params("id")));
            map.put("name", title.getNimi());
            map.put("title", title);

            return new ModelAndView(map, "title");
        }, new ThymeleafTemplateEngine());
    }
}
