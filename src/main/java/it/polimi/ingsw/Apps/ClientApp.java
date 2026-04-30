package it.polimi.ingsw.Apps;

import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import it.polimi.ingsw.Networking.RMI.RMIServerAdapter;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.Networking.Socket.SocketServerAdapter;
import it.polimi.ingsw.Controller.ClientController;
import it.polimi.ingsw.View.TUIView;

import java.util.Scanner;

/**
 * This class must not have any parameters or methods. It is just an entry point
 * It asks if you want to use RMI or TCP connection and creates the right adapter.
 * It does the same thing for TUI or GUI
 */
public class ClientApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientController clientController = new ClientController();

        System.out.println("choose connection protocol: Socket / RMI");
        String protocol = scanner.nextLine();
        ServerConnection connection;
        //in futuro si puo' fare un do{}while che prende in input l'ip e la porta desiderata
        //do{}while perche' si prova fintanto che sono ip e porta validi (=> helper class??)
        switch (protocol.toUpperCase()) {
            case "SOCKET" ->
                    connection = new SocketServerAdapter(ServerConfigs.DEFAULT_SOCKET_SERVER_IP_ADDR, ServerConfigs.DEFAULT_SOCKET_SERVER_PORT);
            case "RMI" ->
                    connection = new RMIServerAdapter(ServerConfigs.DEFAULT_RMI_IP_ADDR, ServerConfigs.DEFAULT_RMI_SERVER_PORT, clientController);
            default -> {
                System.out.println("protocollo non valido");
                return;
            }
        }
        connection.connect();
        clientController.bindConnection(connection);        //binding connessione-controller cosi' che parli con il server
        /*
         * Al player viene chiesto il nickname da usare durante la partita
         */
        //System.out.println("Scegli nickname:");
        //String playerName = scanner.nextLine();
        //clientController.setPlayerName(playerName);
        //si fara' una cosa molto simile a quella sopra per istanziare TUI o GUI:

        System.out.println("choose visualization method:\n1 -> TUI\n2 -> GUI");
        boolean valid = false;
        int UIType = 0;
        while (!valid) {
            UIType = scanner.nextInt();
            scanner.nextLine();
            if (UIType == 1 || UIType == 2) valid = true;
            else System.out.println("invalid choice, please try again...");
        }
        //interfaccia implementata da GUI e TUI
        switch(UIType){
            case 1: new TUIView(clientController);
            case 2:
                // GUI start
        }
    }
}
