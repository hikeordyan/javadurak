package ua.com.fland.durak.client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.<br>
 * User: maxim<br>
 * Date: Jul 23, 2009<br>
 * Time: 8:46:08 PM<br>
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

public class AngledLinesWindowsCornerIcon implements Icon {
  private static final Color WHITE_LINE_COLOR = new Color(255, 255, 255);

  private static final Color GRAY_LINE_COLOR = new Color(172, 168, 153);
  private static final int WIDTH = 13;

  private static final int HEIGHT = 13;

  public int getIconHeight() {
    return WIDTH;
  }

  public int getIconWidth() {
    return HEIGHT;
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {

    g.setColor(WHITE_LINE_COLOR);
    g.drawLine(0, 12, 12, 0);
    g.drawLine(5, 12, 12, 5);
    g.drawLine(10, 12, 12, 10);

    g.setColor(GRAY_LINE_COLOR);
    g.drawLine(1, 12, 12, 1);
    g.drawLine(2, 12, 12, 2);
    g.drawLine(3, 12, 12, 3);

    g.drawLine(6, 12, 12, 6);
    g.drawLine(7, 12, 12, 7);
    g.drawLine(8, 12, 12, 8);

    g.drawLine(11, 12, 12, 11);
    g.drawLine(12, 12, 12, 12);

  }
}
