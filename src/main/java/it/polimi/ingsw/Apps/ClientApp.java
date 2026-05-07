package it.polimi.ingsw.Apps;

import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import it.polimi.ingsw.Networking.RMI.RMIServerAdapter;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.Networking.Socket.SocketServerAdapter;
import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.View.GUIView.MainGUIView;
import it.polimi.ingsw.View.ViewInterface;
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

        ServerConnection connection;

        String protocol;
        do {
            System.out.println("Choose connection protocol: Socket / RMI");
            protocol = scanner.nextLine();
        }while(!protocol.equalsIgnoreCase("SOCKET") && !protocol.equalsIgnoreCase("RMI"));
        switch (protocol.toUpperCase()) {
            case "SOCKET" ->
                    connection = new SocketServerAdapter(ServerConfigs.DEFAULT_SOCKET_SERVER_IP_ADDR, ServerConfigs.DEFAULT_SOCKET_SERVER_PORT, clientController);
            case "RMI" ->
                    connection = new RMIServerAdapter(ServerConfigs.DEFAULT_RMI_IP_ADDR, ServerConfigs.DEFAULT_RMI_SERVER_PORT, clientController);
            default -> {
                System.out.println("protocollo non valido");
                return;
            }
        }
        connection.connect();
        clientController.bindConnection(connection);        //binding connessione-controller cosi' che parli con il server

        String UIType;
        do{
            System.out.println("Choose visualization method:\nTUI\nGUI");
            UIType = scanner.nextLine();
        }while(!UIType.equalsIgnoreCase("TUI") && !UIType.equalsIgnoreCase("GUI"));

        ViewInterface view;      //interfaccia implementata da GUI e TUI
        switch(UIType.toUpperCase()){
            case "TUI" -> view = new TUIView(clientController);
            case "GUI"-> view = new MainGUIView();
            default -> {
                System.out.println("view not supported");
                return;
            }// GUI start
        }
        clientController.bindView(view);
        //view.runView();

        /*
         * Al player viene chiesto il nickname da usare durante la partita
         */
        //System.out.println("Scegli nickname:");
        //String playerName = scanner.nextLine();
        //clientController.setPlayerName(playerName);
        //si fara' una cosa molto simile a quella sopra per istanziare TUI o GUI:


    }
}
