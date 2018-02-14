/**
 *
 * Main.java -
 * @author: Saurav Sharma
 *
 */

package com.utd.aos;

import com.utd.aos.client.TCPClient;
import com.utd.aos.server.TCPServer;

public class Main {

    public static void main(String[] args) {

        if(args.length > 1) {
            try {
                String clientOrServer = args[0];
                int port = Integer.parseInt(args[1]);
                if (clientOrServer.equalsIgnoreCase("client")) {
                    if(args.length == 3) {
                        String serverAddress = args[2];
                        TCPClient client = new TCPClient(serverAddress, port);
                        Thread clientThread = client;
                        client.start();
                    }
                    else    {
                        System.out.println("Please provide servers address, separated by comma");
                    }
                }
                else if (clientOrServer.equalsIgnoreCase("server")){
                    if(args.length == 3) {
                        String directoryPath = args[2];
                        TCPServer server = new TCPServer(port, directoryPath);
                        Thread serverThread = new Thread(server);;
                        serverThread.start();
                    }
                    else    {
                        System.out.println("Please provide home directory path");
                    }
                }
                else {
                    System.out.println("Only 'server' or 'client' is accepted as valid input");
                }
            }
            catch (NumberFormatException numEx) {
                System.out.println("Please provide integer value for port number");
            }
            catch (Exception ex)    {
                ex.printStackTrace();
            }
        }
        else    {
            System.out.println("Invalid Syntax");
            System.out.println("Usage: <server or client> <port_no> <server address for client instance>");
        }
    }
}
