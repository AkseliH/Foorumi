package tikape;

import java.sql.*;
import java.util.*;

public class Alue {

    private Integer alueId;
    private String nimi;
    private int viestienMaara;
    private Timestamp viimeisinViesti;
    private List<Keskustelu> keskustelut;
    private boolean testi;

    public Alue(Integer alueId, String nimi) {
        this.alueId = alueId;
        this.nimi = nimi;
    }

    public Alue(Integer alueId, String nimi, Timestamp viimeisinViesti,
            Integer viestienMaara) {
        this.alueId = alueId;
        this.nimi = nimi;
        this.viimeisinViesti = viimeisinViesti;
        this.viestienMaara = viestienMaara;
    }

    public Alue(Integer alueId, String nimi, Timestamp viimeisinViesti,
            Integer viestienMaara, List<Keskustelu> keskustelut) {
        this.alueId = alueId;
        this.nimi = nimi;
        this.viimeisinViesti = viimeisinViesti;
        this.viestienMaara = viestienMaara;
        this.keskustelut = keskustelut;
    }

    public Integer getAlueId() {
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

    public int getViestienMaara() {
        return viestienMaara;
    }

    public Timestamp getViimeisinViesti() {
        return viimeisinViesti;
    }

    public void setViestienMaara(int viestienMaara) {
        this.viestienMaara = viestienMaara;
    }

    public void setViimeisinViesti(Timestamp viimeisinViesti) {
        this.viimeisinViesti = viimeisinViesti;
    }

    @Override
    public String toString() {
        if (viestienMaara < 1) {
            return " | viestejä: 0";
        }
        return " | viestejä: " + viestienMaara + " | viimeisin: " + viimeisinViesti;
    }

}
