package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.TitleDao;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:imbd.db");

        TitleDao titleDao = new TitleDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("testi", "Tervehdys, cunts");
            map.put("feck", "THIS IS a kitten");
            String kohde;
            int rnd = (int)(Math.random()*2);
            if (rnd == 0) {
                kohde = "/testi";
            } else {
                kohde = "/titles";
            }
            //System.out.println(kohde);
            map.put("kohde", kohde);

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/testi", (req, res) -> {
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
            map.put("titles", titleDao.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "titles");
        }, new ThymeleafTemplateEngine());
    }
}
