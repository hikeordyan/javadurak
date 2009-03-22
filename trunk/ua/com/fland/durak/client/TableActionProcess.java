package ua.com.fland.durak.client;

import com.caucho.hessian.client.HessianProxyFactory;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: maxim
 * Date: Feb 1, 2009
 * Time: 8:18:00 PM
 */
public class TableActionProcess {
    private static final Logger logger = Logger.getLogger(TableActionProcess.class);

    private JFrame mainFrame;
    /*private List<JToggleButton> firstPLCardButtons;
    private List<JToggleButton> cardsOnTableButtons;*/
    private List<CardDesc> cardsOnTableValues;
    private List<CardDesc> firstPLCardsValues;

    private ActiveCardsDesc activeCardsDesc;

    private int wasSelected;
    private int selectedCard;
    private int tramp;

    private byte gameType;

    private String serverID;
    private byte plName;

    private boolean tableUpdated;

    //private FramesExchanger exchanger;

    private final static byte LEADING = 0;
    private final static byte BEATING_OFF = 1;

    //exchanger statuses
    /*private final static byte GETTING_LAST_MOVE = 0;
    private final static byte TABLE_CHANGED = 1;*/

    /**
     * Hessian factory for Hessian connection
     */
    private HessianProxyFactory factory;
    /**
     * Interface for Hessian connection
     */
    private GameServer gameServer;

    public TableActionProcess(ActiveCardsDesc activeCardsDesc, int selectedCard, String serverID, byte plName) {
        /*firstPLCardButtons = new ArrayList<JToggleButton>();
        cardsOnTableButtons = new ArrayList<JToggleButton>();*/
        initConnection();

        cardsOnTableValues = new ArrayList<CardDesc>();
        firstPLCardsValues = new ArrayList<CardDesc>();
        tramp = 3;

        this.activeCardsDesc = activeCardsDesc;
        this.selectedCard = selectedCard;
        this.serverID = serverID;
        this.plName = plName;

        formCardValues(activeCardsDesc);
    }

    private void formCardValues(ActiveCardsDesc activeCardsDesc) {
        this.activeCardsDesc = activeCardsDesc;
        CardDesc tempCardDesc;
        //forming cardsOnTableValues
        cardsOnTableValues = new ArrayList<CardDesc>();
        logger.debug("cardsOnTable.size() " + activeCardsDesc.cardsOnTable.size());
        for (int i = 0; i < activeCardsDesc.cardsOnTable.size(); i++) {
            tempCardDesc = new CardDesc();
            tempCardDesc.cardSuit = activeCardsDesc.cardsOnTable.get(i);
            ++i;
            tempCardDesc.cardValue = activeCardsDesc.cardsOnTable.get(i);
            cardsOnTableValues.add(tempCardDesc);
        }

        //forming firstPLCardsValues
        firstPLCardsValues = new ArrayList<CardDesc>();
        for (int i = 0; i < activeCardsDesc.firstPLCards.size(); i++) {
            tempCardDesc = new CardDesc();
            tempCardDesc.cardSuit = activeCardsDesc.firstPLCards.get(i);
            ++i;
            tempCardDesc.cardValue = activeCardsDesc.firstPLCards.get(i);
            firstPLCardsValues.add(tempCardDesc);
        }

        //test
        /*for (int i = 0; i < firstPLCardsValues.size(); i++) {
            logger.debug(firstPLCardsValues.get(i).cardSuit);
            logger.debug(firstPLCardsValues.get(i).cardValue);
        }*/
    }

    public ActiveCardsDesc cardButtonPressed(int cardNum, ActiveCardsDesc activeCardsDesc, byte gameType) {
        this.activeCardsDesc = activeCardsDesc;
        this.gameType = gameType;
        formCardValues(this.activeCardsDesc);
        tableUpdated = false;

        if (cardNum == selectedCard) {
            switch (gameType) {
                case BEATING_OFF:
                    processBeatOff(cardNum);
                    break;
                case LEADING:
                    processLead(cardNum);
                    break;
                default:
                    logger.error("Unexpected value of gameType: " + gameType);
            }
            this.activeCardsDesc.selectedCard = 0;
            selectedCard = 0;
        } else {
            //setting other selected card
            this.activeCardsDesc.selectedCard = cardNum;
            selectedCard = cardNum;
        }
        return this.activeCardsDesc;
    }

    private void processBeatOff(int cardNum) {
        if (firstPLCardsValues.get(cardNum).cardSuit == tramp) {
            if (cardsOnTableValues.get(cardsOnTableValues.size() - 1).cardSuit == tramp) {
                if (cardsOnTableValues.get(cardsOnTableValues.size() - 1).cardValue < firstPLCardsValues.get(cardNum).cardValue) {
                    processSuccessTurn(cardNum);
                }
            } else {
                processSuccessTurn(cardNum);
            }
        } else {
            if ((cardsOnTableValues.get(cardsOnTableValues.size() - 1).cardSuit == firstPLCardsValues.get(cardNum).cardSuit) &
                    cardsOnTableValues.get(cardsOnTableValues.size() - 1).cardValue < firstPLCardsValues.get(cardNum).cardValue) {
                processSuccessTurn(cardNum);
            }
        }

    }

    private void updateActiveCardsDesc() {
        //forming new firstPLCards
        activeCardsDesc.firstPLCards = new ArrayList<Integer>();
        for (int i = 0; i < firstPLCardsValues.size(); i++) {
            activeCardsDesc.firstPLCards.add(firstPLCardsValues.get(i).cardSuit);
            activeCardsDesc.firstPLCards.add(firstPLCardsValues.get(i).cardValue);
        }
        logger.debug(activeCardsDesc.firstPLCards);

        //forming new cardsOnTable
        activeCardsDesc.cardsOnTable = new ArrayList<Integer>();
        for (int i = 0; i < cardsOnTableValues.size(); i++) {
            activeCardsDesc.cardsOnTable.add(cardsOnTableValues.get(i).cardSuit);
            activeCardsDesc.cardsOnTable.add(cardsOnTableValues.get(i).cardValue);
        }
        logger.debug(activeCardsDesc.cardsOnTable);
    }

    private void processSuccessTurn(int cardNum) {
        cardsOnTableValues.add(firstPLCardsValues.get(cardNum));
        firstPLCardsValues.remove(cardNum);
        updateActiveCardsDesc();

        gameServer.setLastMove(serverID, plName, cardNum);
        tableUpdated = true;
    }

    private void processLead(int cardNum) {
        boolean stopSearch = false;
        logger.debug("cardsOnTableValues.size() " + cardsOnTableValues.size());
        if (cardsOnTableValues.size() == 0) {
            processSuccessTurn(cardNum);
        } else {
            for (int i = 0; i < cardsOnTableValues.size(); i++) {
                if (cardsOnTableValues.get(i).cardValue == firstPLCardsValues.get(cardNum).cardValue & !stopSearch) {
                    processSuccessTurn(cardNum);
                    stopSearch = true;
                }
            }
        }

    }

    public boolean isTableUpdated() {
        return tableUpdated;
    }

    //TODO make something with this terrible initConnection in many classes
    /**
     * Initializing connection, which is using Hessian
     */
    private void initConnection() {
        String url = "http://81.22.135.175:8080/gameServer";

        factory = new HessianProxyFactory();
        try {
            gameServer = (GameServer) factory.create(GameServer.class, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
