package ua.com.fland.durak.client;

import com.caucho.hessian.client.HessianProxyFactory;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;

import ua.com.fland.durak.client.Connection;

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

public class ConnectionServiceImpl implements ConnectionService {
    private static final Logger logger = Logger.getLogger(ConnectionServiceImpl.class);

    private String serverUrl;
    private Connection connection;
    private HessianProxyFactory hessianFactory;

    ConnectionServiceImpl() {
        hessianFactory = new HessianProxyFactory();
    }

    public void showValues() {
        logger.debug(serverUrl);

    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;

        try {
            connection = (Connection) hessianFactory.create(Connection.class, serverUrl);
        } catch (MalformedURLException e) {
            logger.error(e);
        }
    }

}
