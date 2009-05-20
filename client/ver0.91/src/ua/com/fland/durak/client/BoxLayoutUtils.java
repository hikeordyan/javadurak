package ua.com.fland.durak.client;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.<br>
 * User: maxim<br>
 * Date: May 7, 2009<br>
 * Time: 3:41:13 PM<br>
 *
 *
    DukarGameClient - client of on-line durak game<br>
    Copyright (C) 2009  Maxim Bondarenko<br>

    This program is free software: you can redistribute it and/or modify<br>
    it under the terms of the GNU General Public License as published by<br>
    the Free Software Foundation, either version 3 of the License, or<br>
    (at your option) any later version.<br>
    <br>
    This program is distributed in the hope that it will be useful,<br>
    but WITHOUT ANY WARRANTY; without even the implied warranty of<br>
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<br>
    GNU General Public License for more details.<br>
    <br>
    You should have received a copy of the GNU General Public License<br>
    along with this program.  If not, see <a href="http://www.gnu.org/licenses/">GNU Licenses</a><br>
 */

public class BoxLayoutUtils {
    //makes the same alignment on XAxis for group of elements
    public static void setGroupAlignmentX(JComponent[] cs, float alignment) {
        for (int i = 0; i < cs.length; i++) {
            cs[i].setAlignmentX(alignment);
        }
    }

    //makes the same alignment on YAxis for group of elements
    public static void setGroupAlignmentY(JComponent[] cs, float alignment) {
        for (int i = 0; i < cs.length; i++) {
            cs[i].setAlignmentY(alignment);
        }
    }

    //returns panel with vertical elements placing
    public static JPanel createVerticalPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    //returns panel with horizontal elements placing
    public static JPanel createHorizontalPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        return p;
    }
}
