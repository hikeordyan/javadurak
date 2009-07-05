package ua.com.fland.durak.client;

import com.caucho.hessian.client.HessianConnectionException;
import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

public class TableVisualization implements Runnable {
    private static final Logger logger = Logger.getLogger(TableVisualization.class);

    private JFrame mainFrame;
    private JPanel mainPanel;

    private List<JLabel> firstPLCardLabels;
    //private List<JLabel> cardsOnTableLabels;
    //private List<JLabel> secondPLCardLabels;

    private JLabel statusLabel = new JLabel("STATUS: no status yet");

    //private TableActionProcess tableActionProcess;

    private ActiveCardsDesc activeCardsDesc;
    private TableInit tableInit;

    private JButton submitButton;

    private JLabel waitingLabel;

    private byte gameType;

    private byte plName;
    private String serverID;

    private boolean isTurnWaiting;

    private JPanel waitingPanel;

    /**
     * Hessian factory for Hessian connection
     */
    private HessianProxyFactory factory;
    /**
     * Interface for Hessian connection
     */
    private GameServer gameServer;

    private FramesExchanger exchanger;

    private boolean isSend = false;

    /**
     * Different types of game
     */
    private final static byte LEADING = 0;
    private final static byte BEATING_OFF = 1;
    private final static byte TAKING_CARDS = -2;
    private final static byte PUTTING_CARDS = -3;
    private final static byte END_OF_TAKING_CARDS = -4;

    private final static byte TAKE_CARDS = -1;
    //private final static byte END_OF_TURN = -2;
    private final static byte END_OF_LEADING = -3;
    private final static byte END_OF_TAKE_CARDS = -4;
    private final static byte END_OF_PUTTING_CARDS = -5;

    /**
     * diff possible values of incoming secondPLCardsNum for got activeCardsDesc
     */
    private final static byte END_OF_GAME = -2;

    //exchanger statuses
    private final static byte END_GAME_REACHED = 6;
    private final static int EXIT_GAME = 7;

    private final static int STATUS_LABEL_LENGTH = 500;

    private int cardNum;

    private final static String url = "http://81.22.135.175:8080/gameServer";

    public TableVisualization(byte plName, String serverID, FramesExchanger exchanger) {
        this.plName = plName;
        this.serverID = serverID;
        this.exchanger = exchanger;

        submitButton = new JButton("End of turn");
        activeCardsDesc = new ActiveCardsDesc();
        firstPLCardLabels = new ArrayList<JLabel>();
        tableInit = new TableInit();
        mainPanel = new JPanel();

        ImageIcon waitEmptyIcon = new ImageIcon();
        String waitEmptyName = "waitEmpty.gif";
        if (getClass().getResource(waitEmptyName) != null) {
            waitEmptyIcon = new ImageIcon(getClass().getResource(waitEmptyName));
        } else {
            logger.error("Cann't find file: " + waitEmptyName);
        }
        waitingLabel = new JLabel(waitEmptyIcon);

        waitingPanel = BoxLayoutUtils.createHorizontalPanel();

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
        isTurnWaiting = false;

        initConnection();
    }

    private void mainFrameValidate() {
        mainFrame.validate();
        mainFrame.repaint();
    }

    public void drawTable(ActiveCardsDesc activeCardsDesc, JFrame mainFrame, byte gameType) {
        logger.debug("Drawing table...");
        this.activeCardsDesc = activeCardsDesc;
        this.mainFrame = mainFrame;
        this.gameType = gameType;

        this.mainFrame.addWindowListener(new WindowAdapter() {
            public void windowDeiconified(WindowEvent we) {
                mainFrameValidate();
            }
        });

        //setting up cards
        removeFrameElements();
        /*createCardButtons();
        placeCards();
        addCardsToTable();*/
        TableElementsDesc tempTableElementsDesc = tableInit.placeElements(submitButton, waitingLabel, activeCardsDesc, mainPanel, statusLabel);
        firstPLCardLabels = new ArrayList<JLabel>();
        firstPLCardLabels = tempTableElementsDesc.firstPLCardLabels;
        mainPanel = tempTableElementsDesc.mainPanel;
        this.mainFrame.add(mainPanel);

        createItemsListeners(firstPLCardLabels);

        this.mainFrame.validate();
        this.mainFrame.repaint();
    }

    public void startNewGame() {
        removeFrameElements();
        mainFrame.validate();
        exchanger.put(END_GAME_REACHED);
    }

    private void removeFrameElements() {
        logger.debug("Removing frame elements...");
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

        /*//removing firstPLCards
        for (JLabel firstPLCardButton : firstPLCardLabels) {
            mainPanel.remove(firstPLCardButton);
        }

        //removing cardsOnTable
        for (JLabel cardsOnTableButton : cardsOnTableLabels) {
            mainPanel.remove(cardsOnTableButton);
        }

        //removing secondPLCards
        for (JLabel secondPlayerCard : secondPLCardLabels) {
            mainPanel.remove(secondPlayerCard);
        }

        //removing submit button
        mainPanel.remove(submitButton);

        //removing emptySpace cards
        if (firstPLCardLabels.size() < 13) {
            mainPanel.remove(firstFirstRawEmptyCardPlace);
        }

        if (secondPLCardLabels.size() < 13) {
            mainPanel.remove(secondFirstRawEmptyCardPlace);
        }

        if (cardsOnTableLabels.size() == 0) {
            mainPanel.remove(tableEmptyCardPlace);
        }
        if (firstPLCardLabels.size() == 0) {
            mainPanel.remove(firstSecondRawEmptyCardPlace);
        }

        if (secondPLCardLabels.size() == 0) {
            mainPanel.remove(secondSecondRawEmptyCardPlace);
        }

        //removing status label
        mainPanel.remove(statusLabel);
        //mainFrame.removeAll();

        //removing waiting label
        mainPanel.remove(waitingLabel);*/

        mainFrame.remove(mainPanel);
        mainFrame.validate();
    }

    private Map<Integer, Integer> sortFirstPLLabels(List<Integer> firstPLCards) {
        /**
         * key - sorted nomber, value - nomber in lables
         */
        Map<Integer, Integer> sortedValues = new HashMap<Integer, Integer>();

        int firstPlCardsNum = firstPLCards.size() / 2;

        for (int i = 0; i < firstPlCardsNum; i++) {
            sortedValues.put(i, i);
        }

        List<CardDesc> tempCardsDesc = new ArrayList<CardDesc>();
        for (int i = 0; i < firstPLCards.size(); i++) {
            CardDesc tempCardDesc = new CardDesc();
            tempCardDesc.cardSuit = firstPLCards.get(i);
            i++;
            tempCardDesc.cardValue = firstPLCards.get(i);
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

    //TODO make something with this terrible initConnection in many classes
    /**
     * Initializing connection, which is using Hessian
     */
    private void initConnection() {
        factory = new HessianProxyFactory();
        try {
            gameServer = (GameServer) factory.create(GameServer.class, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private boolean isEndOfGame(ActiveCardsDesc activeCardsDesc) {
        return activeCardsDesc.secondPLCardsNum == END_OF_GAME;
    }

    private void cardButtonPressed(int cardNum) {
        logger.debug("card button pressed...");
        if (!isTurnWaiting) {
            if (activeCardsDesc.selectedCard == cardNum) {
                logger.debug("Setting visible waiting animation");
                ImageIcon waitAnimationIcon = new ImageIcon();
                String waitAnimationName = "waitAnimation.gif";
                if (getClass().getResource(waitAnimationName) != null) {
                    waitAnimationIcon = new ImageIcon(getClass().getResource(waitAnimationName));
                } else {
                    logger.error("Cann't find file: " + waitAnimationName);
                }
                waitingLabel.setIcon(waitAnimationIcon);
                submitButton.setEnabled(false);
                mainFrame.validate();
                this.cardNum = cardNum;
                isSend = true;
                new Thread(this, "sending last move").start();
                //isSend = false;
            } else {
                activeCardsDesc.selectedCard = cardNum;
                drawTable(activeCardsDesc, mainFrame, gameType);
            }
        }
    }

    private void createItemsListeners(List<JLabel> firstPLCardButtons/*, List<JLabel> cardsOnTableButtons, ActiveCardsDesc activeCardsDesc*/) {
        this.firstPLCardLabels = firstPLCardButtons;
        /*this.activeCardsDesc = activeCardsDesc;
        this.cardsOnTableLabels = cardsOnTableButtons;*/

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
            public void actionPerformed(ActionEvent ae) {
                logger.debug("submit button clicked");
                changeGameType();
            }
        });
    }

    private void changeGameType() {
        boolean retryConnection = true;
        boolean stopCurrGame = false;
        switch (gameType) {
            case PUTTING_CARDS:
                logger.debug("End of putting cards");
                activeCardsDesc = gameServer.setLastMove(serverID, plName, END_OF_PUTTING_CARDS);
                gameType = LEADING;
                drawTable(activeCardsDesc, mainFrame, gameType);
                break;
            case TAKING_CARDS:
                break;
            case BEATING_OFF:
                logger.debug("Taking cards...");
                while (retryConnection) {
                    try {
                        activeCardsDesc = gameServer.setLastMove(serverID, plName, TAKE_CARDS);
                        retryConnection = false;
                    } catch (HessianRuntimeException hre) {
                        retryConnection = noConnectionPrevention(hre);
                        stopCurrGame = !retryConnection;
                    } catch (HessianConnectionException hce) {
                        retryConnection = noConnectionPrevention(hce);
                        stopCurrGame = !retryConnection;
                    }
                }
                if (!stopCurrGame) {
                    activeCardsDesc.selectedCard = sortFirstPLLabels(activeCardsDesc.firstPLCards).get(activeCardsDesc.selectedCard);
                    logger.debug("redrawing table...");
                    statusLabel.setText("STATUS: waiting for second player move...");
                    drawTable(activeCardsDesc, mainFrame, gameType);
                    submitButton.setEnabled(false);
                    gameType = TAKING_CARDS;
                    new Thread(this, "Last move waiting thread").start();
                }
                break;
            case LEADING:
                logger.debug("End of leading...");
                while (retryConnection) {
                    try {
                        activeCardsDesc = gameServer.setLastMove(serverID, plName, END_OF_LEADING);
                        retryConnection = false;
                    } catch (HessianRuntimeException hre) {
                        retryConnection = noConnectionPrevention(hre);
                    } catch (HessianConnectionException hce) {
                        retryConnection = noConnectionPrevention(hce);
                    }
                }
                logger.debug("activeCardsDesc.selectedCard" + activeCardsDesc.selectedCard);
                activeCardsDesc.selectedCard = sortFirstPLLabels(activeCardsDesc.firstPLCards).get(activeCardsDesc.selectedCard);
                logger.debug("redrawing table...");
                statusLabel.setText("STATUS: waiting for second player move...");
                drawTable(activeCardsDesc, mainFrame, gameType);
                gameType = BEATING_OFF;
                submitButton.setEnabled(false);
                new Thread(this, "Last move waiting thread").start();
                break;
            default:
                logger.error("Unexpected gameType value: " + gameType);
        }
    }

    private boolean noConnectionPrevention(HessianRuntimeException hre) {
        logger.error("Cann't connect to " + url + " " + hre);
        Object[] options = {"Yes",
                "No, exit the game",
                "No, start new game"};

        switch (JOptionPane.showOptionDialog(mainFrame, "Cann't connect to game server. Check your firewall settings or in-game proxy settings. Retry connection?", "Error",
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

    private boolean noConnectionPrevention(HessianConnectionException hce) {
        logger.error("Cann't connect to " + url + " " + hce);
        Object[] options = {"Yes",
                "No, exit the game",
                "No, start new game"};

        switch (JOptionPane.showOptionDialog(mainFrame, "Cann't connect to game server. Check your firewall settings or in-game proxy settings. Retry connection?", "Error",
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

    private void sendSelectedCard() {
        isSend = false;
        boolean retryConnection = true;
        boolean isStopCurrGame = false;
        ActiveCardsDesc tempActiveCardsDesc = new ActiveCardsDesc();
        while (retryConnection) {
            try {
                logger.debug("setting visible");
                ImageIcon waitAnimationIcon = new ImageIcon();
                String waitAnimationName = "waitAnimation.gif";
                if (getClass().getResource(waitAnimationName) != null) {
                    waitAnimationIcon = new ImageIcon(getClass().getResource(waitAnimationName));
                } else {
                    logger.error("Cann't find file: " + waitAnimationName);
                }
                waitingLabel.setIcon(waitAnimationIcon);

                tempActiveCardsDesc = gameServer.setLastMove(serverID, plName, cardNum);

                ImageIcon waitEmptyIcon = new ImageIcon();
                String waitEmptyName = "waitEmpty.gif";
                if (getClass().getResource(waitEmptyName) != null) {
                    waitEmptyIcon = new ImageIcon(getClass().getResource(waitEmptyName));
                } else {
                    logger.error("Cann't find file: " + waitEmptyName);
                }
                waitingLabel.setIcon(waitEmptyIcon);
                submitButton.setEnabled(true);
                retryConnection = false;
            } catch (HessianRuntimeException hre) {
                retryConnection = noConnectionPrevention(hre);
                isStopCurrGame = !retryConnection;
            } catch (HessianConnectionException hce) {
                retryConnection = noConnectionPrevention(hce);
                isStopCurrGame = !retryConnection;
            }
        }
        if (!isStopCurrGame) {
            logger.debug("tempActiveCardsDesc.secondPLCardsNum " + tempActiveCardsDesc.secondPLCardsNum);
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
                    if (tempActiveCardsDesc.selectedCard == PUTTING_CARDS) {
                        tempActiveCardsDesc.selectedCard = 0;
                        gameType = PUTTING_CARDS;
                    }
                    if (gameType == PUTTING_CARDS) {
                        logger.debug("tempActiveCardsDesc.secondPLCardsNum " + activeCardsDesc.secondPLCardsNum);
                        tempActiveCardsDesc.selectedCard = 0;
                        activeCardsDesc = tempActiveCardsDesc;
                        activeCardsDesc.selectedCard = sortFirstPLLabels(activeCardsDesc.firstPLCards).get(activeCardsDesc.selectedCard);
                        logger.debug("redrawing table...");
                        statusLabel.setText("STATUS: leading, put next card or press End of turn button");
                        drawTable(activeCardsDesc, mainFrame, gameType);
                        submitButton.setEnabled(true);
                    } else {
                        if (tempActiveCardsDesc.cardsOnTable.size() != activeCardsDesc.cardsOnTable.size()) {
                            logger.debug("tempActiveCardsDesc.secondPLCardsNum " + activeCardsDesc.secondPLCardsNum);
                            activeCardsDesc = tempActiveCardsDesc;
                            activeCardsDesc.selectedCard = sortFirstPLLabels(activeCardsDesc.firstPLCards).get(activeCardsDesc.selectedCard);
                            logger.debug("redrawing table...");
                            statusLabel.setText("STATUS: waiting for second player move...");
                            drawTable(activeCardsDesc, mainFrame, gameType);
                            submitButton.setEnabled(false);
                            logger.debug("starting waiting card thread...");
                            new Thread(this, "Last move waiting thread").start();
                        } else {
                            activeCardsDesc = tempActiveCardsDesc;
                            activeCardsDesc.selectedCard = sortFirstPLLabels(activeCardsDesc.firstPLCards).get(activeCardsDesc.selectedCard);
                            logger.debug("redrawing table...");
                            drawTable(activeCardsDesc, mainFrame, gameType);
                        }
                    }
                }
            }
        }
    }

    public void run() {
        if (!isSend) {
            logger.debug("Getting last move...");

            isTurnWaiting = true;
            boolean retryConnection = true;
            while (retryConnection) {
                try {
                    activeCardsDesc = gameServer.getActiveCards(serverID, plName);
                    retryConnection = false;
                } catch (HessianRuntimeException hre) {
                    retryConnection = noConnectionPrevention(hre);
                } catch (HessianConnectionException hce) {
                    retryConnection = noConnectionPrevention(hce);
                }
            }
            submitButton.setEnabled(true);

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
                    //checking if game type changed to taking cards
                    logger.debug("activeCard num got: " + activeCardsDesc.selectedCard);
                    if (activeCardsDesc.selectedCard < 0) {
                        switch (activeCardsDesc.selectedCard) {
                            case PUTTING_CARDS:
                                gameType = PUTTING_CARDS;
                                logger.debug("Game type changed to putting cards");
                                break;
                            case TAKING_CARDS:
                                gameType = TAKING_CARDS;
                                logger.debug("Game type changed to taking cards");
                                break;
                            case END_OF_TAKING_CARDS:
                                statusLabel.setText("STATUS: waiting for second player move...");
                                drawTable(activeCardsDesc, mainFrame, gameType);
                                submitButton.setEnabled(false);
                                gameType = BEATING_OFF;
                                new Thread(this, "Last move waiting thread").start();
                                break;
                            default:
                                logger.error("Unexpected selectedCard value: " + activeCardsDesc.selectedCard);
                        }
                        activeCardsDesc.selectedCard = 0;
                    } else {
                        activeCardsDesc.selectedCard = sortFirstPLLabels(activeCardsDesc.firstPLCards).get(activeCardsDesc.selectedCard);
                        //checking if gameType changed
                        if (activeCardsDesc.cardsOnTable.size() == 0 & gameType == BEATING_OFF) {
                            logger.debug("Changing game type...");
                            gameType = LEADING;
                        }
                    }
                    logger.debug("Last move got, re-drawing table...");

                    logger.debug("gameType " + gameType);
                    switch (gameType) {
                        case LEADING:
                            statusLabel.setText("STATUS: leading, put next card or press End of turn button");
                            break;
                        case BEATING_OFF:
                            statusLabel.setText("STATUS: beating off, put next card or press End of turn button");
                            break;
                        case TAKING_CARDS:
                            statusLabel.setText("STATUS: taking cards, waiting for next card");
                            break;
                        case PUTTING_CARDS:
                            statusLabel.setText("STATUS: leading, put next card or press End of turn button");
                            break;
                        default:
                            logger.error("Unexpected gameType value: " + gameType);
                    }
                    drawTable(activeCardsDesc, mainFrame, gameType);
                    isTurnWaiting = false;
                }
            }

            if (gameType == TAKING_CARDS) {
                gameType = BEATING_OFF;
                new Thread(this, "Last move waiting thread").start();
            }
        } else {
            isSend = false;
            sendSelectedCard();
        }
    }
}

