/*
 * Created by JFormDesigner on Sat Dec 27 17:42:24 EET 2008
 */

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Maxim Bondarenko
 */
public class StartGameWindow extends JDialog implements Runnable {
    private static final Logger logger = Logger.getLogger(StartGameWindow.class);

    private HessianProxyFactory factory;
    private GameServer gameServer;
    private Map<String, String> serversNames;
    private Map<String, Integer> serversTimeouts;
    private Map<String, String> serversIds;
    private Map<String, String> serverNamesTimeouts;

    private NewGameServerWindow newGameServer;

    private int gameType = 0;

    private FramesExchanger exchanger;
    private FramesExchanger mainGameWinExchanger;

    private final static int NEW_SERVER_GAME_TYPE = 1;
    private final static int NORMAL_GAME_TYPE = 0;

    private final static int NEW_GAME_CANCELED = 2;
    private final static int NEW_GAME_ACCEPTED = 3;
    private final static int EXIT_START_GAME = 4;
    private final static int JOIN_GAME_ACCEPTED = 5;
    private final static int EXIT_GAME = 7;

    private String serverID;

    private final static String url = "http://81.22.135.175:8080/gameServer";

    private void fillServerList() {
        //getting lists
        serversNames = new HashMap<String, String>();
        try {
            serversNames = gameServer.getGamesNames();
            logger.debug("serversNames " + serversNames);

            serversTimeouts = new HashMap<String, Integer>();
            serversTimeouts = gameServer.getGamesTimeouts();
            logger.debug("serverTimeouts " + serversTimeouts);

            //filling list in form
            String[] servers = new String[serversNames.size()];
            Iterator serverNamesIter = serversNames.values().iterator();
            Iterator serverTimeoutsIter = serversTimeouts.values().iterator();
            serverNamesTimeouts = new HashMap<String, String>();
            serversIds = new HashMap<String, String>();
            String[] ids = serversNames.keySet().toArray(new String[serversNames.keySet().size()]);
            for (int i = 0; i < serversNames.size(); i++) {
                String serverName = serverNamesIter.next().toString();
                String serverTimeout = serverTimeoutsIter.next().toString();
                servers[i] = serverName;
                serverNamesTimeouts.put(serverName, serverTimeout);
                serversIds.put(serverName, ids[i]);
            }
            avaibleServers.setListData(servers);
        } catch (HessianRuntimeException e) {
            logger.error("Cann't connect to " + url + " " + e);
            switch (JOptionPane.showConfirmDialog(this, "Cann't connect to game server. Check your firewall settings or in-game proxy settings. Exit game?", "Error", JOptionPane.YES_NO_OPTION)) {
                case JOptionPane.YES_OPTION:
                    logger.debug("Closing  StartGame frame");
                    mainGameWinExchanger.put(EXIT_GAME);
                    this.setVisible(false);
                    this.dispose();
                    break;
            }
        }
    }

    private void initConnection() throws MalformedURLException {
        //String url = "http://81.22.135.175:8080/gameServer";
        //String url = "http://127.0.0.1:8080/gameServer";

        factory = new HessianProxyFactory();
        gameServer = (GameServer) factory.create(GameServer.class, url);
    }

    public StartGameWindow(FramesExchanger exchanger) {
        this.mainGameWinExchanger = exchanger;
        this.exchanger = new FramesExchanger();

        initComponents();
        this.setModal(true);

        try {
            initConnection();
            fillServerList();
        } catch (MalformedURLException e) {
            logger.error("Cann't create factory" + e);
        }
    }

    private void cancelButtonMouseClicked(MouseEvent e) {
        logger.debug("Closing  StartGame frame");
        this.setVisible(false);
        mainGameWinExchanger.put(EXIT_START_GAME);
        this.setModal(false);
        this.dispose();
    }


    private void createBtnMouseClicked(MouseEvent e) {
        logger.debug("Creating NewGameServerWindow");
        this.setModal(false);
        newGameServer = new NewGameServerWindow(exchanger);

        /*ServerDesc temp = gameServer.testSerial();
        logger.debug(temp.serverName);
        logger.debug(temp.timeout);*/
        this.setVisible(false);

        logger.debug("Showing NewGameServerWindow");
        gameType = NEW_SERVER_GAME_TYPE;
        new Thread(this, "StartGameWindow Start exchange").start();
        newGameServer.setModal(true);
        newGameServer.setVisible(true);
    }

    public void run() {
        int exchangerRes = exchanger.get();
        switch (exchangerRes) {
            case NEW_GAME_CANCELED:
                fillServerList();
                this.setModal(true);
                this.setVisible(true);
                break;
            case NEW_GAME_ACCEPTED:
                logger.debug("Got cardBatch:" + gameServer.getCardBatchValues(newGameServer.getServerID()));
                serverID = newGameServer.getServerID();
                mainGameWinExchanger.put(NEW_GAME_ACCEPTED);
                break;
            default:
                logger.error("Unexpected value from exchanger" + exchangerRes);
        }
    }

    private void connectBtnMouseClicked(MouseEvent e) {
        if (avaibleServers.getSelectedValue() != null) {
            logger.debug("Trying to connect to server");
            serverID = gameServer.setPair(serversIds.get(avaibleServers.getSelectedValue().toString()));
            logger.debug("Got server ID " + serverID);
            if (serverID.equals("-noSuchWaitingServerName")) {
                logger.debug("Showing error window");
                JOptionPane.showMessageDialog(this, "No \"" + avaibleServers.getSelectedValue().toString() + "\" waiting server name. Select other server.", "Error", JOptionPane.ERROR_MESSAGE);
                fillServerList();
            } else {
                this.setModal(false);
                this.setVisible(false);
                this.dispose();
                mainGameWinExchanger.put(JOIN_GAME_ACCEPTED);
            }
        }
    }

    private void avaibleServersMouseClicked(MouseEvent e) {
        if (avaibleServers.getSelectedValue() != null) {
            String selectedServer = avaibleServers.getSelectedValue().toString();
            String descText = "<html>Server name: " + selectedServer +
                    "<br>Server timeout: " + serverNamesTimeouts.get(selectedServer) + " sec</html>";
            serversDesc.setText(descText);
        }
    }

    public String getServerID() {
        return serverID;
    }

    private void thisWindowActivated(WindowEvent e) {
        /*if (gameType == NEW_SERVER_GAME_TYPE) {
            if (!newGameServer.isVisible()){
                this.setVisible(false);
            }
        } */
    }

    private void refreshListBtnMouseClicked(MouseEvent me) {
        fillServerList();
    }

    public void checkChildFocus() {
        if (newGameServer != null) {
            newGameServer.toFront();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Maxim Bondarenko
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        avaibleServers = new JList();
        listHeadline = new JLabel();
        serversDesc = new JLabel();
        refreshListBtn = new JButton();
        buttonBar = new JPanel();
        connectBtn = new JButton();
        createBtn = new JButton();
        cancelButton = new JButton();
        helpButton = new JButton();

        //======== this ========
        setAlwaysOnTop(false);
        setTitle("Start Game");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                thisWindowActivated(e);
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

                //======== scrollPane1 ========
                {

                    //---- avaibleServers ----
                    avaibleServers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    avaibleServers.setVisibleRowCount(4);
                    avaibleServers.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            avaibleServersMouseClicked(e);
                        }
                    });
                    scrollPane1.setViewportView(avaibleServers);
                }

                //---- listHeadline ----
                listHeadline.setText("Avaible game servers");

                //---- refreshListBtn ----
                refreshListBtn.setText("Refresh list");
                refreshListBtn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        refreshListBtnMouseClicked(e);
                    }
                });

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                        contentPanelLayout.createParallelGroup()
                                .add(contentPanelLayout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(contentPanelLayout.createParallelGroup(GroupLayout.TRAILING)
                                        .add(GroupLayout.LEADING, serversDesc, GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                                        .add(GroupLayout.LEADING, contentPanelLayout.createParallelGroup(GroupLayout.TRAILING, false)
                                        .add(GroupLayout.LEADING, scrollPane1, GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                                        .add(GroupLayout.LEADING, contentPanelLayout.createSequentialGroup()
                                        .add(listHeadline, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.UNRELATED)
                                        .add(refreshListBtn))))
                                .add(80, 80, 80))
                );
                contentPanelLayout.setVerticalGroup(
                        contentPanelLayout.createParallelGroup()
                                .add(contentPanelLayout.createSequentialGroup()
                                .add(9, 9, 9)
                                .add(contentPanelLayout.createParallelGroup(GroupLayout.TRAILING)
                                        .add(listHeadline)
                                        .add(refreshListBtn))
                                .addPreferredGap(LayoutStyle.UNRELATED)
                                .add(scrollPane1, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(serversDesc, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                                .addContainerGap())
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 85, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{1.0, 0.0, 0.0, 0.0};

                //---- connectBtn ----
                connectBtn.setText("Connect");
                connectBtn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        connectBtnMouseClicked(e);
                    }
                });
                buttonBar.add(connectBtn, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- createBtn ----
                createBtn.setText("Create");
                createBtn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        createBtnMouseClicked(e);
                    }
                });
                buttonBar.add(createBtn, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
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
                buttonBar.add(helpButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
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
    private JScrollPane scrollPane1;
    private JList avaibleServers;
    private JLabel listHeadline;
    private JLabel serversDesc;
    private JButton refreshListBtn;
    private JPanel buttonBar;
    private JButton connectBtn;
    private JButton createBtn;
    private JButton cancelButton;
    private JButton helpButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
