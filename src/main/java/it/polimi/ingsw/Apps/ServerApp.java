package it.polimi.ingsw.Apps;

import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import it.polimi.ingsw.Networking.RMI.RMIServer;
import it.polimi.ingsw.Networking.Shared.LeaderboardDAO;
import it.polimi.ingsw.Networking.Shared.ServerController;
import it.polimi.ingsw.Networking.Socket.SocketServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

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

        // MESOS DATABASE SETUP AND CONNECTION

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nDo you want to connect this server to an external MySQL Database for match history? [Y/n]");
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("Y") || answer.isEmpty()) {
            System.out.println("--- MESOS DATABASE CONFIGURATION ---");
            System.out.println("\nPress ENTER to select the default options.");

            System.out.print("\nEnter the Database URL (default is: jdbc:mysql://localhost:3306/mesos_ranking_db): ");
            String inputURL = scanner.nextLine();
            String URL = inputURL.isEmpty() ? "jdbc:mysql://localhost:3306/mesos_ranking_db" : inputURL;

            System.out.print("Enter the username (default is: root): ");
            String inputUser = scanner.nextLine();
            String USER = inputUser.isEmpty() ? "root" : inputUser;

            System.out.print("Enter the Password (default is: Mesos_ranking_db_2026!): ");
            String inputPassword = scanner.nextLine();
            String PASSWORD = inputPassword.isEmpty() ? "Mesos_ranking_db_2026!" : inputPassword;
            System.out.println();
            try {
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Successfully connected to MySQL mesos database!");

                mainController.setDBConnected(true);
                mainController.setLeaderboardDAO(new LeaderboardDAO(connection));
            } catch (SQLException e) {
                System.out.println("[ERROR]  Unable to connect to MySQL Mesos database: " + e.getMessage());
            }
        }
        System.out.println("Server is running...");
    }
}
