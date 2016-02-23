package tikape;

import java.sql.*;
import java.util.*;

public class Keskustelu {
    private Integer keskusteluId;
    private String otsikko;
    private Alue alue;
    private int viestienMaara;
    private Timestamp viimeisinViesti;
    private List<Viesti> viestit;
    
    public Keskustelu(Integer keskusteluId, String otsikko) {
        this.keskusteluId = keskusteluId;
        this.otsikko = otsikko;
    }
    
    public Keskustelu(Integer keskusteluId, String otsikko, Timestamp viimeisinViesti,
                Integer viestienMaara) {
        this.keskusteluId = keskusteluId;
        this.otsikko = otsikko;
        this.viimeisinViesti = viimeisinViesti;
        this.viestienMaara = viestienMaara;
    }
    
    public Keskustelu(Integer keskusteluId, String otsikko, Timestamp viimeisinViesti,
                Integer viestienMaara, List<Viesti> viestit) {
        this.keskusteluId = keskusteluId;
        this.otsikko = otsikko;
        this.viimeisinViesti = viimeisinViesti;
        this.viestit = viestit;
        this.viestienMaara = viestienMaara;
    }
    
    public Integer getKeskusteluId() {
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

    public int getViestienMaara() {
        return viestienMaara;
    }

    public Timestamp getViimeisinViesti() {
        return viimeisinViesti;
    }
    
    @Override
    public String toString() {
        return keskusteluId + " " + otsikko + " " + viestienMaara + " " + viimeisinViesti;
    }
} 