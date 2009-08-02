package ua.com.fland.durak.client;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

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

public class ConnectionSettingsDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(ConnectionSettingsDialog.class);

    private JPanel mainPanel;
    private JTextField proxyHost;
    private JTextField proxyPort;
    private JCheckBox isUsingProxy;
    private JLabel proxyHostLabel;
    private JLabel proxyPortLabel;
    private JButton okButton;
    private JButton cancelButton;

    public ConnectionSettingsDialog(Frame frame) {
        super(frame);

        logger.debug("ConnectionSettingsDialog inited");

        this.setTitle(TextsGetter.getText("connectionSettingsDialog.title"));
        //this.setResizable(false);
        this.setSize(300, 200);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(getOwner());

        initElements();
        placeElements();

        this.pack();
    }

    private void placeElements() {
        //placing textField and label for proxy host
        JPanel proxyHostPanel = BoxLayoutUtils.createHorizontalPanel();
        proxyHostPanel.add(proxyHostLabel);
        proxyHostPanel.add(Box.createHorizontalStrut(12));
        proxyHostPanel.add(proxyHost);
        BoxLayoutUtils.setGroupAlignmentX(new JComponent[]{proxyHost, proxyHostLabel}, Component.LEFT_ALIGNMENT);

        //placing textField and label for proxy port
        JPanel proxyPortPanel = BoxLayoutUtils.createHorizontalPanel();
        proxyPortPanel.add(proxyPortLabel);
        proxyPortPanel.add(Box.createHorizontalStrut(12));
        proxyPortPanel.add(proxyPort);
        BoxLayoutUtils.setGroupAlignmentX(new JComponent[]{proxyPort, proxyPortLabel}, Component.LEFT_ALIGNMENT);

        //placing checkbox which is enabling settings
        JPanel useProxyPanel = BoxLayoutUtils.createHorizontalPanel();
        useProxyPanel.add(isUsingProxy);
        BoxLayoutUtils.setGroupAlignmentX(new JComponent[]{isUsingProxy}, Component.LEFT_ALIGNMENT);

        //adding buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JPanel buttonsGrid = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonsGrid.add(okButton);
        //buttonsGrid.add(Box.createHorizontalStrut(12));
        buttonsGrid.add(cancelButton);
        buttonsPanel.add(buttonsGrid);

        //placing text fields and labels panels
        BoxLayoutUtils.setGroupAlignmentX(new JComponent[]{proxyHostPanel, proxyPortPanel,
                useProxyPanel, buttonsPanel, mainPanel}, Component.LEFT_ALIGNMENT);

        //making same size for labels
        GUITools.makeSameSize(new JComponent[]{proxyHostLabel, proxyPortLabel});

        //making recommended size for buttons
        GUITools.makeSameSize(new JComponent[]{okButton, cancelButton});
        GUITools.createRecommendedMargin(new JButton[]{okButton, cancelButton});

        //making normal height of textFields
        GUITools.fixTextFieldSize(proxyHost);
        GUITools.fixTextFieldSize(proxyPort);

        //adding temp panels to mainPanel
        mainPanel.add(proxyHostPanel);
        mainPanel.add(proxyPortPanel);
        mainPanel.add(useProxyPanel);
        mainPanel.add(buttonsPanel);

        this.add(mainPanel);
    }

    private void initElements() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        proxyHost = new JTextField(20);
        proxyHost.setText(System.getProperty("http.proxyHost"));
        proxyHostLabel = new JLabel(TextsGetter.getText("connectionSettingsDialog.proxyHost"));
        proxyHostLabel.setLabelFor(proxyHost);

        proxyPort = new JTextField(5);
        proxyPort.setText(System.getProperty("http.proxyPort"));
        proxyPortLabel = new JLabel(TextsGetter.getText("connectionSettingsDialog.proxyPort"));
        proxyPortLabel.setLabelFor(proxyPort);

        isUsingProxy = new JCheckBox(TextsGetter.getText("connectionSettingsDialog.useProxy"));
        if (System.getProperty("http.proxyHost") != null & System.getProperty("http.proxyPort") != null) {
            if (!System.getProperty("http.proxyPort").equals("") & !System.getProperty("http.proxyHost").equals("")) {
                isUsingProxy.setSelected(true);
            }
        } else {
            isUsingProxy.setSelected(false);
        }

        okButton = new JButton(TextsGetter.getText("buttons.ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                okButtonClicked();
            }
        });

        cancelButton = new JButton(TextsGetter.getText("buttons.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cancelButtonClicked();
            }
        });
    }

    private void cancelButtonClicked() {
        logger.debug("Proxy settings not changed");
        this.setModal(false);
        this.setVisible(false);
        this.dispose();
        logger.debug("ConnectionSettingsDialog closing");
    }

    private void okButtonClicked() {
        if (isUsingProxy.isSelected()) {
            Properties systemSettings = System.getProperties();
            systemSettings.put("http.proxyHost", proxyHost.getText());
            systemSettings.put("http.proxyPort", proxyPort.getText());
            System.setProperties(systemSettings);
        } else {
            Properties systemSettings = System.getProperties();
            systemSettings.put("http.proxyHost", "");
            systemSettings.put("http.proxyPort", "");
            System.setProperties(systemSettings);
        }
        logger.debug("Set http.proxyHost: " + System.getProperty("http.proxyHost"));
        logger.debug("Set http.proxyPort: " + System.getProperty("http.proxyPort"));
        this.setModal(false);
        this.setVisible(false);
        this.dispose();
        logger.debug("ConnectionSettingsDialog closing");
    }
}
