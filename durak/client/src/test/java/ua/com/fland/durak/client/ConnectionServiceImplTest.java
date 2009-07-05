package ua.com.fland.durak.client;

import org.junit.Test;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Fland
 * Date: 27.11.2008
 * Time: 20:17:41
 */
public class ConnectionServiceImplTest {
    private static final Logger logger = Logger.getLogger(ConnectionServiceImplTest.class);

    @Test
    public void testServerUrlNull() {
        logger.info("Null serverUrl test");
//        ConnectionServiceImpl connectionService = new ConnectionServiceImpl();
//        connectionService.setServerUrl(null);
        logger.info("\n");
    }

    @Test
    public void testNormalServerUrl() {
        logger.info("Normal serverUrl test");
//        ConnectionServiceImpl connectionService = new ConnectionServiceImpl();
//        connectionService.setServerUrl("http://localhost:8080/demoFland");
//        connectionService.showValues();
        logger.info("\n");
    }


}
