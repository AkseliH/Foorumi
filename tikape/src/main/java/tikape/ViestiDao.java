package tikape;

import java.util.*;
import java.sql.*;

public class ViestiDao implements Dao<Viesti, Integer> {

    private Database db;
    private Dao<Keskustelu, Integer> keskusteluDao;

    public ViestiDao(Database db, Dao<Keskustelu, Integer> keskusteluDao) {
        this.db = db;
        this.keskusteluDao = keskusteluDao;
    }

    @Override
    public Viesti findOne(Integer key) throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Viesti WHERE viestiid = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer viestiId = rs.getInt("viestiid");
        Integer keskusteluId = rs.getInt("keskusteluid");
        String sisalto = rs.getString("sisalto");
        String nimimerkki = rs.getString("nimimerkki");
        Timestamp aika = rs.getTimestamp("aika");

        Viesti v = new Viesti(viestiId, sisalto, nimimerkki, aika);

        rs.close();
        stmt.close();
        con.close();

        v.setKeskustelu(this.keskusteluDao.findOne(keskusteluId));
        return v;
    }

    @Override
    public List<Viesti> findAll() throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Viesti");
        ResultSet rs = stmt.executeQuery();

        Map<Integer, List<Viesti>> keskustelunViestit = new HashMap<>();

        List<Viesti> viestit = new ArrayList<>();

        while (rs.next()) {

            Integer viestiId = rs.getInt("viestiid");
            String sisalto = rs.getString("sisalto");
            String nimimerkki = rs.getString("nimimerkki");
            Timestamp aika = rs.getTimestamp("aika");

            Viesti v = new Viesti(viestiId, sisalto, nimimerkki, aika);
            viestit.add(v);

            Integer keskusteluId = rs.getInt("keskusteluid");

            if (!keskustelunViestit.containsKey(keskusteluId)) {
                keskustelunViestit.put(keskusteluId, new ArrayList<>());
            }
            keskustelunViestit.get(keskusteluId).add(v);
        }

        rs.close();
        stmt.close();
        con.close();

        for (Keskustelu keskustelu : this.keskusteluDao.findAllIn(keskustelunViestit.keySet())) {
            for (Viesti viesti : keskustelunViestit.get(keskustelu.getId())) {
                viesti.setKeskustelu(keskustelu);
            }
        }

        return viestit;
    }

    @Override
    public List<Viesti> findAllIn(Collection<Integer> keys) throws SQLException {
        if (keys.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder muuttujat = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            muuttujat.append(", ?");
        }

        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Viesti WHERE viestiid IN (" + muuttujat + ")");
        int laskuri = 1;
        for (Integer key : keys) {
            stmt.setObject(laskuri, key);
            laskuri++;
        }

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
