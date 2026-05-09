package it.polimi.ingsw.Networking.Configs;

/**
 * Default values of the connection parameters, for both RMI and socket
 */
public class ServerConfigs {
    public static final String DEFAULT_RMI_SERVER_NAME = "RMIServer";
    public static final int DEFAULT_RMI_SERVER_PORT = 1099;
    public static final String DEFAULT_RMI_IP_ADDR = "127.0.0.1";

    public static final String DEFAULT_SOCKET_SERVER_IP_ADDR = "127.0.0.1";
    public static final int DEFAULT_SOCKET_SERVER_PORT = 8080;


    public static String RMI_SERVER_NAME = DEFAULT_RMI_SERVER_NAME;
    public static int RMI_SERVER_PORT = DEFAULT_RMI_SERVER_PORT;
    public static String RMI_IP_ADDR = DEFAULT_RMI_IP_ADDR;

    public static String SOCKET_SERVER_IP_ADDR = DEFAULT_SOCKET_SERVER_IP_ADDR;
    public static int SOCKET_SERVER_PORT = DEFAULT_SOCKET_SERVER_PORT;
}
