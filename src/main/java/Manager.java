import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

public class Manager {

    public static final String DB_PATH = "jdbc:sqlite:src/main/resources/blackJackDB.db";
    public static final int MIN_MONEY = 10;
    static Scanner scan = new Scanner(System.in);
    int userBet = 0;
    public static HashMap<String, BlackJack> gameHash = new HashMap<String, BlackJack>();

    public static void main(String[] args) throws EmptyDeckException, IllegalCardException, SQLException {
        DataBaseManager.connect(DB_PATH);
        System.out.println("What's your name?");
        String name = scan.next();
        System.out.println(DataBaseManager.isUser(name));
        if(!DataBaseManager.isUser(name)){
            DataBaseManager.createNewPlayer(name);
        }
        if (!gameHash.containsKey(name)){
            //gameHash.put(name, new BlackJack(new GuildMessageReceivedEvent(0));
        }
        System.out.println("You have: $"+DataBaseManager.getMoney(name)+
                "\nHow much would you like to bet?");
        //gameManager();
        BlackJack currentGame = gameHash.get(name);
        currentGame.firstDraw();
        gameHash.remove(name);
        System.exit(1);
    }

}
