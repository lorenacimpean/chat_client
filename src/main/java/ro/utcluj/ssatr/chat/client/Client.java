package ro.utcluj.ssatr.chat.client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author lorena.cimpean
 */
public class Client extends JFrame {

    private static final int WINDOW_HEIGHT = 300;
    private static final int WINDOW_WIDTH = 300;
    private static final int PORT_NUMBER = 8888;

    private final JTextArea chatWindow;
    private final String serverIp;
    private JTextField userText;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message;

    private Socket connection;

    //constructor
    public Client(String host) {
        super("Instant Messaging App - Client");
        serverIp = host;
        userText = new JTextField();
        ableToType(false);
        userText.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                sendData(event.getActionCommand());
                userText.setText("");
            }
        }
        );
        add(userText, BorderLayout.SOUTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(WINDOW_HEIGHT, WINDOW_WIDTH);
        setVisible(true);
    }

    //connect to svr
    public void startRunning() {
        try {
            connectToServer();
            setupStreams();
            whileChatting();
        } catch (EOFException eof) {
            showMessage("\n *** SERVER ENDED THE CONNECTION... END OF STREAM ***");
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            closeEverything();
        }
    }

    private void connectToServer() throws IOException {
        showMessage("\n *** ATTEMPTING CONNECTION... ***");
        connection = new Socket(InetAddress.getByName(serverIp), PORT_NUMBER);
        showMessage("\n *** CONNECTED TO " + connection.getInetAddress().getHostName().toUpperCase() + "***");

    }

    //get stream to send and receive data
    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        //clear leftover data from the stream
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n *** STREAMS ARE SETUP ***");
    }

    private void whileChatting() throws IOException {

        ableToType(true);
        do {
            try {
                message = (String) input.readObject();
                if (message != null && !message.equals("")) {
                    sendData("\n" + message);
                }

            } catch (ClassNotFoundException ex) {
                showMessage("\n UNKNOWN OBJECT TYPE" + message);
                ex.printStackTrace();
            }
        } while (!message.equals("CLIENT - END"));
    }

    //send messages to server
    private void sendData(String msg) {
        try {
            //output.writeObject("CLIENT - " + msg);
            output.flush();
            if (message != null && !message.isEmpty()) {
                showMessage("\n CLIENT - " + msg);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            chatWindow.append("\n ERROR: Message could not be sent!");
        }
    }

    //update chat window with message
    private void showMessage(final String text) {
        //create new thread that updated the GUI
        SwingUtilities.invokeLater(
                new Runnable() {
            public void run() {
                chatWindow.append(text);
            }
        }
        );
    }

    //close streams and sockets 
    private void closeEverything() {
        showMessage("\n *** CLOSING CONNECTION... ***");
        ableToType(false);
        try {
            output.close();
            input.close();
            connection.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //let the user type into chat box
    private void ableToType(final boolean able) {
        SwingUtilities.invokeLater(
                new Runnable() {
            public void run() {
                userText.setEditable(able);
            }
        }
        );
    }

}
