package it.polimi.ingsw.Apps;

import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import it.polimi.ingsw.Networking.RMI.RMIServer;
import it.polimi.ingsw.Networking.Shared.LeaderboardDAO;
import it.polimi.ingsw.Networking.Shared.ServerController;
import it.polimi.ingsw.Networking.Socket.SocketServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class must not have any parameters or methods. It is just an entry point
 * It starts both an RMI connection and a TCP connection, being able to listen for
 * incoming clients on both protocols. Then he connects to the MySQL server to access
 * the Mesos match history database.
 */
public class ServerApp {
    private static RMIServer rmiServer;

    public static void main(String[] args) throws Exception {
        //UNICO CONTROLLER CON ASSOCIATO IL MODEL
        ServerController mainController = new ServerController();

        String realIp = "127.0.0.1";
        try (java.net.DatagramSocket socket = new java.net.DatagramSocket()) {
            socket.connect(java.net.InetAddress.getByName("8.8.8.8"), 10002);
            realIp = socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            System.out.println("You are offline");
        }
        System.setProperty("java.rmi.server.hostname", realIp);
        System.out.println("[SERVER] Server IP address is: " + realIp);

        try{
            rmiServer = new RMIServer(mainController);
            rmiServer.startServer();
            System.out.println("[RMI] Server ready");
        } catch (Exception e){
            System.out.println("[ERROR]  an error occurred during RMI server initialization:\n" + e.getMessage());
        }
        try{
            SocketServer socketServer = new SocketServer(mainController);
            socketServer.startServer(ServerConfigs.DEFAULT_SOCKET_SERVER_PORT);
            System.out.println("[TCP] Server ready");
        } catch (Exception e){
            System.out.println("[ERROR]  an error occurred during TCP server initialization:\n" + e.getMessage());
        }

        // MESOS DATABASE CONNECTION
        String URL = "jdbc:mysql://localhost:3306/mesos_ranking_db";
        String USER = "root";
        String PASSWORD = "Mesos_ranking_db_2026!";
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Successfully connected to MySQL mesos database on port: 3306.");

            mainController.setLeaderboardDAO(new LeaderboardDAO(connection));
        } catch (SQLException e) {
            System.out.println("[ERROR]  Unable to connect to MySQL mesos database:");
            e.printStackTrace();
        }
    }
}
