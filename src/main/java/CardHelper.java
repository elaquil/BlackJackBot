import java.awt.*;

public class CardHelper {

    public static String getIcon(int index){
        switch(index-1){
            case 0: return "\uD83C\uDCD1";
            case 1: return "\uD83C\uDCD2";
            case 2: return "\uD83C\uDCD3";
            case 3: return "\uD83C\uDCD4";
            case 4: return "\uD83C\uDCD5";
            case 5: return "\uD83C\uDCD6";
            case 6: return "\uD83C\uDCD7";
            case 7: return "\uD83C\uDCD8";
            case 8: return "\uD83C\uDCD9";
            case 9: return "\uD83C\uDCDA";
            case 10: return "\uD83C\uDCDB";
            case 11: return "\uD83C\uDCDD";
            case 12: return "\uD83C\uDCDE";
            case 13: return "\uD83C\uDCC1";
            case 14: return "\uD83C\uDCC2";
            case 15: return "\uD83C\uDCC3";
            case 16: return "\uD83C\uDCC4";
            case 17: return "\uD83C\uDCC5";
            case 18: return "\uD83C\uDCC6";
            case 19: return "\uD83C\uDCC7";
            case 20: return "\uD83C\uDCC8";
            case 21: return "\uD83C\uDCC9";
            case 22: return "\uD83C\uDCCA";
            case 23: return "\uD83C\uDCCB";
            case 24: return "\uD83C\uDCCD";
            case 25: return "\uD83C\uDCCE";
            case 26: return "\uD83C\uDCB1";
            case 27: return "\uD83C\uDCB2";
            case 28: return "\uD83C\uDCB3";
            case 29: return "\uD83C\uDCB4";
            case 30: return "\uD83C\uDCB5";
            case 31: return "\uD83C\uDCB6";
            case 32: return "\uD83C\uDCB7";
            case 33: return "\uD83C\uDCB8";
            case 34: return "\uD83C\uDCB9";
            case 35: return "\uD83C\uDCBA";
            case 36: return "\uD83C\uDCBB";
            case 37: return "\uD83C\uDCBD";
            case 38: return "\uD83C\uDCBE";
            case 39: return "\uD83C\uDCA1";
            case 40: return "\uD83C\uDCA2";
            case 41: return "\uD83C\uDCA3";
            case 42: return "\uD83C\uDCA4";
            case 43: return "\uD83C\uDCA5";
            case 44: return "\uD83C\uDCA6";
            case 45: return "\uD83C\uDCA7";
            case 46: return "\uD83C\uDCA8";
            case 47: return "\uD83C\uDCA9";
            case 48: return "\uD83C\uDCAA";
            case 49: return "\uD83C\uDCAB";
            case 50: return "\uD83C\uDCAD";
            case 51: return "\uD83C\uDCAE";
            default: return null;
        }
    }

    public static Color getColor(int index){
        if (Card.whatSuit(index) == 1 || Card.whatSuit(index) == 2)
            return Color.red;
        else
            return Color.black;
    }


}
