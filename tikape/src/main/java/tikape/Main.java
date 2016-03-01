package tikape;

import java.sql.*;
import java.util.*;
import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:Foorumi.db");
        AlueDao alueDao = new AlueDao(database);
        KeskusteluDao keskusteluDao = new KeskusteluDao(database, alueDao);
        ViestiDao viestiDao = new ViestiDao(database, keskusteluDao);

        for (Alue alue : alueDao.findAll()) {
            System.out.println(alue);
        }
        
        for (Keskustelu keskustelu : alueDao.findOneWithKeskustelut(2).getKeskustelut()) {
            System.out.println(keskustelu);
        }
        
        for (Viesti viesti : keskusteluDao.findOneWithViestit(4).getViestit()) {
            System.out.println(viesti);
        }
        
        
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("teksti", "Keskustelualueet:");
            List alueet = alueDao.findAll();
            map.put("alueet", alueet);

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        get("/alue/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            int alueid = Integer.parseInt(req.params(":id"));
            map.put("alue", alueDao.findOne(alueid).getNimi());
            map.put("keskustelut", alueDao.findOneWithKeskustelut(alueid).getKeskustelut());

            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine()); 
        
        
        get("/keskustelu/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            int keskusteluid = Integer.parseInt(req.params(":id"));
            map.put("keskustelu", keskusteluDao.findOne(keskusteluid).getOtsikko());
            map.put("viestit", keskusteluDao.findOneWithViestit(keskusteluid).getViestit());
            
            return new ModelAndView(map, "keskustelu");
        }, new ThymeleafTemplateEngine());         
    }
}
