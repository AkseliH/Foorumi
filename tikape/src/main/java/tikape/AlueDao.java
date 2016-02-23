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
        
        rs.close();
        stmt.close();
        con.close();
        
        return a;
    }
    
    public Alue findOneWithKeskustelut(Integer key) throws SQLException {
        Alue alue = this.findOne(key);
        
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement(
                "SELECT *, COUNT(Viesti.viestiid) AS viestienMaara, MAX(Viesti.aika) AS viimeisinViesti "
                    + "FROM Keskustelu LEFT JOIN Viesti ON Viesti.keskusteluid = Keskustelu.keskusteluid "
                    + "WHERE Keskustelu.alueid = ? GROUP BY Keskustelu.keskusteluid ORDER BY viimeisinViesti DESC");
        stmt.setObject(1, alue.getAlueId());        
        ResultSet rs = stmt.executeQuery();
        
        List<Keskustelu> keskustelut = new ArrayList<>();
        
        while (rs.next()) {
            Integer keskusteluId = rs.getInt("keskusteluid");
            String otsikko = rs.getString("otsikko");
            Timestamp viimeisinViesti = rs.getTimestamp("viimeisinViesti");
            int viestienMaara = rs.getInt("viestienMaara");
            
            Keskustelu keskustelu = new Keskustelu(keskusteluId, otsikko, viimeisinViesti, viestienMaara);
            keskustelu.setAlue(alue);
            
            keskustelut.add(keskustelu);
        }

        alue.setKeskustelut(keskustelut);
        
        rs.close();
        stmt.close();
        con.close();
        
        return alue;
    }
    @Override
    public List<Alue> findAll() throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement(""
                + "SELECT *, SUM(Viesti.viestiid) AS viestienMaara, MAX(Viesti.aika) AS viimeisinViesti "
                + "FROM Alue LEFT JOIN Keskustelu ON Alue.alueid = Keskustelu.alueid "
                + "LEFT JOIN Viesti ON Keskustelu.keskusteluid = Viesti.keskusteluid "
                + "GROUP BY Alue.alueid ORDER BY viimeisinViesti DESC");

        ResultSet rs = stmt.executeQuery();
        
        List<Alue> alueet = new ArrayList<>();
        
        while (rs.next()) {
            Integer alueId = rs.getInt("alueid");
            String nimi = rs.getString("nimi");
            Timestamp viimeisinViesti = rs.getTimestamp("viimeisinViesti");
            int viestienMaara = rs.getInt("viestienMaara");
            
            Alue alue = new Alue(alueId, nimi, viimeisinViesti, viestienMaara);
            alueet.add(alue);
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

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = db.getConnection();
        
        PreparedStatement stmt = connection.prepareStatement("DELETE * FROM Alue WHERE alueid = ?");
        stmt.setObject(1, key);
        stmt.executeUpdate();
        
        stmt.close();
        connection.close();
    }
    
    public void insert(String nimi) throws SQLException {
        Connection connection = db.getConnection();
        
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Alue (nimi) VALUES (?)");
        stmt.setObject(1, nimi);
        stmt.executeUpdate();
        
        stmt.close();
        connection.close(); 
    }
}
