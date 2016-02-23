package tikape;

import java.sql.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Database db = new Database("jdbc:sqlite:Foorumi.db");
        AlueDao alueDao = new AlueDao(db);
        KeskusteluDao keskusteluDao = new KeskusteluDao(db, alueDao);
        ViestiDao viestiDao = new ViestiDao(db, keskusteluDao);

        for (Alue alue : alueDao.findAll()) {
            System.out.println(alue);
        }
        
        for (Keskustelu keskustelu : alueDao.findOneWithKeskustelut(1).getKeskustelut()) {
            System.out.println(keskustelu);
        }
        
        for (Viesti viesti : keskusteluDao.findOneWithViestit(1).getViestit()) {
            System.out.println(viesti);
        }
        
        
    }
}
