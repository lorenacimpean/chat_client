package ro.utcluj.ssatr.chat.client;

import javax.swing.JFrame;

/**
 *
 * @author lorena.cimpean
 */
public class ClientTest {

    static final String LOCAL_HOST = "127.0.0.1";

    public static void main(String[] args) {
        Client client = new Client(LOCAL_HOST);
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.startRunning();
    }
}
