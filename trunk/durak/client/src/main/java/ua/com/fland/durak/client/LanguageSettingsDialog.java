package ua.com.fland.durak.client;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.<br>
 * User: maxim<br>
 * Date: Jul 30, 2009<br>
 * Time: 9:52:23 PM<br>
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

public class LanguageSettingsDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(LanguageSettingsDialog.class);

    private JPanel mainPanel;
    private JLabel selectLangLabel;
    private JComboBox selectLangCombo;
    private JButton okButton;
    private JButton cancelButton;

    private final Map<String, String> langs;

    public LanguageSettingsDialog(Frame frame) {
        super(frame);

        logger.debug("Language settings dialog initing...");

        langs = new HashMap<String, String>();
        langs.put("English", "en");
        langs.put("Русский", "ru");

        this.setTitle(TextsGetter.getText("languageSettingsDialog.title"));
        //this.setSize(300, 200);
        this.setMinimumSize(new Dimension(300, 120));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(getOwner());

        initElemnts();
        placeElements();


        this.pack();
    }

    private void initElemnts() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        selectLangLabel = new JLabel(TextsGetter.getText("languageSettingsDialog.selectLangLabel"));

        String langs[] = {"English", "Русский"};
        selectLangCombo = new JComboBox(langs);

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

    private void okButtonClicked() {
        String localeName = "en";
        String selectedItem = selectLangCombo.getSelectedItem().toString();
        if (selectedItem!= null) {
            if (langs.containsKey(selectedItem)) {
                localeName = langs.get(selectedItem);
            }
        }
        logger.debug("new locale name: " + localeName);
        Locale.setDefault(new Locale(localeName));
        this.dispose();
    }

    private void cancelButtonClicked() {
        this.dispose();
    }

    private void placeElements() {
        //adding langs selecter
        JPanel langsPanel = BoxLayoutUtils.createHorizontalPanel();
        langsPanel.add(selectLangLabel);
        langsPanel.add(Box.createHorizontalStrut(12));
        langsPanel.add(selectLangCombo);
        //adding some free space
        langsPanel.add(Box.createVerticalStrut(10));

        //adding buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JPanel buttonsGrid = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonsGrid.add(okButton);
        buttonsGrid.add(cancelButton);
        buttonsPanel.add(buttonsGrid);

        //placing text fields and labels panels
        BoxLayoutUtils.setGroupAlignmentX(new JComponent[]{langsPanel, buttonsPanel, mainPanel}, Component.LEFT_ALIGNMENT);

        //making same size for labels
        GUITools.makeSameSize(new JComponent[]{selectLangLabel});

        //making recommended size for buttons
        GUITools.makeSameSize(new JComponent[]{okButton, cancelButton});
        GUITools.createRecommendedMargin(new JButton[]{okButton, cancelButton});

        //making normal height of comboBoxes
        GUITools.fixComboBoxSize(selectLangCombo);

        mainPanel.add(langsPanel);
        mainPanel.add(buttonsPanel);

        this.add(mainPanel);
    }
}
