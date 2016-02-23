 package tikape;

import java.util.*;
import java.sql.*;

public class AlueDao implements Dao<Alue, Integer> {

    private Database db;

    public AlueDao(Database db) {
        this.db = db;
    }

    @Override
    public Alue findOne(Integer key) throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Alue WHERE alueid = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer alueId = rs.getInt("alueid");
        String nimi = rs.getString("nimi");

        Alue a = new Alue(alueId, nimi);
        
        stmt = con.prepareStatement("SELECT * FROM Keskustelu WHERE alueid = ?");
        stmt.setObject(1, alueId);
        rs = stmt.executeQuery();
        List<Keskustelu> keskustelut = new ArrayList<>();
        while (rs.next()) {
            Integer keskusteluId = rs.getInt("keskusteluid");
            String otsikko = rs.getString("otsikko");
            if (rs.getInt("alueid") == key) {
                Keskustelu k = new Keskustelu(keskusteluId, otsikko);
                if (!keskustelut.contains(k)) {
                    keskustelut.add(k);
                }
            }
        }

        a.setKeskustelut(keskustelut);
        rs.close();
        stmt.close();
        con.close();
        return a;
    }

    @Override
    public List<Alue> findAll() throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Alue");

        ResultSet rs = stmt.executeQuery();
        List<Alue> alueet = new ArrayList<>();
        while (rs.next()) {
            Integer alueId = rs.getInt("alueid");
            String nimi = rs.getString("nimi");

            alueet.add(new Alue(alueId, nimi));
        }

        rs.close();
        stmt.close();
        con.close();

        return alueet;
    }

    @Override
    public List<Alue> findAllIn(Collection<Integer> keys) throws SQLException {
        if (keys.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder muuttujat = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            muuttujat.append(", ?");
        }

        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Alue WHERE alueid IN (" + muuttujat + ")");
        int laskuri = 1;
        for (Integer key : keys) {
            stmt.setObject(laskuri, key);
            laskuri++;
        }

        ResultSet rs = stmt.executeQuery();
        List<Alue> alueet = new ArrayList<>();
        while (rs.next()) {
            Integer alueId = rs.getInt("alueid");
            String nimi = rs.getString("nimi");
            alueet.add(new Alue(alueId, nimi));
        }

        return alueet;
    }
    
    public List<Keskustelu> findAllWithin(Integer alueId) throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Keskustelu WHERE alueid = ?");
        stmt.setObject(1, alueId);
        ResultSet rs = stmt.executeQuery();
        List<Keskustelu> keskustelut = new ArrayList<>();
        while (rs.next()) {
            Integer keskusteluId = rs.getInt("keskusteluid");
            String otsikko = rs.getString("otsikko");
            keskustelut.add(new Keskustelu(keskusteluId, otsikko));
        }
        return keskustelut;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
}
