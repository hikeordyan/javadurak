package ua.com.fland.durak.client;

import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: maxim
 * Date: May 7, 2009
 * Time: 3:41:13 PM
 */
public class TestDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(TestDialog.class);

    public TestDialog() {
        this.setTitle("test dialog");
        this.setSize(200, 200);
        logger.debug("in test dialog");
    }
}
