package it.polimi.ingsw.Utils;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SocketValidatorTest {
    ConnectionValidator socketValidator = new ConnectionValidator();

    @Test
    public void ipTest(){
        assertTrue(socketValidator.checkIP("127.0.0.1"));
        assertTrue(socketValidator.checkIP(""));
        assertTrue(socketValidator.checkIP("123.123.123.123"));
        assertTrue(socketValidator.checkIP("0.0.0.0"));
        assertFalse(socketValidator.checkIP("2"));
        assertFalse(socketValidator.checkIP("256.3.3.3"));
        assertFalse(socketValidator.checkIP("-56.3.3.3"));
    }
    @Test
    public void portTest(){
        assertTrue(socketValidator.checkPort("1099"));
        assertTrue(socketValidator.checkPort(""));
        assertTrue(socketValidator.checkPort("12345"));
        assertTrue(socketValidator.checkPort("0"));
        assertTrue(socketValidator.checkPort("65353"));
        assertFalse(socketValidator.checkPort("-4"));
        assertFalse(socketValidator.checkPort("100000"));
    }
}
