package com.dcom.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserService extends Remote {
    boolean updatePassword(String token, String oldPassword, String newPassword) throws RemoteException;
}
