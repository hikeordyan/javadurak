package ua.com.fland.durak.client;

/**
 * Created by IntelliJ IDEA.<br>
 * User: maxim<br>
 * Date: May 7, 2009<br>
 * Time: 3:41:13 PM<br>
 * <p/>
 * <p/>
 * DukarGameClient - client of on-line durak game<br>
 * Copyright (C) 2009  Maxim Bondarenko<br>
 * <p/>
 * This program is free software: you can redistribute it and/or modify<br>
 * it under the terms of the GNU General Public License as published by<br>
 * the Free Software Foundation, either version 3 of the License, or<br>
 * (at your option) any later version.<br>
 * <br>
 * This program is distributed in the hope that it will be useful,<br>
 * but WITHOUT ANY WARRANTY; without even the implied warranty of<br>
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<br>
 * GNU General Public License for more details.<br>
 * <br>
 * You should have received a copy of the GNU General Public License<br>
 * along with this program.  If not, see <a href="http://www.gnu.org/licenses/">GNU Licenses</a><br>
 */
public class TableActionProcess {
    /*private static final Logger logger = Logger.getLogger(TableActionProcess.class);

    private List<CardDesc> cardsOnTableValues;
    private List<CardDesc> firstPLCardsValues;

    private ActiveCardsDesc activeCardsDesc;

    //private int wasSelected;
    private int selectedCard;
    private int tramp;

    private String serverID;
    private byte plName;

    private boolean tableUpdated;

    //private FramesExchanger exchanger;

    private final static byte LEADING = 0;
    private final static byte BEATING_OFF = 1;

    private final static String url = "http://81.22.135.175:8080/gameServer";

    /**
     * Interface for Hessian connection
     */
    /*private GameServer gameServer;

    public TableActionProcess(ActiveCardsDesc activeCardsDesc, int selectedCard, String serverID, byte plName) {
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
        logger.debug("cardsOnTable.size() " + activeCardsDesc.getCardsOnTable().size());
        for (int i = 0; i < activeCardsDesc.getCardsOnTable().size(); i++) {
            tempCardDesc = new CardDesc();
            tempCardDesc.cardSuit = activeCardsDesc.getCardsOnTable().get(i);
            ++i;
            tempCardDesc.cardValue = activeCardsDesc.getCardsOnTable().get(i);
            cardsOnTableValues.add(tempCardDesc);
        }

        //forming firstPLCardsValues
        firstPLCardsValues = new ArrayList<CardDesc>();
        for (int i = 0; i < activeCardsDesc.getFirstPLCards().size(); i++) {
            tempCardDesc = new CardDesc();
            tempCardDesc.cardSuit = activeCardsDesc.getFirstPLCards().get(i);
            ++i;
            tempCardDesc.cardValue = activeCardsDesc.getFirstPLCards().get(i);
            firstPLCardsValues.add(tempCardDesc);
        }

        //test
        /*for (int i = 0; i < firstPLCardsValues.size(); i++) {
            logger.debug(firstPLCardsValues.get(i).cardSuit);
            logger.debug(firstPLCardsValues.get(i).cardValue);
        }*/
    /*}

    public ActiveCardsDesc cardButtonPressed(int cardNum, ActiveCardsDesc activeCardsDesc, byte gameType) {
        this.activeCardsDesc = activeCardsDesc;
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
            this.activeCardsDesc.setSelectedCard(0);
            selectedCard = 0;
        } else {
            //setting other selected card
            this.activeCardsDesc.setSelectedCard(cardNum);
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
        activeCardsDesc.setFirstPLCards(new ArrayList<Integer>());
        for (int i = 0; i < firstPLCardsValues.size(); i++) {
            activeCardsDesc.firstPLCards.add(firstPLCardsValues.get(i).cardSuit);
            activeCardsDesc.firstPLCards.add(firstPLCardsValues.get(i).cardValue);
        }
        logger.debug(activeCardsDesc.getFirstPLCards());

        //forming new cardsOnTable
        activeCardsDesc.cardsOnTable = new ArrayList<Integer>();
        for (int i = 0; i < cardsOnTableValues.size(); i++) {
            activeCardsDesc.cardsOnTable.add(cardsOnTableValues.get(i).cardSuit);
            activeCardsDesc.cardsOnTable.add(cardsOnTableValues.get(i).cardValue);
        }
        logger.debug(activeCardsDesc.getCardsOnTable());
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
    /*private void initConnection() {
        //String url = "http://81.22.135.175:8080/gameServer";

        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            gameServer = (GameServer) factory.create(GameServer.class, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }*/
}
