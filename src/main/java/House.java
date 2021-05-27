import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;

public class House extends Player {

    public House(GuildMessageReceivedEvent event){
        super(event);
        this.event = event;
        score = 0;
        bust = false;
        playerHand = new ArrayList<Card>();
    }

}
