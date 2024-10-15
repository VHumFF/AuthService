package com.dcom;

import com.dcom.dataModel.User;
import com.dcom.rmi.LoginService;
import com.dcom.rmi.UserService;
import com.dcom.services.LoginServiceImpl;
import com.dcom.services.UserServiceImpl;
import com.dcom.utils.JWTUtil;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {

        try {
            LoginService loginService = new LoginServiceImpl();
            UserService userService = new UserServiceImpl();
            Registry registry = LocateRegistry.createRegistry(8080);
            registry.rebind("loginService", loginService);
            registry.rebind("userService", userService);
            System.out.println("Auth Service is running...");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}