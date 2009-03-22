package ua.com.fland.durak.client;

import com.caucho.hessian.client.HessianProxyFactory;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;

import ua.com.fland.durak.client.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: Fland
 * Date: 27.11.2008
 * Time: 20:17:11
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
