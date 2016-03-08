package tikape;

import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws Exception {
        this.databaseAddress = databaseAddress;
        
//        init();
    }

    public Connection getConnection() throws SQLException {
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Throwable t) {
                System.out.println("Error: " + t.getMessage());
                t.printStackTrace();
            }
        }
        
        return DriverManager.getConnection(databaseAddress);
    }
    
    private void init() {
        List<String> lauseet = null;
        if (this.databaseAddress.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
        
        
    }
    
    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä     
        lista.add("CREATE TABLE Alue (alueid SERIAL PRIMARY KEY, nimi varchar(50) UNIQUE NOT NULL);");
                
        lista.add("CREATE TABLE Keskustelu (keskusteluid SERIAL PRIMARY KEY, "
                + "alueid integer NOT NULL, otsikko varchar(100) NOT NULL, FOREIGN KEY (alueid) REFERENCES Alue (alueid));");
                
        lista.add("CREATE TABLE Viesti (viestiid SERIAL PRIMARY KEY, "
                + "keskusteluid integer NOT NULL, sisalto text NOT NULL, "
                + "aika timestamp NOT NULL, nimimerkki varchar(50) NOT NULL, "
                + "FOREIGN KEY (keskusteluid) REFERENCES Keskustelu (keskusteluid));");

        return lista;
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("CREATE TABLE Alue (alueid integer PRIMARY KEY, nimi varchar(50) UNIQUE NOT NULL);");
        lista.add("CREATE TABLE Keskustelu (keskusteluid integer PRIMARY KEY, "
                + "alueid integer NOT NULL, otsikko varchar(100) NOT NULL, FOREIGN KEY (alueid) REFERENCES Alue (alueid));");
        lista.add("CREATE TABLE Viesti (viestiid integer PRIMARY KEY, "
                + "keskusteluid integer NOT NULL, sisalto text NOT NULL, "
                + "aika timestamp NOT NULL, nimimerkki varchar(50) NOT NULL, "
                + "FOREIGN KEY (keskusteluid) REFERENCES Keskustelu (keskusteluid));");

        return lista;
    }
    
    public boolean isPostgre() {
        return this.databaseAddress.contains("postgres");
    }
}
