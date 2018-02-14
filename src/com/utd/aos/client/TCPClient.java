/**
 *
 * TCPClient.java -
 * @author: Saurav Sharma
 *
 */

package com.utd.aos.client;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class TCPClient extends Thread{

    private static List<String> serverAddress;
    private static int port;

    public TCPClient(String serverAddress, int port) {
        this.serverAddress = Arrays.asList(serverAddress.split(","));
        this.port = port;
    }

    public void run()   {
        try {
            Socket[] clients = new Socket[serverAddress.size()];
            int i = 0;
            for (String serverName : serverAddress) {
                System.out.println("Connecting to " + serverName + " on port " + port);
                clients[i] = new Socket(serverName.trim(), port);
                i++;
            }
            for(Socket client : clients) {
                System.out.println("Just connected to " + client.getRemoteSocketAddress());
                OutputStream outToServer = client.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);

                out.writeUTF("Hello from " + client.getLocalSocketAddress());
                InputStream inFromServer = client.getInputStream();
                DataInputStream in = new DataInputStream(inFromServer);

                System.out.println("Server says " + in.readUTF());
                while(true) {

                }
            }
        }
        catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
