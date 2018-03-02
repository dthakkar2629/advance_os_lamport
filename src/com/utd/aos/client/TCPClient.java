/**
 *
 * TCPClient.java -
 * @author: Saurav Sharma
 *
 */

package com.utd.aos.client;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class TCPClient {

    class ClientClock   {
        private int clientID;
        private int localClock;

        ClientClock(int localClock, int clientID)   {
            this.clientID = clientID;
            this.localClock = localClock;
        }
    }

    class ClientInfo  {
        private volatile List<String> serverAddress = new ArrayList<>();
        private volatile List<String> clientAddress = new ArrayList<>();
        private volatile int clientport, serverport;
        private volatile Socket[] clients;
        private volatile int localClock = 1;
        private volatile int clientID;
        Random rand = new Random();
        private volatile int noOfRequests;
        private volatile ArrayList<String> typeOfOperations;
        volatile Boolean isRunning = true;
        volatile PriorityQueue<ClientClock> fileQueue_1;
        volatile PriorityQueue<ClientClock> fileQueue_2;
        volatile PriorityQueue<ClientClock> fileQueue_3;
        volatile PriorityQueue<ClientClock> fileQueue_4;
        volatile PriorityQueue<ClientClock> fileQueue_5;
    }
    final ClientInfo clientInfo = new ClientInfo();

    public TCPClient(Properties properties, String clientID) {


        for(int i = 1; i <= Integer.valueOf(properties.getProperty("noofserver")); i++)
            this.clientInfo.serverAddress.add(properties.getProperty("server" + i));

        for(int i = 1; i <= Integer.valueOf(properties.getProperty("noofclient")); i++)
            this.clientInfo.clientAddress.add(properties.getProperty("client"+i));

        this.clientInfo.serverport = Integer.valueOf(properties.getProperty("serverport"));
        this.clientInfo.clientport = Integer.valueOf(properties.getProperty("clientport"));
        this.clientInfo.clients = new Socket[this.clientInfo.serverAddress.size()];
        this.clientInfo.noOfRequests = Integer.valueOf(properties.getProperty("noofrequests"));
        this.clientInfo.typeOfOperations = new ArrayList<>();
        this.clientInfo.typeOfOperations.add("READ: ");
        this.clientInfo.typeOfOperations.add("WRITE: ");
        this.clientInfo.typeOfOperations.add("ENQUIRY");
        this.clientInfo.clientID = Integer.parseInt(clientID);



        Comparator<ClientClock> comparator = new Comparator<ClientClock>() {
            @Override
            public int compare(ClientClock o1, ClientClock o2) {
                if(o1.localClock < o2.localClock)
                    return 1;
                else if (o1.localClock == o2.localClock)    {
                    if(o1.clientID < o2.clientID)
                        return 1;
                    else
                        return -1;
                }
                else    {
                    return -1;
                }

            }
        };
        this.clientInfo.fileQueue_1 = new PriorityQueue(comparator);
        this.clientInfo.fileQueue_2 = new PriorityQueue(comparator);
        this.clientInfo.fileQueue_3 = new PriorityQueue(comparator);
        this.clientInfo.fileQueue_4 = new PriorityQueue(comparator);
        this.clientInfo.fileQueue_5 = new PriorityQueue(comparator);


    }

    public void startClient()   {
        try {
            TCPClientListener listener = new TCPClientListener(clientInfo.clientport);
            TCPClientProcess process = new TCPClientProcess();
            System.out.println("Starting Client " + clientInfo.clientID);
            System.out.println("-----------------------------------------------------------------");
            new Thread(listener).start();
            Thread.sleep(10000);
            new Thread(process).start();
        }   catch (IOException ioEx)    {
            ioEx.printStackTrace();
        }   catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public class TCPClientListener implements Runnable {

        private ServerSocket serverSocket;

        public TCPClientListener(int port) throws IOException {
            this.serverSocket = new ServerSocket(port);
        }

        @Override
        public void run()  {
            try {
                System.out.println("Listener Thread: Waiting for client on port " +
                        serverSocket.getLocalPort() + "...");
                while (clientInfo.isRunning) {
                    clientInfo.localClock++;
                    Socket server = serverSocket.accept();
                    String clientIP = server.getInetAddress().toString().substring(1);
                    DataInputStream in = new DataInputStream(server.getInputStream());
                    DataOutputStream out = new DataOutputStream(server.getOutputStream());
                    String msg = in.readUTF();
                    //System.out.println("Listener Thread: " + msg + " from " + clientIP);
                    int fileName = Character.getNumericValue(msg.split(";")[0].split(":")[1].trim().charAt(0));
                    int clientClockValue = Integer.parseInt(msg.split(";")[1].split(":")[1].trim());
                    int clientID = Integer.parseInt(msg.split(";")[2].split(":")[1].trim());
                    if(msg.contains("REQUESTING")) {
                        switch (fileName)   {
                            case 1: clientInfo.fileQueue_1.add(new ClientClock(clientClockValue, clientID));
                                    break;
                            case 2: clientInfo.fileQueue_2.add(new ClientClock(clientClockValue, clientID));
                                break;
                            case 3: clientInfo.fileQueue_3.add(new ClientClock(clientClockValue, clientID));
                                break;
                            case 4: clientInfo.fileQueue_4.add(new ClientClock(clientClockValue, clientID));
                                break;
                            case 5: clientInfo.fileQueue_5.add(new ClientClock(clientClockValue, clientID));
                                break;
                            default:
                                System.out.println("Wrong Filename provided " + fileName);
                                break;
                        }
                        System.out.println("Listener Thread: Adding to Queue for file " + fileName + " ... ");
                    }
                    else    {
                        switch (fileName)   {
                            case 1: clientInfo.fileQueue_1.poll();
                                break;
                            case 2: clientInfo.fileQueue_2.poll();
                                break;
                            case 3: clientInfo.fileQueue_3.poll();
                                break;
                            case 4: clientInfo.fileQueue_4.poll();
                                break;
                            case 5: clientInfo.fileQueue_5.poll();
                                break;
                            default:
                                System.out.println("Wrong Filename provided " + fileName);
                                break;
                        }
                        System.out.println("Listener Thread: " + fileName + " Polling... ");
                    }
                    if (clientClockValue > clientInfo.localClock)
                        clientInfo.localClock = clientClockValue + 1;
                    out.writeUTF("Acknowledge; Local Clock: " + clientInfo.localClock);
                }
            }
            catch (Exception ex)    {
                ex.printStackTrace();
            }
        }
    }

    public class TCPClientProcess implements Runnable   {

        @Override
        public void run()   {
            try {
                int serverID = 0, operationID, fileID;
                int i = 0;
                Socket[] clients = new Socket[clientInfo.clientAddress.size()];

                Thread.sleep(2000);
                while(i < clientInfo.noOfRequests) {
                    i++;
                    System.out.println();
                    System.out.println("---------------------------------------------------");
                    System.out.println("Request No: " + i);
                    serverID = clientInfo.rand.nextInt(clientInfo.serverAddress.size());
                    operationID = clientInfo.rand.nextInt(3);
                    fileID = clientInfo.rand.nextInt(5) + 1;
                    if(operationID != 2) {
                        System.out.println("Sending " + clientInfo.typeOfOperations.get(operationID) + " request to Server ");
                        for (int clID = 0; clID < clientInfo.clientAddress.size(); clID++) {
                            clients[clID] = new Socket(clientInfo.clientAddress.get(clID).trim(), clientInfo.clientport);
                            OutputStream outToServer = clients[clID].getOutputStream();
                            DataOutputStream out = new DataOutputStream(outToServer);
                            InputStream inFromServer = clients[clID].getInputStream();
                            DataInputStream in = new DataInputStream(inFromServer);

                            out.writeUTF("REQUESTING FOR File: " + fileID + ".txt; Local Clock: " + clientInfo.localClock + "; Client ID: " + clientInfo.clientID + " : " + clients[clID].getInetAddress());
                            String msg = in.readUTF();
                            int clockValue = Integer.valueOf(msg.split(";")[1].split(":")[1].trim());
                            if(clockValue > clientInfo.localClock)
                                clientInfo.localClock = clockValue + 1;

                            out.close();
                            in.close();
                            clients[clID].close();
                        }
                        System.out.println("Got Acknowledge from all Clients");
                        int clockValueToUpdate = clientInfo.localClock;
                        if(operationID == 1) {
                            boolean updated = false;
                            boolean conditionMet = false;
                            switch (fileID)    {
                                case 1:
                                    if(clientInfo.fileQueue_1.peek().clientID == clientInfo.clientID)   {
                                        conditionMet = true;
                                        clientInfo.fileQueue_1.poll();
                                    }
                                    break;
                                case 2:
                                    if(clientInfo.fileQueue_2.peek().clientID == clientInfo.clientID)   {
                                        conditionMet = true;
                                        clientInfo.fileQueue_2.poll();
                                    }
                                    break;
                                case 3:
                                    if(clientInfo.fileQueue_3.peek().clientID == clientInfo.clientID)   {
                                        conditionMet = true;
                                        clientInfo.fileQueue_3.poll();
                                    }
                                    break;
                                case 4:
                                    if(clientInfo.fileQueue_4.peek().clientID == clientInfo.clientID)   {
                                        conditionMet = true;
                                        clientInfo.fileQueue_4.poll();
                                    }
                                    break;
                                case 5:
                                    if(clientInfo.fileQueue_5.peek().clientID == clientInfo.clientID)   {
                                        conditionMet = true;
                                        clientInfo.fileQueue_5.poll();
                                    }
                                    break;
                            }

                            while (!updated && conditionMet) {
                                for (int serID = 0; serID < clientInfo.serverAddress.size(); serID++) {
                                    clientInfo.clients[serID] = new Socket(clientInfo.serverAddress.get(serID).trim(), clientInfo.serverport);
                                    OutputStream outToServer = clientInfo.clients[serID].getOutputStream();
                                    DataOutputStream out = new DataOutputStream(outToServer);
                                    InputStream inFromServer = clientInfo.clients[serID].getInputStream();
                                    DataInputStream in = new DataInputStream(inFromServer);

                                    out.writeUTF("Client ID: " + clientInfo.clientID + "; " + clientInfo.typeOfOperations.get(operationID) + fileID + ".txt; Local Clock: " + clockValueToUpdate);

                                    clientInfo.clients[serID].close();
                                    inFromServer.close();
                                    in.close();
                                }
                                System.out.println("File Successfully updated");
                                updated = true;
                            }
                        }
                        else    {
                            clientInfo.clients[serverID] = new Socket(clientInfo.serverAddress.get(serverID).trim(), clientInfo.serverport);
                            OutputStream outToServer = clientInfo.clients[serverID].getOutputStream();
                            DataOutputStream out = new DataOutputStream(outToServer);
                            InputStream inFromServer = clientInfo.clients[serverID].getInputStream();
                            DataInputStream in = new DataInputStream(inFromServer);

                            System.out.println("Sending " + clientInfo.typeOfOperations.get(operationID) + " request to " + clientInfo.clients[serverID].getRemoteSocketAddress());
                            out.writeUTF("Client ID: " + clientInfo.clientID + "; " + clientInfo.typeOfOperations.get(operationID) + fileID + ".txt");

                            System.out.println("Response from Server: " + in.readUTF());
                            clientInfo.clients[serverID].close();
                            inFromServer.close();
                            in.close();
                        }
                        System.out.println("Sending release message");
                        for (int clID = 0; clID < clientInfo.clientAddress.size(); clID++) {
                            clients[clID] = new Socket(clientInfo.clientAddress.get(clID).trim(), clientInfo.clientport);
                            OutputStream outToServer = clients[clID].getOutputStream();
                            DataOutputStream out = new DataOutputStream(outToServer);
                            InputStream inFromServer = clients[clID].getInputStream();
                            DataInputStream in = new DataInputStream(inFromServer);

                            out.writeUTF("RELEASE FOR File: " + fileID + ".txt; Local Clock: " + clientInfo.localClock + "; Client ID: " + clientInfo.clientID + " : " + clients[clID].getInetAddress());
                           // System.out.println("Response from Client: " + in.readUTF());

                            out.close();
                            in.close();
                            clients[clID].close();
                        }
                    }
                    else {
                        clientInfo.clients[serverID] = new Socket(clientInfo.serverAddress.get(serverID).trim(), clientInfo.serverport);
                        OutputStream outToServer = clientInfo.clients[serverID].getOutputStream();
                        DataOutputStream out = new DataOutputStream(outToServer);
                        InputStream inFromServer = clientInfo.clients[serverID].getInputStream();
                        DataInputStream in = new DataInputStream(inFromServer);

                        System.out.println("Sending " + clientInfo.typeOfOperations.get(operationID) + " request to " + clientInfo.clients[serverID].getRemoteSocketAddress());
                        out.writeUTF("Client ID: " + clientInfo.clientID + "; " + clientInfo.typeOfOperations.get(operationID));

                        System.out.println("Response from Server: " + in.readUTF());
                        clientInfo.clients[serverID].close();
                        inFromServer.close();
                        in.close();
                    }
                    clientInfo.localClock++;
                    System.out.println("Request Over");
                    System.out.println("-------------------------------------");
                    Thread.sleep(1000);
                }
                System.out.println();
                System.out.println("Processing Done");
                Thread.sleep(10000);
            }
            catch(ConnectException conEx)   {
                System.out.println("Server not up yet. Please start servers before starting client" + conEx.getMessage());
            }
            catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
            finally {
                clientInfo.isRunning = false;
            }
        }
    }

}
