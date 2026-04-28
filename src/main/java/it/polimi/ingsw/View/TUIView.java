package it.polimi.ingsw.View;

import it.polimi.ingsw.Model.ClientModel;

public class TUIView {
    final private ClientModel localModel;
    final private ClientController clientController;

    public TUIView(ClientModel localModel, ClientController clientController) {
        this.localModel = localModel;
        this.clientController = clientController;
    }
}
