package tikape;

import java.sql.*;
import java.util.*;

public class Alue {
    private Integer alueId;
    private String nimi;
    private List<Keskustelu> keskustelut;
    
    public Alue(Integer alueId, String nimi) {
        this.alueId = alueId;
        this.nimi = nimi;
        keskustelut = new ArrayList<>();
    }
    
    public Integer getId() {
        return alueId;
    }
    
    public String getNimi() {
        return nimi;
    }
    
    public List<Keskustelu> getKeskustelut() {
        return keskustelut;
    }
    
    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    public void setKeskustelut(List<Keskustelu> keskustelut) {
        this.keskustelut = keskustelut;
    }
} 