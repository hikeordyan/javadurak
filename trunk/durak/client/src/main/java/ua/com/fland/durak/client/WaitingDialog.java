package ua.com.fland.durak.client;

import com.caucho.hessian.client.HessianProxyFactory;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.net.MalformedURLException;

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
public class WaitingDialog extends JDialog implements Runnable {
    private static final Logger logger = Logger.getLogger(WaitingDialog.class);
    private GameServer gameServer;
    /**
     * Hessian factory for Hessian connection
     */
    private HessianProxyFactory factory;
    ActiveCardsDesc activeCardsDesc;
    boolean stop;

    private JProgressBar progressBar;

    private final static String url = "http://81.22.135.175:8080/gameServer";

    public WaitingDialog() {
        logger.debug("initing waiting");
        progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        //progressBar.setStringPainted(true);
        this.setSize(300, 50);
        this.setResizable(false);
        this.setLocationRelativeTo(getOwner());
        this.add(progressBar);
        this.setVisible(true);
        this.setAlwaysOnTop(true);
        //this.setModal(true);
    }

    private void initConnection() {
        //String url = "http://81.22.135.175:8080/gameServer";

        factory = new HessianProxyFactory();
        try {
            gameServer = (GameServer) factory.create(GameServer.class, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void run() {
    }
}
