package it.polimi.ingsw.Apps;

import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import it.polimi.ingsw.Networking.RMI.RMIServerAdapter;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.Networking.Socket.SocketServerAdapter;
import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.Utils.ConnectionValidator;
import it.polimi.ingsw.View.GUIView.Gui;
import it.polimi.ingsw.View.GUIView.GuiStart;
import it.polimi.ingsw.View.ViewInterface;
import it.polimi.ingsw.View.TUIView;
import javafx.application.Application;

import java.util.Scanner;

import static java.lang.Integer.parseInt;

/**
 * This class must not have any parameters or methods. It is just an entry point
 * It asks if you want to use RMI or TCP connection and creates the right adapter.
 * It does the same thing for TUI or GUI
 */
public class ClientApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientController clientController = new ClientController();
        ConnectionValidator connectionValidator = new ConnectionValidator();

        ServerConnection connection;

        String protocol;
        do {
            System.out.println("Choose connection protocol: Socket / RMI");
            protocol = scanner.nextLine();
        }while(!protocol.equalsIgnoreCase("SOCKET") && !protocol.equalsIgnoreCase("RMI"));
        switch (protocol.toUpperCase()) {
            case "SOCKET" -> {
                boolean validSocketConnection = false;
                do{
                    System.out.println("Enter the TCP server IP address (Default is " + ServerConfigs.DEFAULT_SOCKET_SERVER_IP_ADDR + "):");
                    String IPAddress = scanner.nextLine();
                    if(IPAddress.isEmpty()) break;
                    validSocketConnection = connectionValidator.checkIP(IPAddress);
                    if(validSocketConnection)
                        ServerConfigs.SOCKET_SERVER_IP_ADDR = IPAddress;
                } while(!validSocketConnection);
                validSocketConnection = false;
                do{
                    System.out.println("Enter the TCP server port (Default is " + ServerConfigs.DEFAULT_SOCKET_SERVER_PORT + "):");
                    String port = scanner.nextLine();
                    if(port.isEmpty()) break;
                    validSocketConnection = connectionValidator.checkPort(port);
                    if(validSocketConnection)
                        ServerConfigs.SOCKET_SERVER_PORT = parseInt(port);
                }while(!validSocketConnection);
                connection = new SocketServerAdapter(ServerConfigs.SOCKET_SERVER_IP_ADDR, ServerConfigs.SOCKET_SERVER_PORT, clientController);
            }
            case "RMI" ->{
                boolean validRMIConnection = false;
                do{
                    System.out.println("Enter the RMI server IP address (Default is " + ServerConfigs.DEFAULT_RMI_IP_ADDR + "):");
                    String IPAddress = scanner.nextLine();
                    if(IPAddress.isEmpty()) break;
                    validRMIConnection = connectionValidator.checkIP(IPAddress);
                    if(validRMIConnection)
                        ServerConfigs.RMI_IP_ADDR = IPAddress;
                } while(!validRMIConnection);
                validRMIConnection = false;
                do{
                    System.out.println("Enter the RMI server port (Default is " + ServerConfigs.DEFAULT_RMI_SERVER_PORT + "):");
                    String port = scanner.nextLine();
                    if(port.isEmpty()) break;
                    validRMIConnection = connectionValidator.checkPort(port);
                    if(validRMIConnection)
                        ServerConfigs.RMI_SERVER_PORT = parseInt(port);
                } while (!validRMIConnection);
                System.out.println("Enter the RMI server name (Default is " + ServerConfigs.DEFAULT_RMI_SERVER_NAME + "):");
                String RMIServerName = scanner.nextLine();
                if (!RMIServerName.isEmpty())
                    ServerConfigs.RMI_SERVER_NAME = RMIServerName;

                //START THE CONNECTION WITH THE PARAMETERS TAKEN FROM STDIN
                connection = new RMIServerAdapter(ServerConfigs.RMI_IP_ADDR, ServerConfigs.RMI_SERVER_PORT, clientController);
            }
            default -> {
                System.out.println("Invalid networking protocol");
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
            case "TUI" -> {
                view = new TUIView(clientController);
                clientController.bindView(view);
            }
            case "GUI" ->  {
                GuiStart.bindController(clientController);
                Application.launch(GuiStart.class, args);
            }
            default -> {
                System.out.println("view not supported");
                return;
            }
        }
    }
}