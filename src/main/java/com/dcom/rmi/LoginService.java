package com.dcom.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoginService extends Remote {
    String login(String email, String password) throws RemoteException;
}
