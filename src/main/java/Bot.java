import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import javax.security.auth.login.LoginException;

public class Bot {

    public static void main (String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault(System.getenv("TOKEN")).build();
        jda.addEventListener(new BotEvent());
    }

}
