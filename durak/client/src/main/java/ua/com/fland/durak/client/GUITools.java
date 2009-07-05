package ua.com.fland.durak.client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.<br>
 * User: maxim<br>
 * Date: May 14, 2009<br>
 * Time: 6:38:14 PM<br>
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

public class GUITools {
    //this method get buttons and makes the recommended margins
    public static void
    createRecommendedMargin(JButton[] buttons) {
        for (int i = 0; i < buttons.length; i++) {
            Insets margin = buttons[i].getMargin();
            margin.left = 12;
            margin.right = 12;
            buttons[i].setMargin(margin);
        }
    }

    //make same size for componets from the bigets
    public static void makeSameSize(JComponent[] components) {
        //getting components width
        int[] sizes = new int[components.length];
        for (int i = 0; i < sizes.length; i++) {
            sizes[i] = components[i].getPreferredSize().width;
        }
        //max size calculating
        int maxSizePos = maximumElementPosition(sizes);
        Dimension maxSize =
                components[maxSizePos].getPreferredSize();
        //same size making
        for (int i = 0; i < components.length; i++) {
            components[i].setPreferredSize(maxSize);
            components[i].setMinimumSize(maxSize);
            components[i].setMaximumSize(maxSize);
        }
    }

    public static void fixTextFieldSize(JTextField field) {
        Dimension size = field.getPreferredSize();
        size.width = field.getMaximumSize().width;
        field.setMaximumSize(size);
    }

    private static int maximumElementPosition(int[] array) {
        int maxPos = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxPos]) maxPos = i;
        }
        return maxPos;
    }
}
