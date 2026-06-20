package it.polimi.ingsw.Utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

/**
 * This class is used to ensure that the IP address
 * entered by the user is properly written.
 */
public class ConnectionValidator {
    public boolean checkIP(String IPAddr){
        //(from 1 to 3 digits followed by a dot) 3 times + (from 1 to 3 digits)
        if(IPAddr.isEmpty()) return true;
        Pattern regex = Pattern.compile("^([0-9]{1,3}\\.){3}[0-9]{1,3}$");
        Matcher matcher = regex.matcher(IPAddr);

        if(matcher.matches()){
            //take individual 1 to 3 digits number and checkIP if they are invalid
            String[] IPGroup = IPAddr.split("\\.");
            if(Arrays.stream(IPGroup).noneMatch(group -> parseInt(group) > 255))
                return true;

            return false;
        }
        return false;
    }

    public boolean checkPort(String inputPort){
        if(inputPort.isEmpty()) return true;
        int port;
        try {
            port = parseInt(inputPort);
        } catch (NumberFormatException e) {
            return false;
        }

        if(port >= 0 && port <= 65353) return true;

        return false;
    }
}
