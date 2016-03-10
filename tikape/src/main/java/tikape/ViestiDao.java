package tikape;

import java.util.*;
import java.sql.*;

public class ViestiDao implements Dao<Viesti, Integer> {

    private Database database;
    private Dao<Keskustelu, Integer> keskusteluDao;

    public ViestiDao(Database database, Dao<Keskustelu, Integer> keskusteluDao) {
        this.database = database;
        this.keskusteluDao = keskusteluDao;
    }

    @Override
    public Viesti findOne(Integer key) throws SQLException {
        Connection con = database.getConnection();
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
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti");
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

        rs.close();
        stmt.close();
        connection.close();

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

        Connection con = database.getConnection();
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

        rs.close();
        stmt.close();
        con.close();

        return viestit;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();

        PreparedStatement statement = connection.prepareStatement("DELETE * FROM Viesti WHERE viestiid = ?");
        statement.setObject(1, key);
        statement.executeUpdate();

        statement.close();
        connection.close();
    }

    public void insert(Integer keskusteluId, String sisalto, Timestamp aika, String nimimerkki) throws SQLException {
        Connection connection = database.getConnection();

        if (database.isPostgre()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Viesti "
                    + "(keskusteluid, sisalto, aika, nimimerkki) VALUES (?, ?, to_timestamp(?), ?)");
            statement.setObject(1, keskusteluId);
            statement.setObject(2, sisalto);
            statement.setObject(3, aika.getTime() / 1000);
            statement.setObject(4, nimimerkki);
            statement.executeUpdate();

            statement.close();
            connection.close();
        } else {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Viesti "
                    + "(keskusteluid, sisalto, aika, nimimerkki) VALUES (?, ?, ?, ?)");
            statement.setObject(1, keskusteluId);
            statement.setObject(2, sisalto);
            statement.setObject(3, aika.getTime());
            statement.setObject(4, nimimerkki);
            statement.executeUpdate();

            statement.close();
            connection.close();
        }

    }
}
