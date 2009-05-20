package ua.com.fland.durak.client;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class AboutDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(AboutDialog.class);

    private JLabel textInfo;
    private JButton okButton;
    private JPanel mainPanel;

    public AboutDialog() {
        this.setSize(new Dimension(400, 140));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(getOwner());
        this.setTitle("About");

        initElements();
        placeElements();
        addActionListeners();

        this.add(mainPanel);
    }

    private void initElements() {
        textInfo = new JLabel("<html>The Durak Game client<br>ver 0.9.1 rev 43<br>http://www.gnu.org/licenses/gpl.html - " +
                "GNU General Public License</html>");
        ImageIcon gplIcon = new ImageIcon();
        String waitAnimationName = "gplv3.png";
        if (getClass().getResource(waitAnimationName) != null) {
            gplIcon = new ImageIcon(getClass().getResource(waitAnimationName));
        } else {
            logger.error("Cann't find file: " + waitAnimationName);
        }
        textInfo.setIcon(gplIcon);
        textInfo.setAlignmentX(CENTER_ALIGNMENT);

        okButton = new JButton("OK");
        okButton.setAlignmentX(CENTER_ALIGNMENT);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    }

    private void addActionListeners() {
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                closeDialog();
            }
        });
    }

    private void closeDialog() {
        this.setVisible(false);
        this.dispose();
    }

    private void placeElements() {
        mainPanel.add(textInfo);
        mainPanel.add(okButton);
    }
}
