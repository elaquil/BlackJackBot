import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import javax.security.auth.login.LoginException;

public class Bot {

    public static void main (String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault("Nzk1MjM1ODg4MjkyMDM2NjI4.X_GbIg.LHwg8JE_IJoLikwBfRBnY6h65hc").build();
        jda.addEventListener(new BotEvent());
    }

}
