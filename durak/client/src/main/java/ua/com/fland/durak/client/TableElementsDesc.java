package ua.com.fland.durak.client;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.<br>
 * User: maxim<br>
 * Date: May 26, 2009<br>
 * Time: 7:15:02 PM<br>
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

public class TableElementsDesc {
    public List<JLabel> firstPLCardLabels;
    public List<JLabel> cardsOnTableLabels;
    public List<JLabel> secondPLCardLabels;
    public JLabel statusLabel = new JLabel("STATUS: no status yet");
    public JButton submitButton;
    public JPanel mainPanel;

    public TableElementsDesc(List<JLabel> firstPLCardLabels, List<JLabel> cardsOnTableLabels, List<JLabel> secondPLCardLabels, JButton submitButton, JPanel mainPanel) {
        this.firstPLCardLabels = firstPLCardLabels;
        this.cardsOnTableLabels = cardsOnTableLabels;
        this.secondPLCardLabels = secondPLCardLabels;
        this.submitButton = submitButton;
        this.mainPanel = mainPanel;
    }
}
