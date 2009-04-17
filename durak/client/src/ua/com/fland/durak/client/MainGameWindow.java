package ua.com.fland.durak.client;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: Fland
 * Date: 25.11.2008
 * Time: 21:30:09
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
    private final static int EXIT_GAME = 4;
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

        logger.debug("Setting other params");
        //getPrimaryNetworkData();
        initConnection();
        makeGUI();
        exchanger = new FramesExchanger();
        activeCardsDesc = new ActiveCardsDesc();

        logger.debug("Showing frame");
        mainFrame.setVisible(true);

        logger.debug("Creating NewGame frame");
        new Thread(this, "Start MainGameWindow change").start();
        startGameFrame = new StartGameWindow(exchanger);
        logger.debug("Showing NewGame frame...");
        startGameFrame.setVisible(true);
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

        logger.debug("Creating NewGame frame");
        startGameFrame = new StartGameWindow(exchanger);
        logger.debug("Showing NewGame frame...");
        startGameFrame.setVisible(true);
        new Thread(this, "Start MainGameWindow change").start();
    }

    private boolean noConnectionPrevention(HessianRuntimeException hre) {
        logger.error("Cann't connect to 81.22.135.175:8080/gameServer " + hre);
        Object[] options = {"Yes",
                "No, exit the game"};

        switch (JOptionPane.showOptionDialog(mainFrame, "Cann't connect to game server. Check your firewall settings. Retry connection?", "Error",
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
                mainFrame.setVisible(false);
                System.exit(0);
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
