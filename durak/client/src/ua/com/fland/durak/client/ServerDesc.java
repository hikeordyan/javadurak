package ua.com.fland.durak.client;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: maxim
 * Date: Jan 16, 2009
 * Time: 8:19:23 PM
 */
public class ServerDesc implements Serializable {
    public String serverName;

    public ServerDesc(String serverName, int timeout) {
        this.serverName = serverName;
        this.timeout = timeout;
    }

    public int timeout;
}
