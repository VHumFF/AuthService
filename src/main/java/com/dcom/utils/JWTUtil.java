package com.dcom.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.dcom.dataModel.User;

import java.util.Date;

public class JWTUtil {

    private static final String SECRET_KEY = "kjasdbgojasug132b41!@235Z*(&*!&#%";

    public static String generateToken(User user) {
        try {
            return JWT.create()
                    .withSubject("User Authentication")
                    .withClaim("userId", user.getUserId())
                    .withClaim("userType", user.getUserType())
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000))
                    .sign(Algorithm.HMAC256(SECRET_KEY));
        } catch (Exception e) {
            System.out.println("Error generating JWT: " + e.getMessage());
            return null;
        }
    }

    public static int validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject("User Authentication")
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);


            return decodedJWT.getClaim("userId").asInt();
        } catch (JWTVerificationException exception) {
            System.out.println("Invalid or expired token: " + exception.getMessage());
            return 0;
        }
    }
}

