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
            
            Alue alue = alueDao.findOneWithKeskustelut(alueid,1);
            
            map.put("alueid", alueid);
            map.put("alue", alue.getNimi());
            map.put("keskustelut", alue.getKeskustelut());

            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine()); 
        
        
        get("/keskustelu/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            int keskusteluid = Integer.parseInt(req.params(":id"));
            
            Keskustelu keskustelu = keskusteluDao.findOneWithViestit(keskusteluid,1);
            
            map.put("keskusteluid", keskusteluid);
            map.put("alue", keskustelu.getAlue());
            map.put("keskustelu", keskustelu.getOtsikko());
            map.put("viestit", keskustelu.getViestit());
            
            return new ModelAndView(map, "keskustelu");
        }, new ThymeleafTemplateEngine()); 
        
        post("/", (req, res) -> {
            String nimi = req.queryParams("nimi");
            alueDao.insert(nimi);
            
            HashMap map = new HashMap<>();
            map.put("teksti", "Keskustelualueet:");
            List alueet = alueDao.findAll();
            map.put("alueet", alueet);

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        post("/alue/:id", (req, res) -> {
            int alueid = Integer.parseInt(req.params(":id"));
            
            String otsikko = req.queryParams("otsikko");
            String sisalto = req.queryParams("sisalto");
            String nimimerkki = req.queryParams("nimimerkki");
            Timestamp aika = new Timestamp(System.currentTimeMillis());
            
            keskusteluDao.insertWithViesti(alueid, otsikko, sisalto, aika, nimimerkki);
            
            HashMap map = new HashMap<>();

            Alue alue = alueDao.findOneWithKeskustelut(alueid,1);
            map.put("alueid", alueid);
            map.put("alue", alue.getNimi());
            map.put("keskustelut", alue.getKeskustelut());

            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());
        
        post("/keskustelu/:id", (req, res) -> {
            int keskusteluid = Integer.parseInt(req.params(":id"));
            
            String sisalto = req.queryParams("sisalto");
            String nimimerkki = req.queryParams("nimimerkki");
            Timestamp aika = new Timestamp(System.currentTimeMillis());
            
            viestiDao.insert(keskusteluid, sisalto, aika, nimimerkki);
            
            HashMap map = new HashMap<>();
                        
            Keskustelu keskustelu = keskusteluDao.findOneWithViestit(keskusteluid,1);
            
            map.put("keskusteluid", keskusteluid);
            map.put("alue", keskustelu.getAlue());
            map.put("keskustelu", keskustelu.getOtsikko());
            map.put("viestit", keskustelu.getViestit());
            
            return new ModelAndView(map, "keskustelu");
        }, new ThymeleafTemplateEngine());
    }
}
