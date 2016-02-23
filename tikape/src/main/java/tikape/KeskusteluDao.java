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
    public Keskustelu findOne(Integer key) throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Keskustelu WHERE keskusteluid = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer keskusteluId = rs.getInt("keskusteluid");
        Integer alueId = rs.getInt("alueid");
        String otsikko = rs.getString("otsikko");

        Keskustelu k = new Keskustelu(keskusteluId, otsikko);

        k.setAlue(this.alueDao.findOne(alueId));
        /*
        Hakee keskusteluun kytketyt viestit sekä päivittää ne (setViestit).
        */
        stmt = con.prepareStatement("SELECT * FROM Viesti WHERE keskusteluid = ? ORDER BY aika");
        stmt.setObject(1, keskusteluId);
        rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            Integer viestiId = rs.getInt("viestiid");
            String sisalto = rs.getString("sisalto");
            String nimimerkki = rs.getString("nimimerkki");
            Timestamp aika = rs.getTimestamp("aika");
            if (rs.getInt("keskusteluid") == key) {
                Viesti v = new Viesti(viestiId, sisalto, nimimerkki, aika);
                if (!viestit.contains(v)) {
                    viestit.add(v);
                }
            }
        }

        k.setViestit(viestit);
        rs.close();
        stmt.close();
        con.close();
        return k;
    }

    @Override
    public List<Keskustelu> findAll() throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Keskustelu");
        ResultSet rs = stmt.executeQuery();

        Map<Integer, List<Keskustelu>> alueenKeskustelut = new HashMap<>();

        List<Keskustelu> keskustelut = new ArrayList<>();

        while (rs.next()) {

            Integer keskusteluId = rs.getInt("keskusteluid");
            String otsikko = rs.getString("otsikko");

            Keskustelu k = new Keskustelu(keskusteluId, otsikko);
            keskustelut.add(k);
            Integer alueId = rs.getInt("alueid");
            stmt = con.prepareStatement("SELECT * FROM Viesti WHERE keskusteluid = ?");
            stmt.setObject(1, keskusteluId);

            if (!alueenKeskustelut.containsKey(alueId)) {
                alueenKeskustelut.put(alueId, new ArrayList<>());
            }
            alueenKeskustelut.get(alueId).add(k);
        }

        rs.close();
        stmt.close();
        con.close();

        for (Alue alue : this.alueDao.findAllIn(alueenKeskustelut.keySet())) {
            for (Keskustelu keskustelu : alueenKeskustelut.get(alue.getId())) {
                keskustelu.setAlue(alue);
            }
        }

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
    
    public List<Viesti> findAllWithin(Integer keskusteluId) throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Viesti WHERE keskusteluid = ? ORDER BY aika");
        stmt.setObject(1, keskusteluId);
        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            Integer viestiId = rs.getInt("viestiid");
            String sisalto = rs.getString("sisalto");
            String nimimerkki = rs.getString("nimimerkki");
            Timestamp aika = rs.getTimestamp("aika");
            viestit.add(new Viesti(viestiId, sisalto, nimimerkki, aika));
        }
        return viestit;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
}
