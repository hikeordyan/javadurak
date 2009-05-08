package ua.com.fland.durak.client;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: maxim
 * Date: May 7, 2009
 * Time: 3:41:13 PM
 */
public class WaitingDialog extends JDialog implements Runnable {
    private static final Logger logger = Logger.getLogger(WaitingDialog.class);

    private JPanel mainFrame;

    private GameServer gameServer;
    private byte plName;
    private String serverID;
    private int cardNum;
    /**
     * Hessian factory for Hessian connection
     */
    private HessianProxyFactory factory;
    ActiveCardsDesc activeCardsDesc;
    boolean stop;

    public WaitingDialog(JPanel mainFrame) {
        this.mainFrame = mainFrame;
        activeCardsDesc = new ActiveCardsDesc();
        initConnection();
    }

    private void initConnection() {
        String url = "http://81.22.135.175:8080/gameServer";

        factory = new HessianProxyFactory();
        try {
            gameServer = (GameServer) factory.create(GameServer.class, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void showDialog(JPanel mainFrame) {
        //mainFrame.repaint();
        mainFrame.revalidate();
        mainFrame.setVisible(false);
    }

    public ActiveCardsDesc openDialog(JPanel mainFrame, String serverID, byte plName, int cardNum) {
        //this.mainFrame = mainFrame;
        this.serverID = serverID;
        this.plName = plName;
        this.cardNum = cardNum;
        stop = false;
        /*mainFrame.repaint();
        mainFrame.revalidate();*/
        /*mainFrame.setVisible(false);*/
        mainFrame.setVisible(true);
        new Thread(this, "Start waiting dialog").start();
        while (!stop) {

        }
        return activeCardsDesc;
    }

    public void run() {
        activeCardsDesc = gameServer.setLastMove(serverID, plName, cardNum);
        stop = true;
    }
}
