package com.dcom.services;

import com.dcom.DataRetrievalInterface;
import com.dcom.dataModel.User;
import com.dcom.rmi.LoginService;
import com.dcom.serviceLocator.DbServiceLocator;
import org.mindrot.jbcrypt.BCrypt;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static com.dcom.utils.JWTUtil.generateToken;

public class LoginServiceImpl extends UnicastRemoteObject implements LoginService {

    public LoginServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String login(String email, String inputPassword){
        try {
            DataRetrievalInterface rmiService = DbServiceLocator.getRmiService();
            User user = rmiService.getUserByEmail(email);

            if(checkPassword(inputPassword, user.getPwd())){
                if(user.getStatus().equals("inactive")){
                    return null;
                }
                String token = generateToken(user);
                if (token != null) {
                    return token;
                } else {
                    System.out.println("Token generation failed");
                }
            }
        } catch (Exception e) {
            System.out.println("An error occur while user try to log in.");
        }
        return null;
    }

    private boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
