package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Networking.Shared.ServerController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketServer implements VirtualSocketServer{
    private final List<SocketClientHandler> clients = new ArrayList<>();
    private final ServerController serverController;

    public SocketServer(ServerController serverController){this.serverController = serverController;}

    public void startServer(int port){
        new Thread( () -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    SocketClientHandler handler = new SocketClientHandler(socket, this);
                    new Thread(handler).start();
                }
            } catch (IOException e) {
                System.out.println("Error during TCP server initialization\n" + e.getMessage());
            }
        }).start();

    }

    @Override
    public void connect(SocketClientHandler handler) {
        this.clients.add(handler);
        System.out.println("New TCP client connected");
    }

    @Override
    public void disconnect(SocketClientHandler handler) {
        this.clients.remove(handler);
        System.out.println("TCP client removed");   //magari usare un handler.toString() per includerlo nel log
    }

    @Override
    public void chooseOfferTile() throws Occupied_Tile_Exception {

    }

    @Override
    public void drawCard(boolean fromTopRow, boolean fromBuildings, int index, SocketClientHandler clientHandler) throws Illegal_Draw_Exception {
        serverController.drawCard(clientHandler.getPlayerRecord(), fromTopRow, fromBuildings, index);
    }

    @Override
    public void chooseTotemColor(Color totemColor, SocketClientHandler handler) {
        serverController.chooseTotemColor(handler.getPlayerRecord(), totemColor);
    }
}
