package User;

import User.Security.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("api/v1")
@Slf4j
public class UserController {
    @Autowired
    private UserService service;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;


    @PostMapping("/registration")
    public void registerCustomer(@RequestBody UserRequestRegister userRequest) {
        log.info("new registration request --> " + userRequest.email());
        service.register(userRequest);
    }

    @PostMapping("/auth")
    public ResponseEntity<?> loginCustomer(@RequestBody UserLoginRequest request, HttpServletResponse response) throws Exception {
        String token;
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (AuthenticationException e){
            throw new Exception("authentication : invalid information", e);
        }
        log.info("<--- authentication successful --->");

        UserDetails userDetails = service.loadUserByUsername(request.username());

        token = jwtUtils.createToken(userDetails);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie","Token="+ token +"; SameSite=None; Max-Age=604800; Path=/; Secure; HttpOnly");

        //response.addCookie(new Cookie("Token", token));

        return ResponseEntity.status(HttpStatus.OK)
                //.header("Set-Cookie", "helloworld")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Allow-Credentials", "true")
                //.header("Access-Control-Expose-Headers", "set-cookie")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .headers(headers)
                .build();
    }

    @GetMapping("/verifyToken")
    public Boolean verifyToken(@CookieValue(name = "Token") String token) {
        System.out.println(token.toString());
        return jwtUtils.verifyToken(token);
    }
}
