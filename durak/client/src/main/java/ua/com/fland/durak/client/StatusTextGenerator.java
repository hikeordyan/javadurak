package ua.com.fland.durak.client;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by IntelliJ IDEA.<br>
 * User: maxim<br>
 * Date: Jul 13, 2009<br>
 * Time: 7:44:33 PM<br>
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

public class StatusTextGenerator {
    private static final Logger logger = Logger.getLogger(StatusTextGenerator.class);

    private Map<String, List<String>> additionalStatuses;

    public StatusTextGenerator() {
        additionalStatuses = new HashMap<String, List<String>>();
        additionalStatuses.put("LEADING", formPhrases("additinonalStatus.leading"));
        additionalStatuses.put("MOVE_WAITING", formPhrases("additinonalStatus.moveWaiting"));
        additionalStatuses.put("BEATING_OFF", formPhrases("additinonalStatus.beatingOff"));
        additionalStatuses.put("TAKING", formPhrases("additinonalStatus.gettingCards"));
    }

    private List<String> formPhrases(String gameType) {
        List<String> tempList = new ArrayList<String>();
        String[] texts = TextsGetter.getText(gameType).split(";");
        tempList.addAll(Arrays.asList(texts));
        return tempList;
    }

    public String getAdditionalText(String gameType) {
        if (additionalStatuses.containsKey(gameType)) {
            return additionalStatuses.get(gameType).get(new Random().nextInt(additionalStatuses.get(gameType).size()));
        }else{
            logger.error("Wrong gameType key: " + gameType);
            return "";
        }
    }
}
