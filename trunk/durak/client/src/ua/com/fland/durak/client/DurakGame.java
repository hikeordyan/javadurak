package ua.com.fland.durak.client;

import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Fland
 * Date: 25.11.2008
 * Time: 21:26:56
 */
public class DurakGame {
    private static final Logger logger = Logger.getLogger(DurakGame.class);

    public static void main(String[] args) {
        logger.debug("***App started***");

        logger.debug("Init and show main window");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                logger.debug("Starting new thread");
                new MainGameWindow();
            }
        });

        logger.debug("***App stoped***");
    }

}
