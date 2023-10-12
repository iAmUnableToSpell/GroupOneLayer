package main.persistence;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

import main.Event;
import main.Event.HandledIllegalValueException;
import main.Participant;

public class DbClient {

    private Connection conn;
    
    public DbClient() throws SQLException {
        final String DB_URL = "swa-database.cxvwky2cwxfy.us-east-1.rds.amazonaws.com";
        final int DB_PORT =3306;
        final String DATABASE = "EventScheduler";
        final String DB_USER = "admin";
        final String DB_PASS;
        Path path = Paths.get(System.getProperty("user.dir"), "passfile.txt");
        try(InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
            Scanner scanner = new Scanner(is);
            DB_PASS = scanner.next();
        } catch(IOException e){
            System.err.println(e.getMessage());
            return;
        } catch(NoSuchElementException e){
            System.err.println("Please fill in " + path.toAbsolutePath() + "with the DB password(Contact Ben if you don't know what it is)");
            System.exit(-1);
            return;
        }
        String CONNECTION_STRING = "jdbc:mysql://%s:%d/%s?user=%s&password=%s".formatted(
                //DB_USER
                //, DB_PASS
                DB_URL
                , DB_PORT
                , DATABASE
                , DB_USER
                , DB_PASS
        );
        conn = DriverManager.getConnection(CONNECTION_STRING);
        this.createDatabase();

    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
    }

    public void createDatabase() throws SQLException {
        conn.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS events (\n"
            + "  id VARCHAR(255) PRIMARY KEY,\n"
            + "  date date,\n"
            + "  time time,\n"
            + "  title varchar(255),\n"
            + "  description varchar(600),\n"
            + "  host_email text\n"
            + ");"
        );
        conn.createStatement().execute(
            "CREATE TABLE IF NOT EXISTS participants (\n"
            + "  id VARCHAR(255) PRIMARY KEY,\n"
            + "  event_id VARCHAR(255) , \n"
            + "  name varchar(600),\n"
            + "  email text,\n"
            + "  FOREIGN KEY (event_id) REFERENCES events(id)\n"
            + ");"
        );
        System.out.println("Database created");
    }

    public List<Event> getEvents() throws SQLException {
        try {
            List<Event> output = new ArrayList<Event>();
            ResultSet results = conn.createStatement().executeQuery(
                    "SELECT * FROM events"
            );
            while (results.next()) {
                output.add(Event.create(
                        results.getString("id"),
                        (new SimpleDateFormat("yyyy-MM-dd")).format(results.getDate("date")),
                        (new SimpleDateFormat("hh:mm a")).format(results.getTime("time")),
                        results.getString("title"),
                        results.getString("description"),
                        results.getString("host_email")
                ));
            }
            return output;
        } catch(HandledIllegalValueException e){
            //unreachable branch
            assert false;
        }
        return Collections.emptyList();
    }

    public List<Participant> getParticipants(String eventId) throws SQLException {
        try {
            List<Participant> output = new ArrayList<Participant>();
            ResultSet results = conn.createStatement().executeQuery(
                    "SELECT * FROM participants WHERE event_id=\"" + eventId + "\""
            );
            while (results.next()) {
                output.add(Participant.create(
                        results.getString("id"),
                        eventId,
                        results.getString("name"),
                        results.getString("email")
                ));
            }
            return output;
        } catch(HandledIllegalValueException e){
            //unreachable
            assert false;
        }
        return Collections.emptyList();
    }

    public void addEvent(Event e) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO events(id, date, time, title, description, host_email) VALUES(?,?,?,?,?,?)"
        );
        pstmt.setString(1, e.uuid().toString());
        pstmt.setDate(2, Date.valueOf(e.eventDateTime().toLocalDate()));
        pstmt.setTime(3, Time.valueOf(e.eventDateTime().toLocalTime()));
        pstmt.setString(4, e.title());
        pstmt.setString(5, e.description());
        pstmt.setString(6, e.hEmail());
        System.out.println(pstmt.toString());
        pstmt.executeUpdate();
    }

    public void addParticipant(Participant p) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO participants(id, event_id, name, email) VALUES(?,?,?,?)"
        );
        pstmt.setString(1, p.uuid().toString());
        pstmt.setString(2, p.eventId().toString());
        pstmt.setString(3, p.name());
        pstmt.setString(4, p.email());

        pstmt.executeUpdate();
    }

    // for testing
    public void deleteAll() throws SQLException {
        conn.createStatement().execute(
            "DELETE FROM events; DELETE FROM participants;"
        );
    }


    public static void main(String[] args) {
        DbClient db;
        try {
            db = new DbClient();
            db.deleteAll();
            Event event = Event.create(
                "1234-12-12",
                "01:23 PM",
                "title",
                "description",
                "title@description.com"
            );
            Participant guy = Participant.create(
                event.uuid().toString(),
                "Luis Segovia",
                "luis@segovia.com"
            );
            db.addEvent(event);
            db.addParticipant(guy);
            System.out.println(db.getEvents());
            System.out.println(db.getParticipants(event.uuid().toString()));
            db.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
