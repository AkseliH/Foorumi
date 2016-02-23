package tikape;

import java.sql.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Database db = new Database("jdbc:sqlite:Foorumi.db");
        AlueDao alueDao = new AlueDao(db);
        KeskusteluDao keskusteluDao = new KeskusteluDao(db, alueDao);
        ViestiDao viestiDao = new ViestiDao(db, keskusteluDao);

        for (Alue a : alueDao.findAll()) {
            List<Keskustelu> keskustelut = alueDao.findAllWithin(a.getId());
            System.out.println(a.getNimi());
            for (Keskustelu k : keskustelut) {
                System.out.println("\t" + k.getOtsikko());
                List<Viesti> viestit = keskusteluDao.findAllWithin(k.getId());
                for (Viesti v : viestit) {
                    System.out.println("\t\t" + v.getSisalto() + " " + v.getNimimerkki() + " " + v.getAika());
                }
            }
            System.out.println("--------------");
        }
    }

    /*for (Keskustelu k : keskusteluDao.findAll()) {
     System.out.println("-----------------");
     System.out.println(k.getOtsikko());
     Keskustelu k2 = keskusteluDao.findOne(k.getId());
     for (Viesti v : k2.getViestit()) {
     System.out.println(v.getId() + " " + v.getSisalto() + " " + v.getNimimerkki() + " " + v.getAika());
     }
     }
     System.out.println("\n");

     for (Alue a : alueDao.findAll()) {
     System.out.println("-----------------");
     System.out.println(a.getNimi());
     Alue a2 = alueDao.findOne(a.getId());
     for (Keskustelu k : a2.getKeskustelut()) {
     System.out.println(k.getOtsikko());
     }
     }*/
}
