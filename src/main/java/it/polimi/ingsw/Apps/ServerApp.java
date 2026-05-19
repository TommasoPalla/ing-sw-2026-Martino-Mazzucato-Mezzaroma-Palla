package it.polimi.ingsw.Apps;

import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import it.polimi.ingsw.Networking.RMI.RMIServer;
import it.polimi.ingsw.Networking.Shared.ServerController;
import it.polimi.ingsw.Networking.Socket.SocketServer;

/**
 * This class must not have any parameters or methods. It is just an entry point
 * It starts both an RMI connection and a TCP connection, being able to listen for
 * incoming clients on both protocols
 */

public class ServerApp {
    public static void main(String[] args) throws Exception {
        //UNICO CONTROLLER CON ASSOCIATO IL MODEL
        ServerController mainController = new ServerController();
        System.setProperty("java.rmi.server.hostname", java.net.InetAddress.getLocalHost().getHostAddress());

        try{
            RMIServer rmiServer = new RMIServer(mainController);
            rmiServer.startServer();
            System.out.println("RMI server ready");
        } catch (Exception e){
            System.out.println("[ERROR]  an error occurred during RMI server initialization:\n" + e.getMessage());
        }
        try{
            SocketServer socketServer = new SocketServer(mainController);
            socketServer.startServer(ServerConfigs.DEFAULT_SOCKET_SERVER_PORT);
            System.out.println("TCP server ready");
        } catch (Exception e){
            System.out.println("[ERROR]  an error occurred during TCP server initialization:\n" + e.getMessage());
        }
    }
}
