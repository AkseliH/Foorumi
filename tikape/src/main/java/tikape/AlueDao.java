 package tikape;

import java.util.*;
import java.sql.*;

public class AlueDao implements Dao<Alue, Integer> {

    private Database database;

    public AlueDao(Database database) {
        this.database = database;
    }

    @Override
    public Alue findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "SELECT Alue.*, COUNT(Keskustelu.keskusteluid) AS keskustelujenMaara "
                        + "FROM Alue LEFT JOIN Keskustelu ON Keskustelu.alueid = Alue.alueid "
                        + "WHERE Alue.alueid = ?");
        statement.setObject(1, key);

        ResultSet results = statement.executeQuery();
        boolean hasOne = results.next();
        if (!hasOne) {
            return null;
        }

        Integer alueId = results.getInt("alueid");
        String nimi = results.getString("nimi");
        Integer keskustelujenMaara = results.getInt("keskustelujenMaara");

        Alue alue = new Alue(alueId, nimi);
        alue.setKeskustelujenMaara(keskustelujenMaara);
        
        results.close();
        statement.close();
        connection.close();
        
        return alue;
    }
    
    public Alue findOneWithKeskustelut(Integer key, Integer sivu) throws SQLException {
        Alue alue = this.findOne(key);
        
        Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "SELECT *, COUNT(Viesti.viestiid) AS viestienMaara, MAX(Viesti.aika) AS viimeisinViesti "
                    + "FROM Keskustelu LEFT JOIN Viesti ON Viesti.keskusteluid = Keskustelu.keskusteluid "
                    + "WHERE Keskustelu.alueid = ? GROUP BY Keskustelu.keskusteluid ORDER BY viimeisinViesti DESC "
                    + "LIMIT 10 OFFSET ?");
        statement.setObject(1, alue.getAlueId()); 
        statement.setObject(2, (sivu - 1)*10); 
        ResultSet results = statement.executeQuery();
        
        List<Keskustelu> keskustelut = new ArrayList<>();
        
        while (results.next()) {
            Integer keskusteluId = results.getInt("keskusteluid");
            String otsikko = results.getString("otsikko");
            Timestamp viimeisinViesti = results.getTimestamp("viimeisinViesti");
            int viestienMaara = results.getInt("viestienMaara");
            
            Keskustelu keskustelu = new Keskustelu(keskusteluId, otsikko, viimeisinViesti, viestienMaara);
            keskustelu.setAlue(alue);
            
            keskustelut.add(keskustelu);
        }

        alue.setKeskustelut(keskustelut);
        
        results.close();
        statement.close();
        connection.close();
        
        return alue;
    }
    @Override
    public List<Alue> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(""
                + "SELECT *, COUNT(Viesti.viestiid) AS viestienMaara, MAX(Viesti.aika) AS viimeisinViesti "
                + "FROM Alue LEFT JOIN Keskustelu ON Alue.alueid = Keskustelu.alueid "
                + "LEFT JOIN Viesti ON Keskustelu.keskusteluid = Viesti.keskusteluid "
                + "GROUP BY Alue.alueid ORDER BY Alue.nimi ASC LIMIT 50");

        ResultSet results = statement.executeQuery();
        
        List<Alue> alueet = new ArrayList<>();
        
        while (results.next()) {
            Integer alueId = results.getInt("alueid");
            String nimi = results.getString("nimi");
            Timestamp viimeisinViesti = results.getTimestamp("viimeisinViesti");
            int viestienMaara = results.getInt("viestienMaara");
            
            Alue alue = new Alue(alueId, nimi, viimeisinViesti, viestienMaara);
            alueet.add(alue);
        }

        results.close();
        statement.close();
        connection.close();

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

        Connection con = database.getConnection();
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
        
        rs.close();
        stmt.close();
        con.close();

        return alueet;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        
        PreparedStatement statement = connection.prepareStatement("DELETE * FROM Alue WHERE alueid = ?");
        statement.setObject(1, key);
        statement.executeUpdate();
        
        statement.close();
        connection.close();
    }
    
    public void insert(String nimi) throws SQLException {
        Connection connection = database.getConnection();
        
        PreparedStatement statement = connection.prepareStatement("INSERT INTO Alue (nimi) VALUES (?)");
        statement.setObject(1, nimi);
        statement.executeUpdate();
        
        statement.close();
        connection.close(); 
    }
    
    public boolean exists(String nimi) throws SQLException {
        Connection connection = database.getConnection();
        
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Alue WHERE Alue.nimi = ?");
        statement.setObject(1, nimi);
        ResultSet result = statement.executeQuery();
        boolean exists = result.next();
        
        result.close();
        statement.close();
        connection.close();
        
        return exists;
    }
    
}
