package ua.com.fland.durak.client;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import org.apache.log4j.Logger;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;

/**
 * @author Maxim Bondarenko
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
public class NewGameServerWindow extends JDialog implements Runnable {
    private static final Logger logger = Logger.getLogger(NewGameServerWindow.class);

    private HessianProxyFactory factory;
    private GameServer gameServer;
    private int gameStatus;

    private String waitingServerID;
    private String serverID;

    private int selectedTimeout;

    private final static int PL_WAITING = 0;
    private final static int NORMAL = 1;
    private final static int NEW_GAME_CANCELED = 2;
    private final static int NEW_GAME_ACCEPTED = 3;

    private final static String WAITING_SERVER_TIMEOUT = "-waitingServerTimeoutReached";

    private FramesExchanger exchanger;

    private final static String url = "http://81.22.135.175:8080/gameServer";

    private void fillTimeoutValues() {
        timeOutValue.addItem("30 sec");
        timeOutValue.addItem("1 min");
        timeOutValue.addItem("3 min");
        timeOutValue.addItem("5 min");
        timeOutValue.addItem("10 min");
    }

    public NewGameServerWindow(FramesExchanger exchanger) {
        this.exchanger = exchanger;

        logger.debug("Creating NewGameServerWindow");
        initComponents();
        //this.setModal(true);

        initConnection();
        fillTimeoutValues();
        gameStatus = NORMAL;
        waitingServerID = "";
    }

    //TODO make something with this terrible initConnection in many classes
    private void initConnection() {
        //url = "http://81.22.135.175:8080/gameServer";
        //String url = "http://127.0.0.1:8080/gameServer";

        factory = new HessianProxyFactory();
        try {
            gameServer = (GameServer) factory.create(GameServer.class, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void cancelButtonMouseClicked(MouseEvent e) {
        if (gameStatus == PL_WAITING) {
            logger.debug("Removing server...");
            try {
                gameServer.removeGameServer(waitingServerID);
            } catch (HessianRuntimeException hre) {
                connectionExceptionCaught(hre);
            }
        }
        this.setModal(false);
        //this.setModalityType();
        exchanger.put(NEW_GAME_CANCELED);

        this.setVisible(false);
        this.dispose();
        logger.debug("NewGameServerWindow disposed");
    }

    private void connectionExceptionCaught(HessianRuntimeException hre) {
        logger.error("Cann't connect to " + url + " " + hre);
        JOptionPane.showMessageDialog(this, "Cann't connect to game server. Check your firewall settings or in-game proxy settings.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void okButtonMouseClicked(MouseEvent e) {
        if (gameStatus == NORMAL) {
            try {
                logger.debug("Getting entered params");
                if (gameName.getText().equals("") | gameName.getText() == null | (gameName.getText()).length() > 11 | gameServer.getGamesNames().containsValue(gameName.getText())) {
                    logger.debug("Showing error window");
                    JOptionPane.showMessageDialog(this, "New game server name must be unique and contain more then 0 symbols but less then 12 symbols", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    logger.debug("Sending params to server");
                    //getting timeout
                    switch (timeOutValue.getSelectedIndex()) {
                        case 0:
                            selectedTimeout = 30;
                            break;
                        case 1:
                            selectedTimeout = 60;
                            break;
                        case 2:
                            selectedTimeout = 180;
                            break;
                        case 3:
                            selectedTimeout = 300;
                            break;
                        case 4:
                            selectedTimeout = 600;
                            break;
                        default:
                            selectedTimeout = 60;
                    }

                    waitingServerID = gameServer.addGameServer(gameName.getText(), selectedTimeout);
                    gameStatus = PL_WAITING;
                    statusLabel.setText("Status: Waiting for another player...");
                    okButton.setEnabled(false);
                    new Thread(this, "Client waiting...").start();
                    //this.setVisible(false);
                }
            } catch (HessianRuntimeException hre) {
                connectionExceptionCaught(hre);
            }
        } else {
            logger.debug("Action not performed in not NORMAL status");
        }
    }

    public void run() {
        logger.debug("Waiting for serverID...");
        //gameServer.load();
        serverID = WAITING_SERVER_TIMEOUT;
        while (serverID.equals(WAITING_SERVER_TIMEOUT)) {
            serverID = gameServer.getPair(gameName.getText(), waitingServerID);
            logger.debug("Got serverID " + serverID);

            if (serverID.equals(WAITING_SERVER_TIMEOUT)) {
                logger.debug("Setting up new waiting server...");
                waitingServerID = gameServer.addGameServer(gameName.getText(), selectedTimeout);
            }
        }

        if (!serverID.equals("-waitingServerStopped")) {
            gameServer.removeGameServer(waitingServerID);
            logger.debug(gameServer.getCardBatchValues(serverID));
        }

        gameStatus = NORMAL;
        this.setModal(false);
        this.setVisible(false);
        if (serverID.equals("-waitingServerStopped")) {
            exchanger.put(NEW_GAME_CANCELED);
        } else {
            exchanger.put(NEW_GAME_ACCEPTED);
        }
        this.dispose();
        logger.debug("NewGameServerWindow disposed");
    }

    private void thisWindowClosing(WindowEvent e) {
        if (gameStatus == PL_WAITING) {
            logger.debug("Removing server...");
            gameServer.removeGameServer(waitingServerID);
        }
    }

    public String getServerID() {
        return serverID;
    }

    private void thisWindowClosed(WindowEvent e) {
        /*if (gameStatus == PL_WAITING) {
            logger.debug("Removing server...");
            gameServer.removeGameServer(waitingServerID);
        } */
        //parentW
    }

    private void helpBtnMouseClicked(MouseEvent e){
        logger.debug("help button clicked " + e);
        JOptionPane.showMessageDialog(this, "<html>Enter server name, not less then 1 symbol, but not longer then 12 symbols.<br>" +
                "Choose timeout value. Click button OK and wait until another player connect to this server</html>",
                "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Maxim Bondarenko
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        gameName = new JTextField();
        serverNameLabel = new JLabel();
        timeoutLabel = new JLabel();
        timeOutValue = new JComboBox();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        helpButton = new JButton();
        statusLabel = new JLabel();

        //======== this ========
        setAlwaysOnTop(false);
        setTitle("New Game Server");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                thisWindowClosed(e);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            // JFormDesigner evaluation mark
            dialogPane.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                            "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                            javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                            java.awt.Color.red), dialogPane.getBorder()));
            dialogPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    if ("border".equals(e.getPropertyName())) throw new RuntimeException();
                }
            });

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- serverNameLabel ----
                serverNameLabel.setText("New game name");

                //---- timeoutLabel ----
                timeoutLabel.setText("Timeout");

                //======== buttonBar ========
                {
                    buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                    buttonBar.setLayout(new GridBagLayout());
                    ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 85, 85, 80};
                    ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{1.0, 0.0, 0.0, 0.0};

                    //---- okButton ----
                    okButton.setText("OK");
                    okButton.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            okButtonMouseClicked(e);
                        }
                    });
                    buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- cancelButton ----
                    cancelButton.setText("Cancel");
                    cancelButton.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            cancelButtonMouseClicked(e);
                        }
                    });
                    buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                    //---- helpButton ----
                    helpButton.setText("Help");
                    helpButton.addMouseListener(new MouseAdapter(){
                        @Override
                        public void mouseClicked(MouseEvent e){
                            helpBtnMouseClicked(e);
                        }
                    });
                    buttonBar.add(helpButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                }

                //---- statusLabel ----
                statusLabel.setText("Status: ");

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                        contentPanelLayout.createParallelGroup()
                                .add(contentPanelLayout.createSequentialGroup()
                                        .add(14, 14, 14)
                                        .add(contentPanelLayout.createParallelGroup()
                                                .add(serverNameLabel)
                                                .add(timeoutLabel))
                                        .addPreferredGap(LayoutStyle.RELATED)
                                        .add(contentPanelLayout.createParallelGroup()
                                                .add(gameName, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE)
                                                .add(timeOutValue, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap(24, Short.MAX_VALUE))
                                .add(GroupLayout.TRAILING, contentPanelLayout.createSequentialGroup()
                                        .addContainerGap(15, Short.MAX_VALUE)
                                        .add(buttonBar, GroupLayout.PREFERRED_SIZE, 344, GroupLayout.PREFERRED_SIZE))
                                .add(GroupLayout.TRAILING, contentPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(statusLabel, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                                .addContainerGap())
                );
                contentPanelLayout.setVerticalGroup(
                        contentPanelLayout.createParallelGroup()
                                .add(contentPanelLayout.createSequentialGroup()
                                .add(15, 15, 15)
                                .add(contentPanelLayout.createParallelGroup(GroupLayout.TRAILING)
                                        .add(gameName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .add(serverNameLabel))
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(contentPanelLayout.createParallelGroup(GroupLayout.TRAILING)
                                        .add(timeoutLabel)
                                        .add(timeOutValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(buttonBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, Short.MAX_VALUE)
                                .add(statusLabel)
                                .add(39, 39, 39))
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Maxim Bondarenko
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JTextField gameName;
    private JLabel serverNameLabel;
    private JLabel timeoutLabel;
    private JComboBox timeOutValue;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    private JButton helpButton;
    private JLabel statusLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
