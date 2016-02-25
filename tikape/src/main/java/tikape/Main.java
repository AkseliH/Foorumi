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
        
        get("/alue/:id", (req, res) -> {
            return " " + req.params(":id");
        });
        
        get("/keskustelutesti", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("keskustelu", keskusteluDao.findOneWithViestit(4));

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
    
        
    }
}
