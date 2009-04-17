package ua.com.fland.durak.client;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: maxim
 * Date: Jan 31, 2009
 * Time: 9:18:20 PM
 */
public class TableVisualization implements Runnable {
    private static final Logger logger = Logger.getLogger(TableVisualization.class);

    private JFrame mainFrame;

    private GridBagConstraints gbc;
    private GridBagLayout gridBag;

    private List<JLabel> firstPLCardLabels;
    private List<JLabel> cardsOnTableLabels;
    private List<JLabel> secondPLCardLabels;
    /*private List<CardDesc> cardsOnTableValues;
    private List<CardDesc> firstPLCardsValues;*/
    private JButton firstFirstRawEmptyCardPlace = new JButton();
    private JButton secondFirstRawEmptyCardPlace = new JButton();
    private JButton firstSecondRawEmptyCardPlace = new JButton();
    private JButton secondSecondRawEmptyCardPlace = new JButton();
    private JButton tableEmptyCardPlace = new JButton();

    private JLabel statusLabel = new JLabel("STATUS: no status yet");

    //private TableActionProcess tableActionProcess;

    private ActiveCardsDesc activeCardsDesc;

    private JButton submitButton;

    private byte gameType;

    private byte plName;
    private String serverID;

    private boolean isTurnWaiting;

    /**
     * Hessian factory for Hessian connection
     */
    private HessianProxyFactory factory;
    /**
     * Interface for Hessian connection
     */
    private GameServer gameServer;

    private FramesExchanger exchanger;

    private final static byte LEADING = 0;
    private final static byte BEATING_OFF = 1;

    private final static byte TAKE_CARDS = -1;
    //private final static byte END_OF_TURN = -2;
    private final static byte END_OF_LEADING = -3;

    /**
     * diff possible values of incoming secondPLCardsNum for got activeCardsDesc
     */
    private final static byte END_OF_GAME = -2;

    //exchanger statuses
    private final static byte END_GAME_REACHED = 6;
    private final static int EXIT_GAME = 4;

    private final static int STATUS_LABEL_LENGTH = 500;

    public TableVisualization(byte plName, String serverID, FramesExchanger exchanger) {
        this.plName = plName;
        this.serverID = serverID;
        this.exchanger = exchanger;

        gbc = new GridBagConstraints();
        gridBag = new GridBagLayout();
        submitButton = new JButton("End of turn");
        activeCardsDesc = new ActiveCardsDesc();
        firstPLCardLabels = new ArrayList<JLabel>();
        secondPLCardLabels = new ArrayList<JLabel>();
        cardsOnTableLabels = new ArrayList<JLabel>();
        statusLabel.setPreferredSize(new Dimension(STATUS_LABEL_LENGTH, 20));
        switch (gameType) {
            case LEADING:
                statusLabel.setText("STATUS: leading, put next card or press End of turn button");
                break;
            case BEATING_OFF:
                statusLabel.setText("STATUS: beating off, put next card or press End of turn button");
                break;
            default:
                logger.error("Unexpected gameType value: " + gameType);
        }
        /*cardsOnTableValues = new ArrayList<CardDesc>();
        firstPLCardsValues = new ArrayList<CardDesc>();*/
        //tableActionProcess = new TableActionProcess(activeCardsDesc, activeCardsDesc.selectedCard, serverID, plName);
        isTurnWaiting = false;

        initConnection();
    }

    public void drawTable(ActiveCardsDesc activeCardsDesc, JFrame mainFrame, byte gameType) {
        this.activeCardsDesc = activeCardsDesc;
        this.mainFrame = mainFrame;
        this.gameType = gameType;
        gbc = new GridBagConstraints();
        gridBag = new GridBagLayout();

        //setting up cards
        removeFrameElements();
        createCardButtons();
        placeCards();
        addCardsToTable();

        this.mainFrame.validate();
        this.mainFrame.repaint();
    }

    private void createCardButtons() {
        //creating firstPL buttons
        firstPLCardLabels = new ArrayList<JLabel>();
        JLabel tempLabel;
        String cardIconName;
        String cardIconsPath = "cardIcons/";
        ImageIcon cardIcon = new ImageIcon();
        Border emptyBorder = BorderFactory.createEmptyBorder();
        for (int i = 0; i < activeCardsDesc.firstPLCards.size(); i++) {
            cardIconName = cardIconsPath + "card" + activeCardsDesc.firstPLCards.get(i) + activeCardsDesc.firstPLCards.get(++i) + ".GIF";
            //++i;

            if (getClass().getResource(cardIconName) != null) {
                cardIcon = new ImageIcon(getClass().getResource(cardIconName));
            } else {
                logger.error("Cann't find file: " + cardIconName);
            }

            tempLabel = new JLabel(cardIcon);
            tempLabel.setBorder(emptyBorder);
            firstPLCardLabels.add(tempLabel);
        }

        //creating secondPLCards
        secondPLCardLabels = new ArrayList<JLabel>();
        cardIconName = cardIconsPath + "cover.gif";
        if (getClass().getResource(cardIconName) != null) {
            cardIcon = new ImageIcon(getClass().getResource(cardIconName));
        } else {
            logger.error("Cann't find file: " + cardIconName);
        }
        for (int i = 0; i < activeCardsDesc.secondPLCardsNum; i++) {
            tempLabel = new JLabel(cardIcon);
            tempLabel.setBorder(emptyBorder);
            secondPLCardLabels.add(tempLabel);
        }

        //creating cardsOnTable
        cardsOnTableLabels = new ArrayList<JLabel>();
        for (int i = 0; i < activeCardsDesc.cardsOnTable.size(); i++) {
            cardIconName = cardIconsPath + "card" + activeCardsDesc.cardsOnTable.get(i) + activeCardsDesc.cardsOnTable.get(++i) + ".GIF";
            //++i;

            if (getClass().getResource(cardIconName) != null) {
                cardIcon = new ImageIcon(getClass().getResource(cardIconName));
            } else {
                logger.error("Cann't find file: " + cardIconName);
            }

            tempLabel = new JLabel(cardIcon);
            tempLabel.setBorder(emptyBorder);
            cardsOnTableLabels.add(tempLabel);
        }

        //creating empty places
        cardIconName = "cardIcons/emptyCardPlace.GIF";
        if (getClass().getResource(cardIconName) != null) {
            cardIcon = new ImageIcon(getClass().getResource(cardIconName));
        } else {
            logger.error("Cann't find file: " + cardIconName);
        }

        firstFirstRawEmptyCardPlace = new JButton(cardIcon);
        firstFirstRawEmptyCardPlace.setBorder(emptyBorder);
        secondFirstRawEmptyCardPlace = new JButton(cardIcon);
        secondFirstRawEmptyCardPlace.setBorder(emptyBorder);
        tableEmptyCardPlace = new JButton(cardIcon);
        tableEmptyCardPlace.setBorder(emptyBorder);
        firstSecondRawEmptyCardPlace = new JButton(cardIcon);
        firstSecondRawEmptyCardPlace.setBorder(emptyBorder);
        secondSecondRawEmptyCardPlace = new JButton(cardIcon);
        secondSecondRawEmptyCardPlace.setBorder(emptyBorder);

        createItemsListeners(firstPLCardLabels, cardsOnTableLabels, activeCardsDesc);
    }

    private void placeCards() {
        //general settings
        gbc.weighty = 1.0;
        gbc.insets = new Insets(4, 4, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.ipadx = 0;

        //placing cards

        int maxCardNumber = firstPLCardLabels.size();
        if (maxCardNumber < activeCardsDesc.secondPLCardsNum) {
            maxCardNumber = activeCardsDesc.secondPLCardsNum;
        }
        if (maxCardNumber < cardsOnTableLabels.size()) {
            maxCardNumber = cardsOnTableLabels.size();
        }

        boolean isSecond = false;
        final int topCardPos = 8;
        final int bottomCardPos = -4;
        int verticalCardPos;

        //sorting
        Map<Integer, Integer> sortedValues = sortFirstPLLabels();
        Map<Integer, Integer> asortedValues = asortedFirstPLLabels();

        for (int i = 0; i < maxCardNumber; i++) {
            //counting isSecond parameter if somebody player cards greater then 12
            if ((i > 11) & (cardsOnTableLabels.size() > i - 12)) {
                if (i == 12) {
                    isSecond = false;
                } else {
                    isSecond = !isSecond;
                }
            }

            //firstPLCards
            if (i < firstPLCardLabels.size()) {
                int reducedTableCards;

                if (i > 11) {
                    gbc.gridy = 4;

                    reducedTableCards = cardsOnTableLabels.size() + 12;
                } else {
                    gbc.gridy = 3;

                    reducedTableCards = cardsOnTableLabels.size();
                }

                if (i == asortedValues.get(activeCardsDesc.selectedCard)) {
                    //if (i == activeCardsDesc.selectedCard) {
                    verticalCardPos = bottomCardPos;
                } else {
                    verticalCardPos = topCardPos;
                }

                if (i <= reducedTableCards) {
                    if (!isSecond) {
                        gbc.insets = new Insets(verticalCardPos, 0, 0, 0);
                    } else {
                        gbc.insets = new Insets(verticalCardPos, 4, 0, 0);
                    }
                } else {
                    gbc.insets = new Insets(verticalCardPos, 8, 0, 0);
                }

                //gridBag.setConstraints(firstPLCardLabels.get(i), gbc);
                gridBag.setConstraints(firstPLCardLabels.get(sortedValues.get(i)), gbc);
            }

            //secondPLCards
            if (i < activeCardsDesc.secondPLCardsNum) {
                int reducedTableCards;

                if (i > 11) {
                    gbc.gridy = 0;
                    reducedTableCards = cardsOnTableLabels.size() + 12;
                } else {
                    gbc.gridy = 1;
                    reducedTableCards = cardsOnTableLabels.size();
                }

                verticalCardPos = bottomCardPos;

                if (i <= reducedTableCards) {
                    if (!isSecond) {
                        gbc.insets = new Insets(verticalCardPos, 0, 0, 0);
                    } else {
                        gbc.insets = new Insets(verticalCardPos, 4, 0, 0);
                    }
                } else {
                    gbc.insets = new Insets(verticalCardPos, 8, 0, 0);
                }

                gridBag.setConstraints(secondPLCardLabels.get(i), gbc);
            }
            //second player second row

            //cardsOnTable
            if (i < cardsOnTableLabels.size()) {
                verticalCardPos = topCardPos;
                gbc.gridy = 2;
                if (!isSecond) {
                    gbc.insets = new Insets(verticalCardPos, 4, 0, 0);
                    //gbc.insets = new Insets(verticalCardPos, 4, 0, -40);
                    isSecond = true;
                } else {
                    gbc.insets = new Insets(verticalCardPos, 4, 0, 10);
                    isSecond = false;
                }
                gridBag.setConstraints(cardsOnTableLabels.get(i), gbc);
            }
        }

        //creating insets for all left buttons
        gbc.insets = new Insets(0, 0, 0, -30);

        //adding submit button
        gbc.gridy = 5;

        gridBag.setConstraints(submitButton, gbc);

        //adding status label
        gbc.insets = new Insets(0, 0, 0, -STATUS_LABEL_LENGTH);
        gbc.gridy = 6;
        gridBag.setConstraints(statusLabel, gbc);

        //adding emptyPlace card or cards
        gbc.insets = new Insets(0, 0, 0, -20);
        if (activeCardsDesc.secondPLCardsNum < 13) {
            gbc.gridy = 0;
            gridBag.setConstraints(secondFirstRawEmptyCardPlace, gbc);
        }

        if (firstPLCardLabels.size() < 13) {
            gbc.gridy = 4;
            gridBag.setConstraints(firstFirstRawEmptyCardPlace, gbc);
        }

        if (cardsOnTableLabels.size() == 0) {
            gbc.gridy = 2;
            gridBag.setConstraints(tableEmptyCardPlace, gbc);
        }

        if (activeCardsDesc.secondPLCardsNum == 0) {
            gbc.gridy = 1;
            gridBag.setConstraints(secondSecondRawEmptyCardPlace, gbc);
        }

        if (firstPLCardLabels.size() == 0) {
            gbc.gridy = 3;
            gridBag.setConstraints(firstSecondRawEmptyCardPlace, gbc);
        }

        mainFrame.setLayout(gridBag);
    }

    private void removeFrameElements() {
        //removing submitButton listeners
        if (submitButton.getActionListeners().length != 0) {
            submitButton.removeActionListener(submitButton.getActionListeners()[0]);
        }

        //removing firstPLCardsListeners
        for (JLabel firstPLCardLabel : firstPLCardLabels) {
            if (firstPLCardLabel.getMouseListeners().length != 0) {
                firstPLCardLabel.removeMouseListener(firstPLCardLabel.getMouseListeners()[0]);
            }
        }

        //removing firstPLCards
        for (JLabel firstPLCardButton : firstPLCardLabels) {
            mainFrame.remove(firstPLCardButton);
        }

        //removing cardsOnTable
        for (JLabel cardsOnTableButton : cardsOnTableLabels) {
            mainFrame.remove(cardsOnTableButton);
        }

        //removing secondPLCards
        for (JLabel secondPlayerCard : secondPLCardLabels) {
            mainFrame.remove(secondPlayerCard);
        }

        //removing submit button
        mainFrame.remove(submitButton);

        //removing emptySpace cards
        if (firstPLCardLabels.size() < 13) {
            mainFrame.remove(firstFirstRawEmptyCardPlace);
        }

        if (secondPLCardLabels.size() < 13) {
            mainFrame.remove(secondFirstRawEmptyCardPlace);
        }

        if (cardsOnTableLabels.size() == 0) {
            mainFrame.remove(tableEmptyCardPlace);
        }
        if (firstPLCardLabels.size() == 0) {
            mainFrame.remove(firstSecondRawEmptyCardPlace);
        }

        if (secondPLCardLabels.size() == 0) {
            mainFrame.remove(secondSecondRawEmptyCardPlace);
        }

        //removing status label
        mainFrame.remove(statusLabel);
        //mainFrame.removeAll();

    }

    private Map<Integer, Integer> asortedFirstPLLabels() {
        Map<Integer, Integer> asortedFirstPlValues = new HashMap<Integer, Integer>();
        Map<Integer, Integer> sortedFirstPlValues = sortFirstPLLabels();

        for (int i = 0; i < activeCardsDesc.firstPLCards.size() / 2; i++) {
            asortedFirstPlValues.put(sortedFirstPlValues.get(i), i);
        }
        return asortedFirstPlValues;
    }

    private Map<Integer, Integer> sortFirstPLLabels() {
        /**
         * key - sorted nomber, value - nomber in lables
         */
        Map<Integer, Integer> sortedValues = new HashMap<Integer, Integer>();

        int firstPlCardsNum = activeCardsDesc.firstPLCards.size() / 2;

        for (int i = 0; i < firstPlCardsNum; i++) {
            sortedValues.put(i, i);
        }

        List<CardDesc> tempCardsDesc = new ArrayList<CardDesc>();
        for (int i = 0; i < activeCardsDesc.firstPLCards.size(); i++) {
            CardDesc tempCardDesc = new CardDesc();
            tempCardDesc.cardSuit = activeCardsDesc.firstPLCards.get(i);
            i++;
            tempCardDesc.cardValue = activeCardsDesc.firstPLCards.get(i);
            tempCardsDesc.add(tempCardDesc);
        }


        for (int i = 0; i < firstPlCardsNum - 2; i++) {
            for (int j = 0; j < firstPlCardsNum - 1; j++) {
                if (tempCardsDesc.get(sortedValues.get(j + 1)).cardSuit > tempCardsDesc.get(sortedValues.get(j)).cardSuit) {
                    int tempValue = sortedValues.get(j);
                    sortedValues.put(j, sortedValues.get(j + 1));
                    sortedValues.put(j + 1, tempValue);
                } else {
                    if ((tempCardsDesc.get(sortedValues.get(j + 1)).cardSuit == tempCardsDesc.get(sortedValues.get(j)).cardSuit) &
                            (tempCardsDesc.get(sortedValues.get(j + 1)).cardValue > tempCardsDesc.get(sortedValues.get(j)).cardValue)) {
                        int tempValue = sortedValues.get(j);
                        sortedValues.put(j, sortedValues.get(j + 1));
                        sortedValues.put(j + 1, tempValue);
                    }
                }
            }
        }
        return sortedValues;
    }

    private void addCardsToTable() {

        //adding firstPLCards
        Map<Integer, Integer> sortedValues = sortFirstPLLabels();
        for (int i = 0; i < firstPLCardLabels.size(); i++) {
            mainFrame.add(firstPLCardLabels.get(sortedValues.get(i)));
        }

        //adding cardsOnTable
        for (JLabel cardsOnTableButton : cardsOnTableLabels) {
            mainFrame.add(cardsOnTableButton);
        }

        //adding secondPLCards
        for (JLabel secondPlayerCard : secondPLCardLabels) {
            mainFrame.add(secondPlayerCard);
        }

        //adding submit button
        mainFrame.add(submitButton);

        //adding emptySpace cards
        if (firstPLCardLabels.size() < 13) {
            mainFrame.add(firstFirstRawEmptyCardPlace);
        }

        if (secondPLCardLabels.size() < 13) {
            mainFrame.add(secondFirstRawEmptyCardPlace);
        }

        if (cardsOnTableLabels.size() == 0) {
            mainFrame.add(tableEmptyCardPlace);
        }
        if (firstPLCardLabels.size() == 0) {
            mainFrame.add(firstSecondRawEmptyCardPlace);
        }

        if (secondPLCardLabels.size() == 0) {
            mainFrame.add(secondSecondRawEmptyCardPlace);
        }

        //adding status label
        mainFrame.add(statusLabel);
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

    private boolean isEndOfGame(ActiveCardsDesc activeCardsDesc) {
        return activeCardsDesc.secondPLCardsNum == END_OF_GAME;
    }

    private void cardButtonPressed(int cardNum) {
        if (!isTurnWaiting) {
            if (activeCardsDesc.selectedCard == cardNum) {
                boolean retryConnection = true;
                ActiveCardsDesc tempActiveCardsDesc = new ActiveCardsDesc();
                while (retryConnection) {
                    try {
                        tempActiveCardsDesc = gameServer.setLastMove(serverID, plName, cardNum);
                        retryConnection = false;
                    } catch (HessianRuntimeException hre) {
                        retryConnection = noConnectionPrevention(hre);
                    }
                }
                logger.debug("tempActiveCardsDesc.secondPLCardsNum" + tempActiveCardsDesc.secondPLCardsNum);
                if (tempActiveCardsDesc.timeOutReached) {
                    logger.debug("timeout reached. starting new game...");
                    removeFrameElements();
                    mainFrame.validate();
                    JOptionPane.showMessageDialog(mainFrame, "<html><center>Game timeout reached.<br>YOU HAVE LOST!!!<br>Starting new game...</center></html>", "Condolence", JOptionPane.INFORMATION_MESSAGE);
                    exchanger.put(END_GAME_REACHED);
                } else {
                    if (isEndOfGame(tempActiveCardsDesc)) {
                        logger.debug("end of game reached");
                        removeFrameElements();
                        mainFrame.validate();
                        JOptionPane.showMessageDialog(mainFrame, "<html><center>YOU HAVE WON!!!<br>Starting new game...</center></html>", "Congratulation", JOptionPane.INFORMATION_MESSAGE);
                        exchanger.put(END_GAME_REACHED);
                    } else {
                        if (tempActiveCardsDesc.cardsOnTable.size() != activeCardsDesc.cardsOnTable.size()) {
                            logger.debug("tempActiveCardsDesc.secondPLCardsNum" + activeCardsDesc.secondPLCardsNum);
                            activeCardsDesc = tempActiveCardsDesc;
                            activeCardsDesc.selectedCard = sortFirstPLLabels().get(activeCardsDesc.selectedCard);
                            logger.debug("redrawing table...");
                            statusLabel.setText("STATUS: waiting for second player move...");
                            drawTable(activeCardsDesc, mainFrame, gameType);
                            new Thread(this, "Last move waiting thread").start();
                        } else {
                            activeCardsDesc = tempActiveCardsDesc;
                            activeCardsDesc.selectedCard = sortFirstPLLabels().get(activeCardsDesc.selectedCard);
                            logger.debug("redrawing table...");
                            drawTable(activeCardsDesc, mainFrame, gameType);
                        }
                    }
                }
            } else {
                activeCardsDesc.selectedCard = cardNum;
                drawTable(activeCardsDesc, mainFrame, gameType);
            }
        }
    }

    private void createItemsListeners(List<JLabel> firstPLCardButtons, List<JLabel> cardsOnTableButtons, ActiveCardsDesc activeCardsDesc) {
        this.firstPLCardLabels = firstPLCardButtons;
        this.activeCardsDesc = activeCardsDesc;
        this.cardsOnTableLabels = cardsOnTableButtons;

        for (int i = 0; i < this.firstPLCardLabels.size(); i++) {
            final int cardNum = i;
            this.firstPLCardLabels.get(i).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent test) {
                    cardButtonPressed(cardNum);
                }
            });
        }

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                logger.debug("submit button clicked");
                changeGameType();
            }
        });
    }

    private void changeGameType() {
        boolean retryConnection = true;
        switch (gameType) {
            case BEATING_OFF:
                logger.debug("Taking cards...");
                while(retryConnection){
                    try{
                        activeCardsDesc = gameServer.setLastMove(serverID, plName, TAKE_CARDS);
                        retryConnection = false;
                    }catch(HessianRuntimeException hre){
                        retryConnection = noConnectionPrevention(hre);
                    }
                }
                activeCardsDesc.selectedCard = sortFirstPLLabels().get(activeCardsDesc.selectedCard);
                logger.debug("redrawing table...");
                statusLabel.setText("STATUS: waiting for second player move...");
                drawTable(activeCardsDesc, mainFrame, gameType);
                new Thread(this, "Last move waiting thread").start();
                break;
            case LEADING:
                logger.debug("End of leading...");
                while(retryConnection){
                    try{
                        activeCardsDesc = gameServer.setLastMove(serverID, plName, END_OF_LEADING);
                        retryConnection = false;
                    }catch(HessianRuntimeException hre){
                        retryConnection = noConnectionPrevention(hre);
                    }
                }
                activeCardsDesc.selectedCard = sortFirstPLLabels().get(activeCardsDesc.selectedCard);
                logger.debug("redrawing table...");
                statusLabel.setText("STATUS: waiting for second player move...");
                drawTable(activeCardsDesc, mainFrame, gameType);
                gameType = BEATING_OFF;
                new Thread(this, "Last move waiting thread").start();
                break;
            default:
                logger.error("Unexpected gameType value: " + gameType);
        }
    }

    private boolean noConnectionPrevention(HessianRuntimeException hre) {
        logger.error("Cann't connect to 81.22.135.175:8080/gameServer " + hre);
        Object[] options = {"Yes",
                "No, exit the game",
                "No, start new game"};

        switch (JOptionPane.showOptionDialog(mainFrame, "Cann't connect to game server. Check your firewall settings. Retry connection?", "Error",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[2])) {
            case JOptionPane.YES_OPTION:
                return true;
            case JOptionPane.NO_OPTION:
                logger.debug("Exiting the game...");
                removeFrameElements();
                mainFrame.validate();
                exchanger.put(EXIT_GAME);
                return false;
            case JOptionPane.CANCEL_OPTION:
                logger.debug("Starting new game...");
                removeFrameElements();
                mainFrame.validate();
                exchanger.put(END_GAME_REACHED);
                return false;
            default:
                return true;
        }
    }

    public void run() {
        logger.debug("Getting last move...");

        isTurnWaiting = true;
        boolean retryConnection = true;
        while (retryConnection) {
            try {
                activeCardsDesc = gameServer.getActiveCards(serverID, plName);
                retryConnection = false;
            } catch (HessianRuntimeException hre) {
                retryConnection = noConnectionPrevention(hre);
            }
        }
        if (activeCardsDesc.timeOutReached) {
            JOptionPane.showMessageDialog(mainFrame, "<html><center>Game timeout reached.<br>YOU HAVE WON!!!<br>Starting new game...</center></html>", "Congratulation", JOptionPane.ERROR_MESSAGE);
            removeFrameElements();
            mainFrame.validate();
            exchanger.put(END_GAME_REACHED);
        } else {
            if (activeCardsDesc.endOfGameReached) {
                logger.debug("end of game reached");
                removeFrameElements();
                mainFrame.validate();
                JOptionPane.showMessageDialog(mainFrame, "<html><center>YOU HAVE LOST!!!<br>Starting new game...</center></html>", "Condolence", JOptionPane.INFORMATION_MESSAGE);
                exchanger.put(END_GAME_REACHED);
            } else {
                activeCardsDesc.selectedCard = sortFirstPLLabels().get(activeCardsDesc.selectedCard);
                //checking if gameType changed
                if (activeCardsDesc.cardsOnTable.size() == 0 & gameType == BEATING_OFF) {
                    logger.debug("Changing game type...");
                    gameType = LEADING;
                }
                logger.debug("Last move got, re-drawing table...");

                switch (gameType) {
                    case LEADING:
                        statusLabel.setText("STATUS: leading, put next card or press End of turn button");
                        break;
                    case BEATING_OFF:
                        statusLabel.setText("STATUS: beating off, put next card or press End of turn button");
                        break;
                    default:
                        logger.error("Unexpected gameType value: " + gameType);
                }
                drawTable(activeCardsDesc, mainFrame, gameType);
                isTurnWaiting = false;
            }
        }
    }
}

