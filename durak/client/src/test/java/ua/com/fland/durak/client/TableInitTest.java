package ua.com.fland.durak.client;

import org.junit.Test;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.<br>
 * User: maxim<br>
 * Date: Jul 12, 2009<br>
 * Time: 9:27:15 PM<br>
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

public class TableInitTest {
    @Test
    public void testCardsSorting(){
        ActiveCardsDesc activeCardsDesc = new ActiveCardsDesc();
        Random random = new Random();
        for (int i = 0; i < 12; i++){
            activeCardsDesc.addFirstPLCards(random.nextInt(3));
            activeCardsDesc.addFirstPLCards(random.nextInt(14));
        }
        
    }
}
