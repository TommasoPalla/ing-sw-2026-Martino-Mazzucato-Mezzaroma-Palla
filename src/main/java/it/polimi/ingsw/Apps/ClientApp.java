package it.polimi.ingsw.Apps;

import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import it.polimi.ingsw.Networking.RMI.RMIServerAdapter;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.Networking.Socket.SocketServerAdapter;
import it.polimi.ingsw.View.ClientController;

import java.util.Scanner;

/**
 * This class must not have any parameters or methods. It is just an entry point
 * It asks if you want to use RMI or TCP connection and creates the right adapter.
 * It does the same thing for TUI or GUI
 */
public class ClientApp {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        ClientController clientController = new ClientController();

        System.out.println("choose connection protocol: Socket / RMI");
        String protocol = scanner.nextLine();
        ServerConnection connection;
        //in futuro si puo' fare un do{}while che prende in input l'ip e la porta desiderata
        //do{}while perche' si prova fintanto che sono ip e porta validi (=> helper class??)
        switch (protocol.toUpperCase()){
            case "SOCKET" -> connection = new SocketServerAdapter(ServerConfigs.DEFAULT_SOCKET_SERVER_IP_ADDR, ServerConfigs.DEFAULT_SOCKET_SERVER_PORT);
            case "RMI" -> connection = new RMIServerAdapter(ServerConfigs.DEFAULT_RMI_SERVER_NAME, ServerConfigs.DEFAULT_RMI_SERVER_PORT, clientController);
            default -> {
                System.out.println("protocollo non valido");
                return;
            }
        }
        connection.connect();

        //si fara' una cosa molto simile a quella sopra per istanziare TUI o GUI:
        /*
        System.out.println("choose visualization method:\n1 -> TUI\n2 -> GUI");
        int view = scanner.nextInt();
        scanner.nextLine();
        View view;      //interfaccia implementata da GUI e TUI
        switch(view){
            ...
        }
        view.start();   //da qui si fa partire la GUI o la TUI e da li si prende l'input
         */


    }
}
