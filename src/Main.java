import com.google.common.collect.Lists;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        var startTime = System.currentTimeMillis();

        var names = Lists.newArrayList("Mattia", "Marco", "Luca", "Giovanni", "Matteo", "Francesco", "Andrea", "Vincenzo", "Giuseppe", "Loris", "Mario",
        "Riccardo", "Simone", "Luigi", "Lorenzo", "Federico", "Davide", "Daniele", "Alessandro", "Alessio", "Pasquale", "Marika", "Lucia", "Margherita",
        "Federica", "Francesca", "Simona", "Giorgia", "Elisa", "Laura", "Antonietta", "Giuseppina", "Rita", "Tiziana", "Rossella", "Erika", "Sofia", "Nicole",
        "Isabel", "Irene", "Greta", "Giuliana", "Giulia", "Gaia", "Denise");

        var surnames = Lists.newArrayList("Di Matteo", "Rossi", "Bianchi", "Gialli", "Verdi", "Accalio", "Devito", "Palero", "Veron", "Martiri", "Di Campo", "Di Canio",
        "Gentili", "Mastro", "Fasson", "Ferri", "Ramini", "Ranieri", "De Santis", "Forina", "Lusati", "Borini", "Ballina", "Terrici", "Talano");

        var reparti = Lists.newArrayList("Chirurgia", "Cardiologia", "Psicologia", "Odontoiatria", "Ematologia", "Dermatologia", "Podologia");
        var esami = Lists.newArrayList("Laringoscopia", "Broncoscopia", "Esofagoscopia", "Gastroscopia", "Endoscopia", "Gastroduodenale", "Colonscopia", "Endoscopia",
                "Isteroscopia", "Cistoscopia", "Astroscopia", "Laparoscopia", "Mediastinoscopia", "Toracoscopia", "Biopsia");

        var driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);

        System.out.println("Inizializzo la connessione...");
        var connection =  DriverManager.getConnection("jdbc:mysql://localhost", "root", null);

        var statement = connection.createStatement();
        statement.executeUpdate("DROP DATABASE IF EXISTS hospital");
        statement.executeUpdate("CREATE DATABASE hospital");
        System.out.println("Database creato con successo!");

        connection =  DriverManager.getConnection("jdbc:mysql://localhost/hospital", "root", null);

        statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE reparti (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL)");
        System.out.println("Creata la tabella reparti!");

        statement.executeUpdate("CREATE TABLE pazienti (cf VARCHAR(16) PRIMARY KEY, name VARCHAR(255) NOT NULL, surname VARCHAR(255) NOT NULL, sesso ENUM('m', 'f') NOT NULL)");
        System.out.println("Creata la tabella pazienti!");

        statement.executeUpdate("CREATE TABLE medici (cf VARCHAR(16) PRIMARY KEY, name VARCHAR(255) NOT NULL, surname VARCHAR(255) NOT NULL, date VARCHAR(10) NOT NULL, FK_idRep INT NOT NULL, FOREIGN KEY (FK_idRep) REFERENCES reparti(id))");
        System.out.println("Creata la tabella medici!");

        statement.executeUpdate("CREATE TABLE visite (id INT AUTO_INCREMENT PRIMARY KEY, date VARCHAR(10) NOT NULL, esito VARCHAR(255) NOT NULL, FK_cfPaz VARCHAR(16) NOT NULL, FK_cfMed VARCHAR(16) NOT NULL, " +
                "FOREIGN KEY (FK_cfPaz) REFERENCES pazienti(cf), FOREIGN KEY (FK_cfMed) REFERENCES medici(cf))");
        System.out.println("Creata la tabella visite!");

        statement.executeUpdate("CREATE TABLE ricoveri (id INT AUTO_INCREMENT PRIMARY KEY, date VARCHAR(10) NOT NULL, durata INT NOT NULL, FK_idRep INT NOT NULL, FK_cfPaz VARCHAR(16) NOT NULL, " +
                "FOREIGN KEY (FK_cfPaz) REFERENCES pazienti(cf), FOREIGN KEY (FK_idRep) REFERENCES reparti(id))");
        System.out.println("Creata la tabella ricoveri!");

        statement.executeUpdate("CREATE TABLE esami (id INT AUTO_INCREMENT PRIMARY KEY, date VARCHAR(10) NOT NULL, tipo VARCHAR(255) NOT NULL, esito VARCHAR(255) NOT NULL, FK_cfPaz VARCHAR(16) NOT NULL," +
                "FOREIGN KEY (FK_cfPaz) REFERENCES pazienti(cf))");
        System.out.println("Creata la tabella esami!");

        for (String reparto : reparti)
            statement.executeUpdate("INSERT INTO reparti (id, name) VALUES (null, '%s')".formatted(reparto));

        System.out.println("Tabella reparti popolata con successo!");

        for (int i = 0; i < 100; i++) {
            var num = randOf(44);
            var name = names.get(num);
            var surname = surnames.get(randOf(24));
            var sex = num <= 20 ? 'm' : 'f';

            statement.executeUpdate("INSERT INTO pazienti (cf, name, surname, sesso) VALUES ('%s', '%s', '%s', '%s')".formatted(randCf(), name, surname, sex));
        }
        System.out.println("Tabella pazienti popolata con successo!");

        for (int i = 0; i < 25; i++) {
            var name = names.get(randOf(44));
            var surname = surnames.get(randOf(24));

            statement.executeUpdate("INSERT INTO medici (cf, name, surname, date, FK_idRep) VALUES ('%s', '%s', '%s', '%s', '%s')".formatted(randCf(), name, surname, randDate(1965, 2000), (randOf(6) + 1)));
        }
        System.out.println("Tabella medici popolata con successo!");

        for (int i = 0; i < 400; i++) {
            var esito = randOf(2) == 1 ? "POSITIVO" : "NEGATIVO";
            var res = statement.executeQuery("SELECT cf FROM pazienti ORDER BY RAND() LIMIT 1");
            String cfPaz = null;

            while (res.next())
                cfPaz = res.getString("cf");

            res = statement.executeQuery("SELECT cf FROM medici ORDER BY RAND() LIMIT 1");
            String cfMed = null;

            while (res.next())
                cfMed = res.getString("cf");

            statement.executeUpdate("INSERT INTO visite (id, date, esito, FK_cfPAZ, FK_cfMED) VALUES (null, '%s', '%s', '%s', '%s')".formatted(randDate(2015, 2022), esito, cfPaz, cfMed));
        }
        System.out.println("Tabella visite popolata con successo!");

        for (int i = 0; i < 150; i++) {
            var res = statement.executeQuery("SELECT cf FROM pazienti ORDER BY RAND() LIMIT 1");
            String cfPaz = null;

            while (res.next())
                cfPaz = res.getString("cf");

            statement.executeUpdate("INSERT INTO ricoveri (id, date, durata, FK_idREP, FK_cfPAZ) VALUES (null, '%s', '%s', '%s', '%s')".formatted(randDate(2015, 2022), randOf(30), randOf(6) + 1, cfPaz));
        }
        System.out.println("Tabella ricoveri popolata con successo!");

        for (int i = 0; i < 600; i++) {
            var esito = randOf(2) == 1 ? "POSITIVO" : "NEGATIVO";
            var res = statement.executeQuery("SELECT cf FROM pazienti ORDER BY RAND() LIMIT 1");
            String cfPaz = null;

            while (res.next())
                cfPaz = res.getString("cf");

            statement.executeUpdate("INSERT INTO esami (id, date, tipo, esito, FK_cfPAZ) VALUES (null, '%s', '%s', '%s', '%s')".formatted(randDate(2015, 2022), esami.get(randOf(14)) , esito, cfPaz));
        }
        System.out.println("Tabella esami popolata con successo!");

        statement.close();
        System.out.printf("Esecuzione terminata in %sms.%n", (System.currentTimeMillis() - startTime));
    }

    private static int randOf(int max) {
        Random rand = new Random();
        return rand.nextInt(max + 1);
    }

    private static String randCf() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "").substring(0, 16);
    }

    private static String randDate(int yearStart, int yearEnd) {
        Calendar start = Calendar.getInstance();
        start.set(yearStart, Calendar.JANUARY, 1);
        long startMillis = start.getTimeInMillis();

        Calendar end = Calendar.getInstance();
        end.set(yearEnd, Calendar.DECEMBER, 31);
        long endMillis = end.getTimeInMillis();

        Random rand = new Random();
        long randomMillisSinceEpoch = startMillis + (long)(rand.nextDouble()*(endMillis - startMillis));
        Calendar randomDate = Calendar.getInstance();
        randomDate.setTimeInMillis(randomMillisSinceEpoch);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(randomDate.getTime());
    }
}