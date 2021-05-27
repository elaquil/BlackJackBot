import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class Card {

    static final int MAX = 52;
    static final int MIN = 1;
    private int num;
    private String suit;
    private int val;
    private String cardNum;
    private CardManager cardManager;
    EmptyDeckException emptyDeck = new EmptyDeckException("Deck is empty!");
    IllegalCardException illegalCard = new IllegalCardException("Card is not real!");
    GuildMessageReceivedEvent event;

    public Card(GuildMessageReceivedEvent event) throws EmptyDeckException, IllegalCardException {
        this.event = event;
        //System.out.println("\tCreating new card");
        cardManager = Manager.gameHash.get(event.getAuthor().getId()).cardManager;
        if (cardManager.getIndex()>=MAX)
            throw emptyDeck;
        Random rand = new Random();
        int temp;
        int repetitions = 0;
        do{
            temp = (rand.nextInt(MAX) + MIN);
            repetitions++;
            if (repetitions>=52){
                throw emptyDeck;
            }
        } while (isCardInPlay(temp));
        //System.out.println("\tCard value assignment success! " + temp);
        if (temp>MAX||temp<MIN)
            throw illegalCard;
        num = temp;
        suit = buildSuit(num);
        val = buildVal(num);
        cardNum = buildCardNum(num);
        cardManager.insertCard(num);
        //System.out.println("\tCard assignment finished with: "+num+" "+suit+" "+val+" "+cardNum);
    }

    private String buildSuit(int num){
        //System.out.println("\tBuilding suit!");
        return switch (whatSuit(num)) {
            case 0 -> "Club";
            case 1 -> "Diamond";
            case 2 -> "Heart";
            case 3 -> "Spade";
            default -> null;
        };
    }

    private int buildVal(int num){
        //System.out.println("\tBuilding value!");
        num -= (whatSuit(num)*13);
            if (num==1)
                return 11;
            if (num>9)
                return 10;
            else
                return num;
    }

    private String buildCardNum(int num){
        //System.out.println("\tBuilding number!");
        num -= (whatSuit(num)*13);
        return switch (num) {
            case 1 -> "Ace";
            case 11 -> "Jack";
            case 12 -> "Queen";
            case 13 -> "King";
            default -> ("" + num);
        };
    }

    public int getVal(){
        return val;
    }

    public boolean aceRule(){
        if (val == 11) {
            val = 1;
            return true;
        }
        else{
            return false;
        }
    }

    public static int whatSuit(int num) {
        //System.out.println("\tCalling suit detection...");
        if (num<=13){
            return 0;
        }
        if (num>13 && num<=26){
            return 1;
        }
        if (num>26 && num<=39){
            return 2;
        }
        if (num>39 && num<=52){
            return 3;
        }
        else
            return 0;
    }

    private Boolean isCardInPlay(int index) {
        for (int cardCompareIndex : cardManager.cardsInPlay) {
            if (index == cardCompareIndex) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        //System.out.println("\tBuilding toString!");
        return (CardHelper.getIcon(num)+" "+cardNum+" of "+suit.toLowerCase()+"s" );
    }

}
