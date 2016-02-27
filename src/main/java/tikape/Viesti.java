package tikape;

import java.sql.*;

public class Viesti {
    private Integer viestiId;
    private String sisalto;
    private String nimimerkki;
    private Timestamp aika;
    private Keskustelu keskustelu;
    
    public Viesti(Integer viestiId, String sisalto, String nimimerkki, Timestamp aika) {
        this.viestiId = viestiId;
        this.sisalto = sisalto;
        this.nimimerkki = nimimerkki;
        this.aika = aika;
    }
    
    public Integer getId() {
        return viestiId;
    }
    
    public String getSisalto() {
        return sisalto;
    }
    
    public String getNimimerkki() {
        return nimimerkki;
    }
    
    public Timestamp getAika() {
        return aika;
    }
    
    public Keskustelu getKeskustelu() {
        return keskustelu;
    }
    
    public void setSisalto(String sisalto) {
        this.sisalto = sisalto;
    }
    
    public void setNimimerkki(String nimimerkki) {
        this.nimimerkki = nimimerkki;
    }
    
    public void setAika(Timestamp aika) {
        this.aika = aika;
    }
    
    public void setKeskustelu(Keskustelu keskustelu) {
        this.keskustelu = keskustelu;
    }
    
    @Override
    public String toString() {
        return viestiId + " " + sisalto + " " + nimimerkki + " " + aika;
    }
} 