/**
 *
 * Main.java - Entry point for Lamport's Distributed Mutual Exclusion Algorithm
 * @author: Saurav Sharma
 *
 * @args: <configuration filepath> <server or client> <server or client ID>
 *
 */

package com.utd.aos;

import com.utd.aos.client.TCPClient;
import com.utd.aos.server.TCPServer;
import com.utd.aos.util.ReadPropertyFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {

        if(args.length == 3) {
            try {
                String clientOrServer = args[1];
                String filePath = args[0];
                String clientOrServerID = args[2];
                Properties prop = ReadPropertyFile.readProperties(filePath);

                if (clientOrServer.equalsIgnoreCase("client")) {
                    TCPClient client = new TCPClient(prop, clientOrServerID);
                    client.startClient();
                }
                else if (clientOrServer.equalsIgnoreCase("server")){
                    TCPServer server = new TCPServer(prop, clientOrServerID);
                    server.startServer();
                }
                else {
                    System.out.println("Only 'server' or 'client' is accepted as valid input");
                }
            }
            catch (Exception ex)    {
                ex.printStackTrace();
            }
        }
        else    {
            System.out.println("Invalid Syntax");
            System.out.println("Usage: <property file> <server or client> <server/client id>");
        }
    }
}
