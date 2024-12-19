package com.dcom.services;

import com.dcom.DataRetrievalInterface;
import com.dcom.dataModel.User;
import com.dcom.serviceLocator.DbServiceLocator;
import com.dcom.utils.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceImplTest {

    private LoginServiceImpl loginService;

    @Mock
    private User mockUser;




    @BeforeEach
    public void setUp() throws RemoteException {
        MockitoAnnotations.openMocks(this);
        loginService = new LoginServiceImpl();
    }

    @Test
    public void testLogin_Success() throws Exception {
        String inputPassword = "correctPassword";
        String token = "generatedToken";
        mockUser = new User(1, "hashedPassword", "active", "user", "test@example.com");

        try (MockedStatic<DbServiceLocator> mockedServiceLocator = mockStatic(DbServiceLocator.class)) {
            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(DbServiceLocator::getRmiService).thenReturn(mockDbService);

            // Mocking the getUserByEmail method
            when(mockDbService.getUserByEmail("test@example.com")).thenReturn(mockUser);

            // Mocking BCrypt.checkpw to return true (bypassing the actual BCrypt logic)
            try (MockedStatic<BCrypt> mockedBCrypt = mockStatic(BCrypt.class)) {
                mockedBCrypt.when(() -> BCrypt.checkpw(inputPassword, "hashedPassword")).thenReturn(true);

                // Mocking JWTUtil's generateToken method
                try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class)) {
                    mockedJWTUtil.when(() -> JWTUtil.generateToken(mockUser)).thenReturn(token);

                    String result = loginService.login("test@example.com", inputPassword);

                    assertNotNull(result);
                    assertEquals(token, result);

                    verify(mockDbService, times(1)).getUserByEmail("test@example.com");
                    mockedBCrypt.verify(() -> BCrypt.checkpw(inputPassword, "hashedPassword"), times(1)); // Ensure checkPassword is called
                }
            }
        }
    }


    @Test
    public void testLogin_Failure_InvalidPassword() throws Exception {
        String inputPassword = "incorrectPassword";

        try (MockedStatic<DbServiceLocator> mockedServiceLocator = mockStatic(DbServiceLocator.class)) {
            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(DbServiceLocator::getRmiService).thenReturn(mockDbService);

            // Mocking the getUserByEmail method
            when(mockDbService.getUserByEmail("test@example.com")).thenReturn(mockUser);

            // Mocking the checkPassword method
            try (MockedStatic<BCrypt> mockedBCrypt = mockStatic(BCrypt.class)) {
                mockedBCrypt.when(() -> BCrypt.checkpw(inputPassword, mockUser.getPwd())).thenReturn(true);
                String result = loginService.login("test@example.com", inputPassword);

                assertNull(result);  // Since password is incorrect

            }

        }

    }


    @Test
    public void testLogin_Failure_UserNotFound() throws Exception {
        String inputPassword = "correctPassword";

        try (MockedStatic<DbServiceLocator> mockedServiceLocator = mockStatic(DbServiceLocator.class)) {
            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(DbServiceLocator::getRmiService).thenReturn(mockDbService);

            // Mocking the getUserByEmail method to return null (user not found)
            when(mockDbService.getUserByEmail("test@example.com")).thenReturn(null);

            String result = loginService.login("test@example.com", inputPassword);
            assertNull(result);  // No user found
        }

    }
}
