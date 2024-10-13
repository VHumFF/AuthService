package com.dcom.serviceLocator;

import com.dcom.DataRetrievalInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DbServiceLocator {
    private static DataRetrievalInterface rmiService;

    private DbServiceLocator() {}

    public static DataRetrievalInterface getRmiService() {
        if (rmiService == null) {
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                rmiService = (DataRetrievalInterface) registry.lookup("Server");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rmiService;
    }
}
