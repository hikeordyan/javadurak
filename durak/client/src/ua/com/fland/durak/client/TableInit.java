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

    public JFrame placeElements(JButton submitButton, JLabel waitingLabel,
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

        JPanel secondPLCardsPanel = BoxLayoutUtils.createHorizontalPanel();
        if (activeCardsDesc.secondPLCardsNum < 13) {
            logger.debug("creating free space...");
            secondPLCardsPanel.add(Box.createRigidArea(new Dimension(0, 200)));
        }
        Border emptyBorder = BorderFactory.createEmptyBorder();
        for (int i = 0; i < activeCardsDesc.secondPLCardsNum; i++) {
            JLabel tempCard = new JLabel(cardIcon);
            tempCard.setBorder(emptyBorder);
            secondPLCardLabels.add(tempCard);
            secondPLCardsPanel.add(tempCard);
            secondPLCardsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        //placing cardsOnTable
        List<JLabel> cardsOnTableLabels = new ArrayList<JLabel>();
        JPanel cardsOnTablePanel = BoxLayoutUtils.createHorizontalPanel();
        for (int i = 0; i < activeCardsDesc.cardsOnTable.size(); i++) {
            cardIconName = cardIconsPath + "card" + activeCardsDesc.cardsOnTable.get(i) + activeCardsDesc.cardsOnTable.get(++i) + ".gif";
            if (getClass().getResource(cardIconName) != null) {
                cardIcon = new ImageIcon(getClass().getResource(cardIconName));
            } else {
                logger.error("Cann't find file: " + cardIconName);
            }
            JLabel tempCard = new JLabel(cardIcon);
            tempCard.setBorder(emptyBorder);
            cardsOnTableLabels.add(tempCard);
            cardsOnTablePanel.add(tempCard);
            cardsOnTablePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        //placing firstPl cards
        JPanel firstPLCardsPanel = BoxLayoutUtils.createHorizontalPanel();
        if (activeCardsDesc.firstPLCards.size() < 13) {
            firstPLCardsPanel.add(Box.createRigidArea(new Dimension(0, 200)));
        }
        
        List<JLabel> firstPLCardLabels = new ArrayList<JLabel>();
        for (int i=0; i<activeCardsDesc.firstPLCards.size(); i++){
            cardIconName = cardIconsPath + "card" + activeCardsDesc.firstPLCards.get(i) + activeCardsDesc.firstPLCards.get(++i) + ".gif";
            if (getClass().getResource(cardIconName) != null) {
                cardIcon = new ImageIcon(getClass().getResource(cardIconName));
            } else {
                logger.error("Cann't find file: " + cardIconName);
            }

            JLabel tempCard = new JLabel(cardIcon);
            tempCard.setBorder(emptyBorder);
            firstPLCardLabels.add(tempCard);
            firstPLCardsPanel.add(tempCard);
            firstPLCardsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        JPanel mainPanel = BoxLayoutUtils.createVerticalPanel();
        mainPanel.add(secondPLCardsPanel);
        mainPanel.add(cardsOnTablePanel);
        mainPanel.add(firstPLCardsPanel);

        mainFrame.add(mainPanel);

        return null;
    }
}
