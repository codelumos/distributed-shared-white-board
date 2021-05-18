package org.unimelb.whiteboard.client.RMI;

import org.unimelb.whiteboard.client.Client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class RemoteApp extends UnicastRemoteObject implements IRemoteApp {
    private final Client client;

    public RemoteApp(Client client) throws RemoteException {
        this.client = client;
    }

    @Override
    public void askIn(String hostIp, int chatPort) throws RemoteException {
        System.out.println("The host agree.");
        client.joinRoom(hostIp, chatPort);
    }

    @Override
    public void askOut() throws RemoteException {
        System.out.println("Be kicked by the host.");
        client.switch2Lobby();
        client.getLobbyView().createBeKickedDialog();
    }
}
