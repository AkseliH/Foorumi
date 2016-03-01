package tikape;

import java.util.*;
import java.sql.*;

public class KeskusteluDao implements Dao<Keskustelu, Integer> {

    private Database database;
    private Dao<Alue, Integer> alueDao;

    public KeskusteluDao(Database database, Dao<Alue, Integer> alueDao) {
        this.database = database;
        this.alueDao = alueDao;
    }


    
    
    @Override
    public Keskustelu findOne(Integer key) throws SQLException{
        Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Keskustelu WHERE keskusteluid = ?");
        statement.setObject(1, key);

        ResultSet results = statement.executeQuery();
        boolean hasOne = results.next();
        if (!hasOne) {
            return null;
        }

        Integer keskusteluId = results.getInt("keskusteluid");
        String otsikko = results.getString("otsikko");
        Alue alue = alueDao.findOne(results.getInt("alueid"));

        Keskustelu keskustelu = new Keskustelu(keskusteluId, otsikko);
        keskustelu.setAlue(alue);
        
        results.close();
        statement.close();
        connection.close();
        
        return keskustelu;
    }
    
    public Keskustelu findOneWithViestit(Integer key, Integer sivu) throws SQLException {
        Keskustelu keskustelu = this.findOne(key);

        Connection connection = database.getConnection();
        
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Viesti WHERE keskusteluid = ? ORDER BY aika ASC "
                + "LIMIT 10 OFFSET ?");
        statement.setObject(1, keskustelu.getKeskusteluId());
        statement.setObject(2, (sivu - 1)*10);
        ResultSet results = statement.executeQuery();
        
        List<Viesti> viestit = new ArrayList<>();
        
        while (results.next()) {
            Integer viestiId = results.getInt("viestiid");
            String sisalto = results.getString("sisalto");
            String nimimerkki = results.getString("nimimerkki");
            Timestamp aika = results.getTimestamp("aika");
            
            Viesti viesti = new Viesti(viestiId, sisalto, nimimerkki, aika);
            viestit.add(viesti);
        }

        keskustelu.setViestit(viestit);
        
        results.close();
        statement.close();
        connection.close();
        
        return keskustelu;
    }

    @Override
    public List<Keskustelu> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu");
        ResultSet rs = stmt.executeQuery();

        List<Keskustelu> keskustelut = new ArrayList<>();

        while (rs.next()) {

            Integer keskusteluId = rs.getInt("keskusteluid");
            String otsikko = rs.getString("otsikko");

            Keskustelu keskustelu = new Keskustelu(keskusteluId, otsikko);
            keskustelut.add(keskustelu);        
        }

        rs.close();
        stmt.close();
        connection.close();

        return keskustelut;
    }

    @Override
    public List<Keskustelu> findAllIn(Collection<Integer> keys) throws SQLException {
        if (keys.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder muuttujat = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            muuttujat.append(", ?");
        }

        Connection con = database.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Keskustelu WHERE keskusteluid IN (" + muuttujat + ")");
        int laskuri = 1;
        for (Integer key : keys) {
            stmt.setObject(laskuri, key);
            laskuri++;
        }

        ResultSet rs = stmt.executeQuery();
        List<Keskustelu> keskustelut = new ArrayList<>();
        while (rs.next()) {
            Integer keskusteluId = rs.getInt("keskusteluid");
            String otsikko = rs.getString("otsikko");
            keskustelut.add(new Keskustelu(keskusteluId, otsikko));
        }
        
        rs.close();
        stmt.close();
        con.close();

        return keskustelut;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        
        PreparedStatement statement = connection.prepareStatement("DELETE * FROM Keskustelu WHERE keskusteluid = ?");
        statement.setObject(1, key);
        statement.executeUpdate();
        
        statement.close();
        connection.close();
    }
    
    public void insert(Integer alueId, String otsikko) throws SQLException {
        Connection connection = database.getConnection();
        
        PreparedStatement statement = connection.prepareStatement("INSERT INTO Keskustelu (alueid, otsikko) VALUES (?, ?)");
        statement.setObject(1, alueId);
        statement.setObject(2, otsikko);
        statement.executeUpdate();
        
        statement.close();
        connection.close(); 
    }
    
    public void insertWithViesti(Integer alueId, String otsikko, String sisalto, Timestamp aika, String nimimerkki) throws SQLException {
        Connection connection = database.getConnection();
        
        PreparedStatement statement = connection.prepareStatement("BEGIN TRANSACTION");
        statement.execute();
        
        try {
            statement = connection.prepareStatement("INSERT INTO Keskustelu (alueid, otsikko) VALUES (?, ?);");
            statement.setObject(1, alueId);
            statement.setObject(2, otsikko);
            statement.executeUpdate();

            statement = connection.prepareStatement("INSERT INTO Viesti (keskusteluid, sisalto, aika, nimimerkki) "
                    + "VALUES ((SELECT last_insert_rowid()), ?, ?, ?);");
            statement.setObject(1, sisalto);
            statement.setObject(2, aika.getTime());
            statement.setObject(3, nimimerkki);
            statement.executeUpdate();
            
            statement = connection.prepareStatement("COMMIT;");
            statement.execute();

        
        } catch (SQLException e) {
            statement = connection.prepareStatement("ROLLBACK;");
            statement.execute();
        }
        
        statement.close();
        connection.close(); 
    }
}
