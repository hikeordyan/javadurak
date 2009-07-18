package ua.com.fland.durak.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

public class ActiveCardsDesc implements Serializable {
    private int secondPLCardsNum;
    private List<Integer> cardsOnTable;
    private List<Integer> firstPLCards;
    private int selectedCard;
    private boolean timeOutReached;
    private boolean endOfGameReached;
    private byte leftCardNum;

    public ActiveCardsDesc() {
        cardsOnTable = new ArrayList<Integer>();
        firstPLCards = new ArrayList<Integer>();
        timeOutReached = false;
        endOfGameReached = false;
        leftCardNum = 0;
    }

    public int getSecondPLCardsNum() {
        return secondPLCardsNum;
    }

    public List<Integer> getCardsOnTable() {
        return cardsOnTable;
    }

    public List<Integer> getFirstPLCards() {
        return firstPLCards;
    }

    public int getSelectedCard() {
        return selectedCard;
    }

    public boolean isTimeOutReached() {
        return timeOutReached;
    }

    public boolean isEndOfGameReached() {
        return endOfGameReached;
    }

    public byte getLeftCardNum() {
        return leftCardNum;
    }

    public void setSecondPLCardsNum(int secondPLCardsNum) {
        this.secondPLCardsNum = secondPLCardsNum;
    }

    public void setCardsOnTable(List<Integer> cardsOnTable) {
        this.cardsOnTable = cardsOnTable;
    }

    public void setFirstPLCards(List<Integer> firstPLCards) {
        this.firstPLCards = firstPLCards;
    }

    public void setSelectedCard(int selectedCard) {
        this.selectedCard = selectedCard;
    }

    public void setTimeOutReached(boolean timeOutReached) {
        this.timeOutReached = timeOutReached;
    }

    public void setEndOfGameReached(boolean endOfGameReached) {
        this.endOfGameReached = endOfGameReached;
    }

    public void setLeftCardNum(byte leftCardNum) {
        this.leftCardNum = leftCardNum;
    }

    public void addFirstPLCards(int card){
        this.firstPLCards.add(card);
    }

    public void addCardsOnTable(int card){
        this.cardsOnTable.add(card);
    }
}
