package User.Security.jwt;


import User.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    final String SECRET = "d3D*df!/+Dd3D*df!/+Dd3D*df!/+Dd3D*df!/+Dd3D*df!/+Dd3D*df!/+Dd3D*df!/+Dd3D*df!/+D";
    String encodedSecret = new String(Base64.getUrlEncoder().encode(SECRET.getBytes()), StandardCharsets.UTF_8);
    public String createToken(UserDetails userDetails) throws Exception {
        final long EXPIRATION_TIME = 900_000; // 15 mins
        Algorithm algorithm = Algorithm.HMAC256(encodedSecret);
        try {
            return JWT.create()
                    .withIssuer(userDetails.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new Exception("invalide sign in configuration");
        }
    }

    public boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(encodedSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("mathieu")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception){
            System.out.println(exception);
        }

        return false;
    }

}
