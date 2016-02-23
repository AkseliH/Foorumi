package tikape;

import java.util.*;
import java.sql.*;

public class KeskusteluDao implements Dao<Keskustelu, Integer> {

    private Database db;
    private Dao<Alue, Integer> alueDao;

    public KeskusteluDao(Database db, Dao<Alue, Integer> alueDao) {
        this.db = db;
        this.alueDao = alueDao;
    }


    
    
    @Override
    public Keskustelu findOne(Integer key) throws SQLException{
        Connection connection = db.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu WHERE keskusteluid = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer keskusteluId = rs.getInt("keskusteluid");
        String otsikko = rs.getString("otsikko");
        Alue alue = alueDao.findOne(rs.getInt("alueid"));

        Keskustelu keskustelu = new Keskustelu(keskusteluId, otsikko);
        keskustelu.setAlue(alue);
        
        return keskustelu;
    }
    
    public Keskustelu findOneWithViestit(Integer key) throws SQLException {
        Keskustelu keskustelu = this.findOne(key);

        Connection connection = db.getConnection();
        
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE keskusteluid = ? ORDER BY aika ASC");
        stmt.setObject(1, keskustelu.getKeskusteluId());
        ResultSet rs = stmt.executeQuery();
        
        List<Viesti> viestit = new ArrayList<>();
        
        while (rs.next()) {
            Integer viestiId = rs.getInt("viestiid");
            String sisalto = rs.getString("sisalto");
            String nimimerkki = rs.getString("nimimerkki");
            Timestamp aika = rs.getTimestamp("aika");
            
            Viesti viesti = new Viesti(viestiId, sisalto, nimimerkki, aika);
            viestit.add(viesti);
        }

        keskustelu.setViestit(viestit);
        
        rs.close();
        stmt.close();
        connection.close();
        
        return keskustelu;
    }

    @Override
    public List<Keskustelu> findAll() throws SQLException {
        Connection connection = db.getConnection();
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

        Connection con = db.getConnection();
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

        return keskustelut;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = db.getConnection();
        
        PreparedStatement stmt = connection.prepareStatement("DELETE * FROM Keskustelu WHERE keskusteluid = ?");
        stmt.setObject(1, key);
        stmt.executeUpdate();
        
        stmt.close();
        connection.close();
    }
    
    public void insert(Integer alueId, String otsikko) throws SQLException {
        Connection connection = db.getConnection();
        
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelu (alueid, otsikko) VALUES (?, ?)");
        stmt.setObject(1, alueId);
        stmt.setObject(2, otsikko);
        stmt.executeUpdate();
        
        stmt.close();
        connection.close(); 
    }
}
