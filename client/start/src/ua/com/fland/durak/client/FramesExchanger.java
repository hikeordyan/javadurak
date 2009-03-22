package ua.com.fland.durak.client;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: maxim
 * Date: Jan 28, 2009
 * Time: 9:52:29 PM
 */
public class FramesExchanger {

    private static final Logger logger = Logger.getLogger(FramesExchanger.class);

    private int res;

    synchronized int get(){
        try {
            wait();
        } catch (InterruptedException e) {
            logger.error("Cann't wait()"+e);
        }
        return res;
    }

    synchronized void put(int res){
        this.res = res;
        notify();
    }
}
