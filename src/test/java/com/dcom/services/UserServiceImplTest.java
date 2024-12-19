package com.dcom.services;

import com.dcom.DataRetrievalInterface;
import com.dcom.dataModel.User;
import com.dcom.serviceLocator.DbServiceLocator;
import com.dcom.utils.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.rmi.RemoteException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceImplTest {

    private UserServiceImpl userService;
    private User mockUser;

    @BeforeEach
    public void setUp() throws RemoteException {
        userService = new UserServiceImpl();
        mockUser = new User(1, "hashedPassword", "active", "user", "test@example.com");
    }

    @Test
    public void testUpdatePassword_Success() throws Exception {
        String token = "validToken";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<BCrypt> mockedBCrypt = mockStatic(BCrypt.class);
             MockedStatic<DbServiceLocator> mockedServiceLocator = mockStatic(DbServiceLocator.class)) {

            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(DbServiceLocator::getRmiService).thenReturn(mockDbService);

            // Mock JWT validation to return a valid userId
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(1);

            // Mock DataRetrievalInterface to return the user
            when(mockDbService.retrieveUser(1)).thenReturn(mockUser);

            // Mock BCrypt to simulate password check and hash
            mockedBCrypt.when(() -> BCrypt.checkpw(oldPassword, mockUser.getPwd())).thenReturn(true);  // Old password matches the hash
            mockedBCrypt.when(() -> BCrypt.hashpw(newPassword, BCrypt.gensalt(10))).thenReturn("hashedNewPassword"); // New password hashed

            // Mock the update user method
            when(mockDbService.updateUser(mockUser)).thenReturn(true);

            // Call updatePassword method
            boolean result = userService.updatePassword(token, oldPassword, newPassword);

            assertTrue(result);
            verify(mockDbService, times(1)).retrieveUser(1);
            verify(mockDbService, times(1)).updateUser(mockUser);
        }
    }



    @Test
    public void testUpdatePassword_IncorrectOldPassword() throws Exception {
        String token = "validToken";
        String oldPassword = "wrongOldPassword";
        String newPassword = "newPassword";

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<BCrypt> mockedBCrypt = mockStatic(BCrypt.class);
             MockedStatic<DbServiceLocator> mockedServiceLocator = mockStatic(DbServiceLocator.class)) {

            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(DbServiceLocator::getRmiService).thenReturn(mockDbService);

            // Mock JWT validation to return a valid userId
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(1);

            // Mock DataRetrievalInterface to return the user
            when(mockDbService.retrieveUser(1)).thenReturn(mockUser);

            // Mock BCrypt to simulate password check and hash
            mockedBCrypt.when(() -> BCrypt.checkpw(oldPassword, mockUser.getPwd())).thenReturn(false);

            // Call updatePassword method
            boolean result = userService.updatePassword(token, oldPassword, newPassword);

            assertFalse(result);
            verify(mockDbService, times(1)).retrieveUser(1);
            verify(mockDbService, times(0)).updateUser(mockUser);
        }
    }

    @Test
    public void testUpdatePassword_ExceptionHandling() throws Exception {
        String token = "validToken";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<BCrypt> mockedBCrypt = mockStatic(BCrypt.class);
             MockedStatic<DbServiceLocator> mockedServiceLocator = mockStatic(DbServiceLocator.class)) {

            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(DbServiceLocator::getRmiService).thenReturn(mockDbService);

            // Mock JWT validation to return a valid userId
            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(1);

            // Mock DataRetrievalInterface to throw an exception when retrieving the user
            when(mockDbService.retrieveUser(1)).thenThrow(new RuntimeException("DB Error"));

            // Call updatePassword method
            boolean result = userService.updatePassword(token, oldPassword, newPassword);

            assertFalse(result);
            verify(mockDbService, times(1)).retrieveUser(1);
            verify(mockDbService, times(0)).updateUser(mockUser);
        }
    }
}
