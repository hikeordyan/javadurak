package ua.com.fland.durak.client;

import org.apache.log4j.Logger;

import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.<br>
 * User: maxim<br>
 * Date: Jul 30, 2009<br>
 * Time: 9:41:21 PM<br>
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

public class TextsGetter {
    //private final static ResourceBundle textsResources = ResourceBundle.getBundle("texts");
    private static final Logger logger = Logger.getLogger(TextsGetter.class);

    public static String getText(String propertie) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("texts");
        if (resourceBundle.containsKey(propertie)) {
            return resourceBundle.getString(propertie);
        } else {
            logger.error("Cann't find resource for bundle texts_en/ru.properties, key: " + propertie);
            return "";
        }
    }
}
