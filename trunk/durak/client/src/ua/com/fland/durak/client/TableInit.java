package ua.com.fland.durak.client;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

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

    public TableElementsDesc placeElements(JButton submitButton, JLabel waitingLabel,
                                           ActiveCardsDesc activeCardsDesc, JFrame mainFrame) {
        logger.debug("Starting elemetns placing...");

        //placing secondPL cards
        List<JLabel> secondPLCardLabels = new ArrayList<JLabel>();
        ImageIcon cardIcon = new ImageIcon();
        String cardIconName = cardIconsPath + "cover.gif";
        if (getClass().getResource(cardIconName) != null) {
            cardIcon = new ImageIcon(getClass().getResource(cardIconName));
        } else {
            logger.error("Cann't find file: " + cardIconName);
        }

        JPanel secondPLCardsPanel = BoxLayoutUtils.createVerticalPanel();
        JPanel firstCardsLabelRow = BoxLayoutUtils.createHorizontalPanel();
        JPanel secondCardsLabelRow = BoxLayoutUtils.createHorizontalPanel();

        Border emptyBorder = BorderFactory.createEmptyBorder();
        int cardNum = activeCardsDesc.secondPLCardsNum;
        if (activeCardsDesc.secondPLCardsNum < 13) {
            logger.debug("creating free space...");
            secondCardsLabelRow.add(Box.createRigidArea(new Dimension(0, 110)));
        } else {
            cardNum = 12;
            for (int i = 12; i < activeCardsDesc.secondPLCardsNum; i++) {
                JLabel tempCard = new JLabel(cardIcon);
                tempCard.setBorder(emptyBorder);
                tempCard.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
                secondPLCardLabels.add(tempCard);
                secondCardsLabelRow.add(tempCard);
                secondCardsLabelRow.add(Box.createRigidArea(new Dimension(10, 0)));
            }
        }
        for (int i = 0; i < cardNum; i++) {
            JLabel tempCard = new JLabel(cardIcon);
            tempCard.setBorder(emptyBorder);
            tempCard.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
            secondPLCardLabels.add(tempCard);
            firstCardsLabelRow.add(tempCard);
            firstCardsLabelRow.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        secondPLCardsPanel.add(secondCardsLabelRow);
        secondPLCardsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        secondPLCardsPanel.add(firstCardsLabelRow);

        //placing cardsOnTable
        JPanel cardsOnTablePanel = BoxLayoutUtils.createVerticalPanel();
        if (activeCardsDesc.cardsOnTable.size() < 1) {
            logger.debug("crating free space for cardsOnTable");
            cardsOnTablePanel.add(Box.createRigidArea(new Dimension(0, 105)));
        }
        List<JLabel> cardsOnTableLabels = new ArrayList<JLabel>();
        for (int i = 0; i < activeCardsDesc.cardsOnTable.size(); i++) {
            cardIconName = cardIconsPath + "card" + activeCardsDesc.cardsOnTable.get(i) + activeCardsDesc.cardsOnTable.get(++i) + ".gif";
            if (getClass().getResource(cardIconName) != null) {
                cardIcon = new ImageIcon(getClass().getResource(cardIconName));
            } else {
                logger.error("Cann't find file: " + cardIconName);
            }
            JLabel tempCard = new JLabel(cardIcon);
            tempCard.setBorder(emptyBorder);
            tempCard.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
            cardsOnTableLabels.add(tempCard);
            cardsOnTablePanel.add(tempCard);
            cardsOnTablePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        //placing firstPl cards
        JPanel firstPLCardsPanel = BoxLayoutUtils.createVerticalPanel();
        firstCardsLabelRow = BoxLayoutUtils.createHorizontalPanel();
        secondCardsLabelRow = BoxLayoutUtils.createHorizontalPanel();
        List<JLabel> firstPLCardLabels = new ArrayList<JLabel>();
        cardNum = activeCardsDesc.firstPLCards.size();
        if (activeCardsDesc.firstPLCards.size() < 25) {
            logger.debug("creating empty row in firstPLCards");
            secondCardsLabelRow.add(Box.createRigidArea(new Dimension(0, 110)));
        } else {
            cardNum = 24;
            for (int i = 24; i < activeCardsDesc.firstPLCards.size(); i++) {
                cardIconName = cardIconsPath + "card" + activeCardsDesc.firstPLCards.get(i) + activeCardsDesc.firstPLCards.get(++i) + ".gif";
                if (getClass().getResource(cardIconName) != null) {
                    cardIcon = new ImageIcon(getClass().getResource(cardIconName));
                } else {
                    logger.error("Cann't find file: " + cardIconName);
                }

                JLabel tempCard = new JLabel(cardIcon);
                tempCard.setBorder(emptyBorder);
                tempCard.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
                firstPLCardLabels.add(tempCard);
                secondCardsLabelRow.add(tempCard);
                secondCardsLabelRow.add(Box.createRigidArea(new Dimension(10, 0)));
            }
        }
        for (int i = 0; i < cardNum; i++) {
            cardIconName = cardIconsPath + "card" + activeCardsDesc.firstPLCards.get(i) + activeCardsDesc.firstPLCards.get(++i) + ".gif";
            if (getClass().getResource(cardIconName) != null) {
                cardIcon = new ImageIcon(getClass().getResource(cardIconName));
            } else {
                logger.error("Cann't find file: " + cardIconName);
            }

            JLabel tempCard = new JLabel(cardIcon);
            tempCard.setBorder(emptyBorder);
            tempCard.setAlignmentY(JLabel.TOP_ALIGNMENT);
            firstPLCardLabels.add(tempCard);
            firstCardsLabelRow.add(tempCard);
            firstCardsLabelRow.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        firstPLCardsPanel.add(firstCardsLabelRow);
        firstPLCardsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        firstPLCardsPanel.add(secondCardsLabelRow);

        //placing submitButton and waiting animation
        JPanel buttonPanel = BoxLayoutUtils.createVerticalPanel();
        buttonPanel.add(submitButton);
        //buttonPanel.add

        JPanel mainPanel = BoxLayoutUtils.createVerticalPanel();
        mainPanel.setBackground(new Color(0, 150, 0));
        mainPanel.add(secondPLCardsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(cardsOnTablePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(firstPLCardsPanel);
        mainPanel.add(buttonPanel);

        mainFrame.add(mainPanel);

        TableElementsDesc tempTableElementsDesc = new TableElementsDesc(firstPLCardLabels, cardsOnTableLabels, secondPLCardLabels, submitButton);

        return tempTableElementsDesc;
    }
}
