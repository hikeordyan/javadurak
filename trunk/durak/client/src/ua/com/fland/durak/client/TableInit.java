package ua.com.fland.durak.client;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.<br>
 * User: maxim<br>
 * Date: May 20, 2009<br>
 * Time: 9:08:22 PM<br>
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

public class TableInit {
    private static final Logger logger = Logger.getLogger(TableInit.class);
    private static final String cardIconsPath = "cardIcons/";

    private JPanel mainPanel;

    public TableInit() {
        mainPanel = BoxLayoutUtils.createVerticalPanel();
        mainPanel.setBackground(new Color(0, 150, 0));
    }

    public TableElementsDesc placeElements(JButton submitButton, JLabel waitingLabel,
                                           ActiveCardsDesc activeCardsDesc, JPanel mainPanel, JLabel statusLabel) {
        this.mainPanel = mainPanel;
        logger.debug("Starting elemetns placing...");
        this.mainPanel = BoxLayoutUtils.createVerticalPanel();
        this.mainPanel.setBackground(new Color(0, 150, 0));

        //placing secondPL cards
        List<JLabel> secondPLCardLabels = placeSecondPLCards(activeCardsDesc.secondPLCardsNum);
        //placing cardsOnTable
        List<JLabel> cardsOnTableLabels = placeCardsOnTable(activeCardsDesc.cardsOnTable, activeCardsDesc.usedCardNum);
        //placing firstPl cards
        List<JLabel> firstPLCardLabels = placeFirstPLCards(activeCardsDesc.firstPLCards, activeCardsDesc.selectedCard);

        //placing submitButton and waiting animation
        JPanel buttonPanel = BoxLayoutUtils.createHorizontalPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(submitButton);
        buttonPanel.add(waitingLabel);

        JPanel statusPanel = BoxLayoutUtils.createHorizontalPanel();
        statusPanel.setOpaque(false);
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalGlue());
        this.mainPanel.add(buttonPanel);
        this.mainPanel.add(statusPanel);

        logger.debug("usedCardNum: " + activeCardsDesc.usedCardNum);

        return new TableElementsDesc(firstPLCardLabels, cardsOnTableLabels, secondPLCardLabels, submitButton, this.mainPanel);
    }

    private Map<Integer, Integer> sortFirstPLLabels(List<Integer> PLCards) {
        /**
         * key - sorted nomber, value - nomber in lables
         */
        Map<Integer, Integer> sortedValues = new HashMap<Integer, Integer>();

        int firstPlCardsNum = PLCards.size() / 2;

        for (int i = 0; i < firstPlCardsNum; i++) {
            sortedValues.put(i, i);
        }

        List<CardDesc> tempCardsDesc = new ArrayList<CardDesc>();
        for (int i = 0; i < PLCards.size(); i++) {
            CardDesc tempCardDesc = new CardDesc();
            tempCardDesc.cardSuit = PLCards.get(i);
            i++;
            tempCardDesc.cardValue = PLCards.get(i);
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

    private Map<Integer, Integer> asortedFirstPLLabels(List<Integer> PLCards) {
        Map<Integer, Integer> asortedFirstPlValues = new HashMap<Integer, Integer>();
        Map<Integer, Integer> sortedFirstPlValues = sortFirstPLLabels(PLCards);

        for (int i = 0; i < PLCards.size() / 2; i++) {
            asortedFirstPlValues.put(sortedFirstPlValues.get(i), i);
        }
        return asortedFirstPlValues;
    }

    private List<JLabel> placeCardsOnTable(List<Integer> cardsOnTable, byte usedCardNum) {
        //creating cardBatchLabel
        ImageIcon cardIcon = new ImageIcon();
        String cardIconName;
        cardIconName = cardIconsPath + "card" + 3 + ".gif";
        if (getClass().getResource(cardIconName) != null) {
            cardIcon = new ImageIcon(getClass().getResource(cardIconName));
        } else {
            logger.error("Cann't find file: " + cardIconName);
        }
        String leftCardsNum = Integer.toString(36 - usedCardNum);
        JLabel cardBatchLabel = new JLabel(leftCardsNum);
        cardBatchLabel.setIcon(cardIcon);
        cardBatchLabel.setFont(new Font(mainPanel.getFont().getFontName(), Font.PLAIN, mainPanel.getFont().getSize() + mainPanel.getFont().getSize()));
        cardBatchLabel.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);

        Border emptyBorder = BorderFactory.createEmptyBorder();
        //creating labels
        List<JLabel> cardsOnTableLabels = new ArrayList<JLabel>();
        for (int i = 0; i < cardsOnTable.size(); i++) {
            cardIconName = cardIconsPath + "card" + cardsOnTable.get(i) + cardsOnTable.get(++i) + ".gif";
            if (getClass().getResource(cardIconName) != null) {
                cardIcon = new ImageIcon(getClass().getResource(cardIconName));
            } else {
                logger.error("Cann't find file: " + cardIconName);
            }
            JLabel tempCard = new JLabel(cardIcon);
            tempCard.setBorder(emptyBorder);
            tempCard.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
            cardsOnTableLabels.add(tempCard);
        }

        JPanel cardsOnTablePanel = BoxLayoutUtils.createHorizontalPanel();
        cardsOnTablePanel.setOpaque(false);
        cardsOnTablePanel.add(Box.createHorizontalGlue());
        if (cardsOnTable.size() < 1) {
            logger.debug("creating free space for cardsOnTable");
            cardBatchLabel.setAlignmentY(JLabel.TOP_ALIGNMENT);
        } else {
            for (int i = 0; i < cardsOnTableLabels.size(); i++) {
                if ((i + 1) < cardsOnTableLabels.size()) {
                    int j = i + 1;
                    cardsOnTablePanel.add(Box.createRigidArea(new Dimension(40, 0)));
                    cardsOnTablePanel.add(cardsOnTableLabels.get(j));
                    cardsOnTablePanel.add(Box.createRigidArea(new Dimension(-100, 0)));
                    cardsOnTablePanel.add(cardsOnTableLabels.get(i));
                    cardsOnTablePanel.add(Box.createRigidArea(new Dimension(30, 0)));
                    ++i;
                } else {
                    cardsOnTablePanel.add(Box.createRigidArea(new Dimension(10, 0)));
                    cardsOnTablePanel.add(cardsOnTableLabels.get(i));
                }
            }
        }

        //adding cardbatch info
        cardsOnTablePanel.add(Box.createHorizontalGlue());
        cardsOnTablePanel.add(cardBatchLabel);

        mainPanel.add(cardsOnTablePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        return cardsOnTableLabels;
    }

    private List<JLabel> placeFirstPLCards(List<Integer> firstPLCards, int selectedCard) {
        JPanel firstPLCardsPanel = BoxLayoutUtils.createVerticalPanel();
        firstPLCardsPanel.setOpaque(false);
        JPanel firstCardsLabelRow = BoxLayoutUtils.createHorizontalPanel();
        firstCardsLabelRow.setOpaque(false);
        JPanel secondCardsLabelRow = BoxLayoutUtils.createHorizontalPanel();
        secondCardsLabelRow.setOpaque(false);
        List<JLabel> firstPLCardLabels = new ArrayList<JLabel>();
        ImageIcon cardIcon = new ImageIcon();
        String cardIconName;
        Border emptyBorder = BorderFactory.createEmptyBorder();

        //creating cardsLabels
        for (int i = 0; i < firstPLCards.size(); i++) {
            cardIconName = cardIconsPath + "card" + firstPLCards.get(i) + firstPLCards.get(++i) + ".gif";
            if (getClass().getResource(cardIconName) != null) {
                cardIcon = new ImageIcon(getClass().getResource(cardIconName));
            } else {
                logger.error("Cann't find file: " + cardIconName);
            }

            JLabel tempCard = new JLabel(cardIcon);
            tempCard.setBorder(emptyBorder);
            tempCard.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
            firstPLCardLabels.add(tempCard);
        }

        Map<Integer, Integer> sortedValues = sortFirstPLLabels(firstPLCards);
        Map<Integer, Integer> asortedValues = asortedFirstPLLabels(firstPLCards);

        int cardNum = sortedValues.size();
        if (cardNum < 13) {
            logger.debug("creating empty row in firstPLCards");
            secondCardsLabelRow.add(Box.createRigidArea(new Dimension(0, 110)));
        } else {
            for (int i = 12; i < cardNum; i++) {
                JPanel tempVertPanel = BoxLayoutUtils.createVerticalPanel();
                tempVertPanel.setOpaque(false);
                if (i == asortedValues.get(selectedCard)) {
                    tempVertPanel.add(firstPLCardLabels.get(sortedValues.get(i)));
                    tempVertPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                } else {
                    tempVertPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    tempVertPanel.add(firstPLCardLabels.get(sortedValues.get(i)));
                }
                secondCardsLabelRow.add(tempVertPanel);
                secondCardsLabelRow.add(Box.createRigidArea(new Dimension(10, 0)));
            }
            cardNum = 12;
        }

        for (int i = 0; i < cardNum; i++) {
            JPanel tempVertPanel = BoxLayoutUtils.createVerticalPanel();
            tempVertPanel.setOpaque(false);
            if (i == asortedValues.get(selectedCard)) {
                tempVertPanel.add(firstPLCardLabels.get(sortedValues.get(i)));
                tempVertPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            } else {
                tempVertPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                tempVertPanel.add(firstPLCardLabels.get(sortedValues.get(i)));
            }
            firstCardsLabelRow.add(tempVertPanel);
            firstCardsLabelRow.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        firstPLCardsPanel.add(firstCardsLabelRow);
        firstPLCardsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        firstPLCardsPanel.add(secondCardsLabelRow);
        mainPanel.add(firstPLCardsPanel);

        return firstPLCardLabels;
    }

    private List<JLabel> placeSecondPLCards(int secondPLCardsNum) {
        //placing secondPL cards
        ImageIcon cardIcon = new ImageIcon();
        String cardIconName = cardIconsPath + "cover.gif";
        if (getClass().getResource(cardIconName) != null) {
            cardIcon = new ImageIcon(getClass().getResource(cardIconName));
        } else {
            logger.error("Cann't find file: " + cardIconName);
        }

        //creating secondPL labels
        List<JLabel> secondPLCardLabels = new ArrayList<JLabel>();
        Border emptyBorder = BorderFactory.createEmptyBorder();
        for (int i = 0; i < secondPLCardsNum; i++) {
            JLabel tempCard = new JLabel(cardIcon);
            tempCard.setBorder(emptyBorder);
            tempCard.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
            secondPLCardLabels.add(tempCard);
        }

        JPanel secondPLCardsPanel = BoxLayoutUtils.createVerticalPanel();
        secondPLCardsPanel.setOpaque(false);
        JPanel firstCardsLabelRow = BoxLayoutUtils.createHorizontalPanel();
        firstCardsLabelRow.setOpaque(false);
        JPanel secondCardsLabelRow = BoxLayoutUtils.createHorizontalPanel();
        secondCardsLabelRow.setOpaque(false);
        int cardNum = secondPLCardsNum;
        if (secondPLCardsNum < 13) {
            logger.debug("creating free space...");
            secondCardsLabelRow.add(Box.createRigidArea(new Dimension(0, 110)));
        } else {
            cardNum = 12;
            for (int i = 12; i < secondPLCardsNum; i++) {
                secondCardsLabelRow.add(secondPLCardLabels.get(i));
                secondCardsLabelRow.add(Box.createRigidArea(new Dimension(10, 0)));
            }
        }
        for (int i = 0; i < cardNum; i++) {
            firstCardsLabelRow.add(secondPLCardLabels.get(i));
            firstCardsLabelRow.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        secondPLCardsPanel.add(secondCardsLabelRow);
        secondPLCardsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        secondPLCardsPanel.add(firstCardsLabelRow);
        mainPanel.add(secondPLCardsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        return secondPLCardLabels;
    }
}
