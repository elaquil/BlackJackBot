import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseManager {

    static Connection dataBase = null;

    public static void connect(String path) {
        try {
            // db parameters
            // create a connection to the database
            dataBase = DriverManager.getConnection(path);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void disconnect() throws SQLException {
        dataBase.close();
        System.out.println("SQLite has disconnected from database");
    }

    public static int getMoney(String userId) {
        int money = 0;
        try {
            money = dataBase.createStatement().executeQuery("SELECT money FROM players WHERE uid = '" + userId + "'").getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return money;
    }

    public static void setMoney(String userId, int value) {
        try {
            dataBase.createStatement().execute("UPDATE players SET money = " + value + " WHERE uid = '" + userId + "'");
            System.out.println("SQL insertion (setMoney) successful");
        } catch (SQLException e) {
            System.out.println("Failed to set money! Is the UID valid?");
        }
    }

    public static int getWinrate(String userId) {
        int winrate = 0;
        try {
            winrate = dataBase.createStatement().executeQuery("SELECT winrate FROM players WHERE uid = '" + userId + "'").getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return winrate;
    }

    public static void setWinrate(String userId, int value) {
        try {
            dataBase.createStatement().execute("UPDATE players SET winrate = " + value + " WHERE uid = '" + userId + "'");
            System.out.println("SQL insertion (setWinrate) successful");
        } catch (SQLException e) {
            System.out.println("Failed to set winrate! Is the UID valid?");
        }
    }

    public static int getWins(String userId) {
        int wins = 0;
        try {
            wins = dataBase.createStatement().executeQuery("SELECT wins FROM players WHERE uid = '" + userId + "'").getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return wins;
    }

    public static void setWins(String userId, int value) {
        try {
            dataBase.createStatement().execute("UPDATE players SET wins = " + value + " WHERE uid = '" + userId + "'");
            System.out.println("SQL insertion (setWins) successful");
        } catch (SQLException e) {
            System.out.println("Failed to set wins! Is the UID valid?");
        }
    }

    public static int getLosses(String userId) {
        int losses = 0;
        try {
            losses = dataBase.createStatement().executeQuery("SELECT losses FROM players WHERE uid = '" + userId + "'").getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return losses;
    }

    public static void setLosses(String userId, int value) {
        try {
            dataBase.createStatement().execute("UPDATE players SET losses = " + value + " WHERE uid = '" + userId + "'");
            System.out.println("SQL insertion (setLosses) successful");
        } catch (SQLException e) {
            System.out.println("Failed to set losses! Is the UID valid?");
        }
    }

    public static boolean isUser(String userId) throws SQLException {
        int exists = dataBase.createStatement().executeQuery("SELECT EXISTS (SELECT uid FROM players WHERE uid = '" + userId + "' )").getInt(1);
        return 1 == exists;
    }

    public static void createNewPlayer(String userId) throws SQLException {
        dataBase.createStatement().execute("INSERT INTO players(money, uid, winrate," +
                " wins, losses) VALUES(100, '" + userId + "', 0, 0, 0)");
    }

}
