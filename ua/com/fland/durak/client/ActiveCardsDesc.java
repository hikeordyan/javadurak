package ua.com.fland.durak.client;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: maxim
 * Date: Jan 30, 2009
 * Time: 9:47:10 PM
 */
public class ActiveCardsDesc implements Serializable {
    public int secondPLCardsNum;
    public List<Integer> cardsOnTable;
    public List<Integer> firstPLCards;
    public int selectedCard;
    public boolean timeOutReached;
    public boolean endOfGameReached;

    public ActiveCardsDesc() {
        cardsOnTable = new ArrayList<Integer>();
        firstPLCards = new ArrayList<Integer>();
        timeOutReached = false;
        endOfGameReached = false;
    }
}
