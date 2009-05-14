package ua.com.fland.durak.client;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.util.Date;

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

public class MainGameWindow /*extends JFrame*/ implements Runnable {
    private static final Logger logger = Logger.getLogger(MainGameWindow.class);

    private JFrame mainFrame;
    private StartGameWindow startGameFrame;
    private int plID;
    private long pairNum;

    private FramesExchanger exchanger;

    private String serverID;
    private byte plName;

    /**
     * Hessian factory for Hessian connection
     */
    private HessianProxyFactory factory;
    /**
     * Interface for Hessian connection
     */
    private GameServer gameServer;

    private ActiveCardsDesc activeCardsDesc;

    private byte gameType;

    private final static byte LEADING = 0;
    private final static byte BEATING_OFF = 1;

    private final static byte FIRST_PL = 1;
    private final static byte SECOND_PL = 2;

    private final static int NEW_GAME_ACCEPTED = 3;
    private final static int EXIT_START_GAME = 4;
    private final static int EXIT_GAME = 7;
    private final static int JOIN_GAME_ACCEPTED = 5;

    private final static int END_GAME_REACHED = 6;

    /**
     * Initializing main parameters of MainGameWindow
     */
    MainGameWindow() {
        logger.debug("Creating new frame");
        mainFrame = new JFrame("Cards");

        logger.debug("Setting frame params");
        mainFrame.setSize(1024, 768);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().setBackground(new Color(0, 150, 0));
        initMenues();

        logger.debug("Setting other params");
        //getPrimaryNetworkData();
        initConnection();
        makeGUI();
        exchanger = new FramesExchanger();
        activeCardsDesc = new ActiveCardsDesc();

        logger.debug("Showing frame");
        mainFrame.setVisible(true);

        /*logger.debug("Creating NewGame frame");
        new Thread(this, "Start MainGameWindow change").start();
        startGameFrame = new StartGameWindow(exchanger);
        logger.debug("Showing NewGame frame...");
        startGameFrame.setVisible(true);*/
        showStartNewGameWindow();
    }

    private void showStartNewGameWindow() {
        logger.debug("Creating NewGame frame");
        new Thread(this, "Start MainGameWindow change").start();
        startGameFrame = new StartGameWindow(exchanger);
        logger.debug("Showing NewGame frame...");
        startGameFrame.setVisible(true);
    }

    private void initMenues() {
        MenuBar mainMenuBar = new MenuBar();
        Menu settingsMenu = new Menu("Settings", false);
        MenuItem connectionSettingsMenu = new MenuItem("Connection settings");
        connectionSettingsMenu.setEnabled(true);
        connectionSettingsMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ConnectionSettingsDialog tempConnectionSettingsDialog = new ConnectionSettingsDialog(mainFrame);
                tempConnectionSettingsDialog.setVisible(true);
                tempConnectionSettingsDialog.setModal(true);
            }
        });
        settingsMenu.add(connectionSettingsMenu);

        Menu helpMenu = new Menu("Help", false);
        MenuItem aboutGameMenu = new MenuItem("About");
        aboutGameMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                AboutDialog tempAboutDialog = new AboutDialog();
                tempAboutDialog.setVisible(true);
                tempAboutDialog.setModal(true);
            }
        });
        helpMenu.add(aboutGameMenu);

        Menu gameMenu = new Menu("Game", false);
        MenuItem newGameMenu = new MenuItem("New game");
        newGameMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showStartNewGameWindow();
            }
        });
        gameMenu.add(newGameMenu);

        gameMenu.addSeparator();

        MenuItem exitGameMenu = new MenuItem("Exit game");
        exitGameMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                mainFrame.setVisible(false);
                System.exit(0);
            }
        });
        gameMenu.add(exitGameMenu);

        mainMenuBar.add(gameMenu);
        mainMenuBar.add(settingsMenu);
        mainMenuBar.add(helpMenu);
        mainFrame.setMenuBar(mainMenuBar);
    }

    private void someWait() {
        long startTime = new Date().getTime();
        long currTime = new Date().getTime();
        while (currTime < startTime + 5000) {
            currTime = new Date().getTime();
        }
    }

    //TODO make something with this terrible initConnection in many classes
    /**
     * Initializing connection, which is using Hessian
     */
    private void initConnection() {
        String url = "http://81.22.135.175:8080/gameServer";

        factory = new HessianProxyFactory();
        try {
            gameServer = (GameServer) factory.create(GameServer.class, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void reInitMainFrame() {
        logger.debug("Setting frame params");
        mainFrame.setSize(1024, 768);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().setBackground(new Color(0, 150, 0));

        logger.debug("Setting other params");
        //getPrimaryNetworkData();
        initConnection();
        makeGUI();
        exchanger = new FramesExchanger();
        activeCardsDesc = new ActiveCardsDesc();

        logger.debug("Showing frame");
        mainFrame.setVisible(true);

        /*logger.debug("Creating NewGame frame");
        startGameFrame = new StartGameWindow(exchanger);
        logger.debug("Showing NewGame frame...");
        new Thread(this, "Start MainGameWindow change").start();
        startGameFrame.setVisible(true);*/
        showStartNewGameWindow();
    }

    private boolean noConnectionPrevention(HessianRuntimeException hre) {
        logger.error("Cann't connect to 81.22.135.175:8080/gameServer " + hre);
        Object[] options = {"Yes",
                "No, exit the game"};

        switch (JOptionPane.showOptionDialog(mainFrame, "Cann't connect to game server. Check your firewall settings or in-game proxy settings. Retry connection?", "Error",
                JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1])) {
            case JOptionPane.YES_OPTION:
                return true;
            case JOptionPane.NO_OPTION:
                logger.debug("Exiting the game...");
                mainFrame.validate();
                mainFrame.setVisible(false);
                System.exit(0);
                return false;
            default:
                return true;
        }
    }

    //TODO remake getting cards, put in personal method
    public void run() {
        boolean retryConnection = true;
        switch (exchanger.get()) {
            case EXIT_GAME:
                logger.debug("Exiting program");
                mainFrame.setVisible(false);
                System.exit(0);
            case EXIT_START_GAME:
                /*mainFrame.setVisible(false);
                System.exit(0);*/
                logger.debug("Closing startNewGame dialog");
                startGameFrame.setVisible(false);
                startGameFrame.dispose();
                break;
            case JOIN_GAME_ACCEPTED:
                plName = SECOND_PL;
                gameType = BEATING_OFF;
                logger.debug("plNum is second");
                getPrimaryGameData();
                break;
            case NEW_GAME_ACCEPTED:
                plName = FIRST_PL;
                gameType = LEADING;
                logger.debug("plNum is first");
                getPrimaryGameData();
                break;
            case END_GAME_REACHED:
                logger.debug("starting new game...");
                reInitMainFrame();
                break;
            default:
                logger.error("Unexpected exchanger value");
                break;
        }
    }

    private void getPrimaryGameData() {
        logger.debug("Getting server ID...");
        boolean retryConnection = true;
        serverID = startGameFrame.getServerID();
        logger.debug("Got ID " + serverID);
        logger.debug("Getting cardBatch");
        while (retryConnection) {
            try {
                activeCardsDesc = gameServer.getActiveCards(serverID, plName);
                retryConnection = false;
            } catch (HessianRuntimeException hre) {
                retryConnection = noConnectionPrevention(hre);
            }
        }
        logger.debug("Got my cards:" + activeCardsDesc.firstPLCards);
        logger.debug("Cards on table" + activeCardsDesc.cardsOnTable);
        refreshTable();
    }

    private void getPrimaryNetworkData() {
        //setting
        XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("applicationContext.xml"));
        ConnectionServiceImpl connectionService = ConnectionServiceImpl.class.cast(factory.getBean("setHessianParams"));
        connectionService.showValues();
    }

    private void refreshTable() {
        TableVisualization tableVisualization = new TableVisualization(plName, serverID, exchanger);
        activeCardsDesc.selectedCard = 0;
        //tableVisualization.drawTable(activeCardsDesc, mainFrame, gameType);
        tableVisualization.drawTable(activeCardsDesc, mainFrame, gameType);
        new Thread(this, "Start TableVisualization change").start();
    }

    private void makeGUI() {
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logger.debug("Closing frame...");
            }
        });

        mainFrame.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent we) {
                if (startGameFrame != null) {
                    startGameFrame.toFront();
                    startGameFrame.checkChildFocus();
                }
            }
        });


    }

}
