package tikape;

import java.sql.*;
import java.util.*;

public class Keskustelu {
    private Integer keskusteluId;
    private String otsikko;
    private Alue alue;
    private List<Viesti> viestit;
    
    public Keskustelu(Integer keskusteluId, String otsikko) {
        this.keskusteluId = keskusteluId;
        this.otsikko = otsikko;
        viestit = new ArrayList<>();
    }
    
    public Integer getId() {
        return keskusteluId;
    }
    
    public String getOtsikko() {
        return otsikko;
    }
    
    public Alue getAlue() {
        return alue;
    }
    
    public List<Viesti> getViestit() {
        return viestit;
    }
    
    public void setOtsikko(String otsikko) {
        this.otsikko = otsikko;
    }
    
    public void setAlue(Alue alue) {
        this.alue = alue;
    }
    
    public void setViestit(List<Viesti> viestit) {
        this.viestit = viestit;
    }
} 