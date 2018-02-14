/**
 *
 * TCPServer.java -
 * @author  Saurav Sharma
 *
 */

package com.utd.aos.server;

import com.utd.aos.util.ReadFile;
import com.utd.aos.util.WriteFile;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TCPServer implements Runnable{

    private ServerSocket serverSocket;
    private int clientConnected;
    private String directoryPath;

    public TCPServer(int port, String directoryPath) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.clientConnected = 0;
        this.directoryPath = directoryPath;
    }

    public void run()   {
        while(true) {
            try {
                System.out.println("Waiting for client on port " +
                        serverSocket.getLocalPort() + "...");

                Socket server = serverSocket.accept();
                clientConnected++;

                System.out.println("Just connected to " + server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());

                System.out.println(in.readUTF());

                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                ExecutorService pool = Executors.newFixedThreadPool(3);
                Callable<String> readFileCallable = new ReadFile(directoryPath, "file_1.txt");
                Callable<Boolean> writeFileCallable = new WriteFile(directoryPath, "file_1.txt", "testing");

                Future<Boolean> futureWrite = pool.submit(writeFileCallable);
                Boolean flag = futureWrite.get();
                out.writeUTF("Updated file : " + flag);
                Future<String> future = pool.submit(readFileCallable);
                String msg = future.get();
                System.out.println("File: " + msg);
                out.writeUTF(msg);
                out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress()
                        + "\nGoodbye!");

                System.out.println("Total number of client connected: " + clientConnected);
                // server.close();

            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (Exception ex)  {
                ex.printStackTrace();;
                break;
            }
        }
    }
}
