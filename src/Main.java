import com.google.common.collect.Lists;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

        statement.executeUpdate("CREATE TABLE esami (id INT AUTO_INCREMENT PRIMARY KEY, tipo VARCHAR(255) NOT NULL, esito VARCHAR(255) NOT NULL)");
        System.out.println("Creata la tabella esami!");

        for (String reparto : reparti) {
            statement.executeUpdate("INSERT INTO reparti (id, name) VALUES ('%s', '%s')".formatted(0, reparto));
        }
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

            statement.executeUpdate("INSERT INTO medici (cf, name, surname, date, FK_idRep) VALUES ('%s', '%s', '%s', '%s', '%s')".formatted(randCf(), name, surname, randDate(), (randOf(6) + 1)));
        }
        System.out.println("Tabella medici popolata con successo!");

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

    private static String randDate() {
        Calendar start = Calendar.getInstance();
        start.set(2010, 0, 1);
        long startMillis = start.getTimeInMillis();

        Calendar end = Calendar.getInstance();
        end.set(2022, 11, 31);
        long endMillis = end.getTimeInMillis();

        Random rand = new Random();
        long randomMillisSinceEpoch = startMillis + (long)(rand.nextDouble()*(endMillis - startMillis));
        Calendar randomDate = Calendar.getInstance();
        randomDate.setTimeInMillis(randomMillisSinceEpoch);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(randomDate.getTime());
    }
}