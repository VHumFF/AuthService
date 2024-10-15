package com.dcom.services;

import com.dcom.DataRetrievalInterface;
import com.dcom.dataModel.User;
import com.dcom.rmi.LoginService;
import com.dcom.rmi.UserService;
import com.dcom.serviceLocator.DbServiceLocator;
import com.dcom.utils.JWTUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static com.dcom.utils.JWTUtil.generateToken;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    public UserServiceImpl() throws RemoteException {
        super();
    }


    public boolean updatePassword(String token, String oldPassword, String newPassword){
        int userId = JWTUtil.validateToken(token);
        DataRetrievalInterface rmiService = DbServiceLocator.getRmiService();
        try{
            User user = rmiService.retrieveUser(userId);
            if(checkPassword(oldPassword, user.getPwd())){
                user.setPwd(hashPassword(newPassword));
                return rmiService.updateUser(user);
            }
        }catch (Exception e){
            System.out.println("Error occurred while retrieving user info");
        }

        return false;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    private boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
