package com.dcom;


import com.dcom.dataModel.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataRetrievalInterface extends Remote {
    User getUserByEmail(String email) throws RemoteException;
    User retrieveUser(int userId) throws RemoteException;
    boolean updateUser(User user) throws RemoteException;
}
