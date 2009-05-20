package ua.com.fland.durak.client;

import java.util.Map;
import java.util.List;

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
public interface GameServer {
    /**
     * Return available waiting servers names.
     * @return Map of names.
     *  Key - unique value for each game name. Identical with keys of timeouts of waiting servers.
     *  Value - unique name for each game.
     */
    public Map<String, String> getGamesNames();

    /**
     * Return available waiting servers timeouts.
     * @return Map of timeouts.
     *  Key - unique value for each game timeout. Identical with keys of names of waiting servers.
     *  Value - value of timeout in seconds.
     */
    public Map<String, Integer> getGamesTimeouts();

    /**
     * Add waiting game server
     * @param waitingServerName unique name for game.
     * @param timeout time out of game in seconds.
     * @return unique key for each waiting server.
     */
    public String addGameServer(String waitingServerName, int timeout);

    public void test();

    public ServerDesc testSerial();

    public boolean load();

    /**
     * Get pair for game.
     * @param waitingServerName unique name for game
     * @param waitingServerID unique key for each waiting server
     * @return unique key for each pair;
     *  "-1" if waiting server stopped;
     *  "-noSuchWaitingServerName" if there is no such server name waiting;
     *  "-waitingServerStopped" if server has been already stopped.
     */
    public String getPair(String waitingServerName, String waitingServerID);

    /**
     * Set pair for game
     * @param waitingServerId unique key for each waiting server.
     * @return unique key for each pair;
     *  "-noSuchWaitingServerName" if there is no such server name waiting.
     */
    public String setPair(String waitingServerId);

    /**
     * Remove current waiting server info
     * @param waitingServerID unique key for each waiting server.
     * @return result true result if removing finished successfully
     */
    public boolean removeGameServer(String waitingServerID);

    /**
     * Return full created card batch values.
     * @deprecated
     * @param serverID unique key for each game server.
     * @return list of cards batch. First value of list - card suit, second value of list - card value.
     *  Similarly for left values in list.
     */
    public List<Integer> getCardBatchValues(String serverID);

    /**
     * Get player cards, card on table, and second player cards number, without it values.
     * @param serverID unique key for each game server.
     * @param plName name of current player, can be 1 - first player, 2 - second player.
     * @return description of active cards.
     */
    public ActiveCardsDesc getActiveCards(String serverID, byte plName);

    public ActiveCardsDesc setLastMove(String serverID, byte plName, int cardNum);
}
