import java.util.ArrayList;

public class CardManager {

    private int index = 0;
    ArrayList<Integer> cardsInPlay = new ArrayList<Integer>();
    EmptyDeckException emptyDeck = new EmptyDeckException("Deck is empty!");
    IllegalCardException illegalCard = new IllegalCardException("Card is not real!");

    public int getIndex(){
        return index;
    }

    public void insertCard(int cardIndex) throws EmptyDeckException, IllegalCardException {
        if (cardsInPlay.size()>=Card.MAX)
            throw emptyDeck;
        cardsInPlay.add(cardIndex);
        if (cardsInPlay.get(index)>Card.MAX|| cardsInPlay.get(index)<Card.MIN)
            throw illegalCard;
        index++;
    }

    public void reset(){
        cardsInPlay.clear();
        index = 0;
    }

}
