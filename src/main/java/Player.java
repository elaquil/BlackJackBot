import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;

public class Player {

    int score;
    ArrayList<Card> playerHand;
    boolean bust;
    GuildMessageReceivedEvent event;

    public Player(GuildMessageReceivedEvent event){
        this.event = event;
        score = 0;
        bust = false;
        playerHand = new ArrayList<Card>();
    }

    public void draw() throws EmptyDeckException, IllegalCardException {
        playerHand.add(new Card(event));
        score += (playerHand.get(currentHandIndex())).getVal();
        bust = checkBust();
    }

    public boolean getBust(){
        return bust;
    }

    public int currentHandIndex(){
        return playerHand.size()-1;
    }

    public boolean checkBust(){
        if (score>21){
            for(Card card : playerHand){
                if (card.aceRule()) {
                    score -= 10;
                }
                if (score<=21){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
