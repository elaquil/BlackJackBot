import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.xml.crypto.Data;
import java.awt.*;
import java.sql.SQLException;

public class BotEvent extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) throws NullPointerException {
        if (event.getMember().getUser().isBot())
            return;
        if (event.getMessage().getContentRaw().charAt(0)!='!')
            return;

        String messageTextRaw = event.getMessage().getContentRaw();
        String[] splitMessage = event.getMessage().getContentRaw().split(" ");

        try {
            DataBaseManager.connect(Manager.DB_PATH);
            if(!DataBaseManager.isUser(event.getAuthor().getId())){
                DataBaseManager.createNewPlayer(event.getAuthor().getId());
            }
            if (DataBaseManager.getMoney(event.getAuthor().getId())<Manager.MIN_MONEY){
                DataBaseManager.setMoney(event.getAuthor().getId(),Manager.MIN_MONEY);
            }
            DataBaseManager.disconnect();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }

        if (messageTextRaw.equalsIgnoreCase("!balance")) {
            try {
                getBalance(event);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (messageTextRaw.equalsIgnoreCase("!stats"))  {
            try {
                getStats(event);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (splitMessage[0].equalsIgnoreCase("!blackjack"))    {
            if (Manager.gameHash.containsKey(event.getAuthor().getId()))
                continueGame(event);
            else {
                try {
                    if (splitMessage.length==1)
                        newGame(event, 0);
                    else
                        newGame(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (messageTextRaw.equalsIgnoreCase("!bjdebug")){
            System.out.println("Received !BJDebug message, is this Ismael?");
            System.out.println(event.getAuthor().getId());
            if(event.getAuthor().getIdLong()==105825421123166208L){
                System.out.println("Verified as Ismael");
                event.getChannel().sendMessage("ACTIVE GAMES: "+Manager.gameHash.toString()).queue();
            }
            else{
                System.out.println("Not verified as Ismael :(");
            }
        }

        if (messageTextRaw.equalsIgnoreCase("!hit")){
            try{
                hit(event);
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        if (messageTextRaw.equalsIgnoreCase("!stay")){
            try{
                stay(event);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void getBalance(GuildMessageReceivedEvent event) throws SQLException {
        try {
            DataBaseManager.connect(Manager.DB_PATH);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Database connection failed! Is a connection already open? Perhaps in SQLite?");
            event.getChannel().sendMessage("We're having trouble contacting our player database at the moment." +
                    "It could be under maintenance, please try again later.").queue();
        }
        event.getChannel().sendMessage("Your balance is: $"+DataBaseManager.getMoney
                (event.getAuthor().getId())).queue();
        DataBaseManager.disconnect();
        }

    public void getStats(GuildMessageReceivedEvent event) throws SQLException {
        try {
            DataBaseManager.connect(Manager.DB_PATH);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Database connection failed! Is a connection already open? Perhaps in SQLite?");
            event.getChannel().sendMessage("We're having trouble contacting our player database at the moment." +
                    "It could be under maintenance, please try again later.").queue();
        }
        EmbedBuilder statEmbed = new EmbedBuilder();
        statEmbed.setColor(Color.RED);
        statEmbed.setThumbnail(event.getAuthor().getAvatarUrl());
        statEmbed.setTitle(event.getAuthor().getName()+"'s BlackJack stats");
        statEmbed.setDescription("Stats can not be reset, money will always be reset to"+ Manager.MIN_MONEY +" when bankrupted");
        statEmbed.addField("MONEY", "```$"+DataBaseManager.getMoney(event.getAuthor().getId())+"```", false);
        statEmbed.addField("WINS", "```"+DataBaseManager.getWins(event.getAuthor().getId())+"```", false);
        statEmbed.addField("LOSSES", "```"+DataBaseManager.getLosses(event.getAuthor().getId())+"```", false);
        statEmbed.addField("WINRATE", "```"+((double)DataBaseManager.getWins(event.getAuthor().getId())/DataBaseManager.getLosses(event.getAuthor().getId()))+"```", false);
        statEmbed.setFooter("!BlackJackHelp for help");
        event.getChannel().sendMessage(statEmbed.build()).queue();
        DataBaseManager.disconnect();
    }

    public void newGame(GuildMessageReceivedEvent event) throws EmptyDeckException, IllegalCardException, SQLException {
        int playerBet = 0;
        String[] splitMessage = event.getMessage().getContentRaw().split(" ");
        try {
            playerBet = Integer.parseInt(splitMessage[1]);
        }
        catch(NumberFormatException e){
            System.out.println("Number format exception!");
            System.out.println("Could not parse type 'int' in message: \""+event.getMessage().getContentRaw()+"\"");
            System.out.println("Offending argument: \""+splitMessage[1]+"\"");
            event.getChannel().sendMessage("Please use a value for your bet as the second argument!" +
                    "\nCorrect formatting should look like this: `!BlackJack BET_AMOUNT`" +
                    "\nIf you wish to play for fun, without bets type `!BlackJack` or `!BlackJack 0`").queue();
            return;
        }
        DataBaseManager.connect(Manager.DB_PATH);
        if (playerBet==0){
            newGame(event, 0);
            return;
        }
        if (playerBet<0){
            event.getChannel().sendMessage("Please enter a positive bet!" +
                    "\nThe minimum bet is: $"+Manager.MIN_MONEY+" please bet in increments of 5 from this!" +
                    "\n`$"+(Manager.MIN_MONEY+10)+", $"+(Manager.MIN_MONEY+20)+", $"+(Manager.MIN_MONEY+30)+", etc.`").queue();
            return;
        }
        if (playerBet<Manager.MIN_MONEY || playerBet%10!=0){
            event.getChannel().sendMessage("The minimum bet is: $"+Manager.MIN_MONEY+", please bet in increments of $10 from this!" +
                    "\n`$"+(Manager.MIN_MONEY+10)+", $"+(Manager.MIN_MONEY+20)+", $"+(Manager.MIN_MONEY+30)+", etc.`").queue();
            return;
        }
        try {
            if(!DataBaseManager.isUser(event.getAuthor().getId())){
                DataBaseManager.createNewPlayer(event.getAuthor().getId());
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Does User have enough funds?");
        if (DataBaseManager.getMoney(event.getAuthor().getId())<playerBet){
            System.out.println("User doesn't have enough funds!");
            event.getChannel().sendMessage("You don't have enough funds for this bet!" +
                    "\nYou have `$"+DataBaseManager.getMoney(event.getAuthor().getId())+"`").queue();
            DataBaseManager.disconnect();
            return;
        }
        Manager.gameHash.put(event.getAuthor().getId(), new BlackJack(event, playerBet));
        DataBaseManager.setMoney(event.getAuthor().getId(),(DataBaseManager.getMoney(event.getAuthor().getId())-playerBet));
        DataBaseManager.disconnect();
        Manager.gameHash.get(event.getAuthor().getId()).firstDraw();
        if(Manager.gameHash.get(event.getAuthor().getId()).finished){
            Manager.gameHash.remove(event.getAuthor().getId());
        }
    }

    public void newGame(GuildMessageReceivedEvent event, int playerBet) throws EmptyDeckException, IllegalCardException, SQLException {
        DataBaseManager.connect(Manager.DB_PATH);
        try {
            if(!DataBaseManager.isUser(event.getAuthor().getId())){
                DataBaseManager.createNewPlayer(event.getAuthor().getId());
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        Manager.gameHash.put(event.getAuthor().getId(), new BlackJack(event, playerBet));
        DataBaseManager.disconnect();
        Manager.gameHash.get(event.getAuthor().getId()).firstDraw();
        if(Manager.gameHash.get(event.getAuthor().getId()).finished){
            Manager.gameHash.remove(event.getAuthor().getId());
        }
    }

    public void continueGame(GuildMessageReceivedEvent event){
        try {
            Manager.gameHash.get(event.getAuthor().getId()).gamePrint();
        }
        catch(Exception e){
            Manager.gameHash.remove(event.getAuthor().getId());
            event.getChannel().sendMessage("It appears we had trouble creating your game. Try `!BlackJack` again!");
        }
    }

    public void hit(GuildMessageReceivedEvent event) throws SQLException, EmptyDeckException, IllegalCardException {
        if(!Manager.gameHash.containsKey(event.getAuthor().getId())){
            event.getChannel().sendMessage("You don't have an active game! Try `!BlackJack` first!").queue();
            return;
        }
        BlackJack game = Manager.gameHash.get(event.getAuthor().getId());
        game.hit();
        if(game.finished){
            Manager.gameHash.remove(event.getAuthor().getId());
        }
    }

    public void stay(GuildMessageReceivedEvent event) throws SQLException, EmptyDeckException, IllegalCardException {
        System.out.println("Detected !stay - computing!");
        if(!Manager.gameHash.containsKey(event.getAuthor().getId())){
            System.out.println("Player does not have active game! Aborting.");
            event.getChannel().sendMessage("You don't have an active game! Try `!BlackJack` first!").queue();
            return;
        }
        System.out.println("Found active game, computing final function!");
        Manager.gameHash.get(event.getAuthor().getId()).endGame();
        System.out.println("Final function finished! Removing game from Hashmap.");
        Manager.gameHash.remove(event.getAuthor().getId());
    }
}


