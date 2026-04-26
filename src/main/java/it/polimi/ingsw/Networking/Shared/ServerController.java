package it.polimi.ingsw.Networking.Shared;

/**
 * This class has all the method of the model and some other
 * It is called by RMIServer and SocketServer, allowing them to modify
 * the model state without directly giving them access to it.
 */

public class ServerController {
    //questa classe deve inoltre essere in grado di notificare TUTTI i client,
    //indipendentemente dal protocollo, dei cambiamenti avvuti

    /*final Model model;        istanziare il Game oppure una nuova classe model??

    public ServerController(){this.model = new Model();}    // il model del server e' inizializzato qui a una partita vuota e poi e' modificato giocando


    // le chiamate alle funzioni sono sempre dei blocchi con questa forma:
    public void f(x){
        synchronized(model){
            model.f(x);
        }
    }
    ad esempio
    public void chooseOfferTile(){
        synchronized(model){
            model.chooseOfferTile();
        }
    }
     */
}
