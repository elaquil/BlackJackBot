import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Scanner;

public class BlackJack {

    boolean finished;
    int playerWin;
    int playerBet;
    Player player1;
    House house1;
    boolean hitAgain;
    private Scanner scan = new Scanner(System.in);
    GuildMessageReceivedEvent event;
    CardManager cardManager = new CardManager();

    public BlackJack(GuildMessageReceivedEvent event, int playerBet) throws EmptyDeckException, IllegalCardException {
        finished = false;
        playerWin = 0;
        player1 = new Player(event);
        house1 = new House(event);
        this.playerBet = playerBet;
        this.event = event;
    }

    public void firstDraw() throws EmptyDeckException, IllegalCardException, SQLException {
        for (int i = 0; i<2; i++){
            player1.draw();
            house1.draw();
        }
        if(house1.score == 21 || player1.score == 21) {
            if (player1.score == 21)
                playerWin = 2;
            finished = true;
            endPrint();
        }
        else
            gamePrint();
        winCheck();
    }

    public void hit() throws EmptyDeckException, IllegalCardException, SQLException {
        player1.draw();
        winCheck();
        if (player1.score>21||player1.score==21)
            endPrint();
        else
            gamePrint();
    }

    public void endGame() throws EmptyDeckException, IllegalCardException, SQLException {
        if (house1.score>=player1.score){
            System.out.println("House hand is stronger! House wins.");
            playerWin = 0;
            endPrint();
            return;
        }
        System.out.println("House hand is weaker than player's! Beginning draw loop!");
        System.out.println("Initial state: "+house1.playerHand.toString()+" Score = "+house1.score);
        while (house1.score<player1.score){
            house1.draw();
            System.out.println("Drawing new card... "+house1.playerHand.toString()+" Score = "+house1.score);
        }
        if(house1.score>21){
            System.out.println("House busted! Ending.");
            playerWin = 1;
            endPrint();
            return;
        }
        if(house1.score>=player1.score){
            System.out.println("House created winning hand! Ending.");
            playerWin = 0;
            endPrint();
            return;
        }
    }

    public void gamePrint(){
        EmbedBuilder gameEmbed = new EmbedBuilder();
        gameEmbed.setColor(Color.BLACK);
        gameEmbed.setThumbnail(event.getAuthor().getAvatarUrl());
        gameEmbed.setTitle(event.getAuthor().getName()+"'s game of BlackJack");
        gameEmbed.setDescription("`!Hit` to hit, `!Stay` to stay, or use the reactions below");
        gameEmbed.addField("**HOUSE**", "["+house1.playerHand.get(0).toString()+", \uD83C\uDCA0 ?]" +
                "\nTheir score is: `"+house1.playerHand.get(0).getVal()+"`", false);
        gameEmbed.addField("**"+event.getAuthor().getName().toUpperCase()+"**", ""+player1.playerHand.toString()+
                "\nYour score is: `"+player1.score+"`", false);
        gameEmbed.setFooter("!BlackJackHelp for help");
        event.getChannel().sendMessage(gameEmbed.build()).queue();
    }

    public void endPrint() throws SQLException {
        System.out.println("Printing end embed!");
        EmbedBuilder endEmbed = new EmbedBuilder();
        DataBaseManager.connect(Manager.DB_PATH);
        if (player1.score>21){
            System.out.println("Detected player bust embed!");
            endEmbed.setColor(Color.RED);
            if(playerBet==0){
                endEmbed.setTitle("Bust! "+event.getAuthor().getName()+" lost!");
            }
            else{
                endEmbed.setTitle("Bust! "+event.getAuthor().getName()+" lost $"+playerBet+"!");
            }
            DataBaseManager.setLosses(event.getAuthor().getId(),(DataBaseManager.getLosses(event.getAuthor().getId())+1));
        }
        else if (playerWin==0){
            System.out.println("Detected player lose embed!");
            endEmbed.setColor(Color.RED);
            if(playerBet==0){
                endEmbed.setTitle(event.getAuthor().getName()+" lost!");
            }
            else{
                endEmbed.setTitle(event.getAuthor().getName()+" lost $"+playerBet+"!");
            }
            DataBaseManager.setLosses(event.getAuthor().getId(),(DataBaseManager.getLosses(event.getAuthor().getId())+1));
        }
        else if(playerWin==1){
            System.out.println("Detected player win embed!");
            endEmbed.setColor(Color.GREEN);
            if(playerBet==0){
                endEmbed.setTitle(event.getAuthor().getName()+" won!");
            }
            else{
                endEmbed.setTitle(event.getAuthor().getName()+" won $"+playerBet+"!");
            }
            DataBaseManager.setMoney(event.getAuthor().getId(),(DataBaseManager.getMoney(event.getAuthor().getId())+(playerBet*2)));
            DataBaseManager.setWins(event.getAuthor().getId(),(DataBaseManager.getWins(event.getAuthor().getId())+1));
        }
        else{
            System.out.println("Detected player BlackJack embed!");
            endEmbed.setColor(Color.GREEN);
            if(playerBet==0){
                endEmbed.setTitle("BlackJack! "+event.getAuthor().getName()+" won!");
            } else{
                endEmbed.setTitle("BlackJack! "+event.getAuthor().getName()+" won $"+((int)playerBet*1.5)+"!");
            }
                DataBaseManager.setMoney(event.getAuthor().getId(), (DataBaseManager.getMoney(event.getAuthor().getId()) + (int) (playerBet + playerBet * 1.5)));
                DataBaseManager.setWins(event.getAuthor().getId(), (DataBaseManager.getWins(event.getAuthor().getId()) + 1));
        }
        endEmbed.setThumbnail(event.getAuthor().getAvatarUrl());
        endEmbed.setDescription("Your new balance is: $"+DataBaseManager.getMoney(event.getAuthor().getId())+
                "\n`!BlackJack BET_AMOUNT` to start a new game!");
        endEmbed.addField("**HOUSE**", ""+house1.playerHand.toString() +
                "\nTheir score is: `"+house1.score+"`", false);
        endEmbed.addField("**"+event.getAuthor().getName().toUpperCase()+"**", ""+player1.playerHand.toString()+
                "\nYour score is: `"+player1.score+"`", false);
        endEmbed.setFooter("!BlackJackHelp for help");
        event.getChannel().sendMessage(endEmbed.build()).queue();
        DataBaseManager.setWinrate(event.getAuthor().getId(),(DataBaseManager.getWins(event.getAuthor().getId())/
                DataBaseManager.getLosses(event.getAuthor().getId())));
        cardManager.reset();
        if (DataBaseManager.getMoney(event.getAuthor().getId())<Manager.MIN_MONEY){
            event.getChannel().sendMessage("Bankrupted! We've reset your money back to $"+Manager.MIN_MONEY).queue();
            DataBaseManager.setMoney(event.getAuthor().getId(),Manager.MIN_MONEY);
        }
        DataBaseManager.disconnect();
    }

    public void winCheck(){
        if (player1.score==21 || house1.score>21){
            System.out.println("Player wins!");
            playerWin = player1.score==21 ? 2 : 1;
            finished = true;
            return;
        }
        if (house1.score==21){
            System.out.println("House wins!");
            finished = true;
            return;
        }
        if (player1.score>21){
            System.out.println("Bust! House wins!");
            finished = true;
            return;
        }
        return;
    }

}
