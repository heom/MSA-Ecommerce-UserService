package me.study.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import me.study.userservice.dto.UserDto;
import me.study.userservice.exception.common.ExceptionResponse;
import me.study.userservice.service.UserService;
import me.study.userservice.vo.RequestLogin;
import me.study.userservice.vo.ResponseUser;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper
                                , UserService userService, Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request
                                                , HttpServletResponse response) throws AuthenticationException {
        try {
            RequestLogin requestLogin = objectMapper.readValue(request.getInputStream(), RequestLogin.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestLogin.getEmail()
                            , requestLogin.getPwd()
                            , new ArrayList<>()
                    )
            );
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response
                                                , FilterChain chain, Authentication authResult)
                                                throws IOException, ServletException {
        String email = ((User) authResult.getPrincipal()).getUsername();
        UserDto userDto = userService.getUserDetailsByEmail(email);

        String token = Jwts.builder()
                .setSubject(userDto.getUserId())
                .setExpiration(new Date(System.currentTimeMillis()
                                        + Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        response.addHeader("token", token);
        response.setContentType("application/json;charset=UTF-8");

        response.getOutputStream()
                .println(objectMapper.writeValueAsString(new ResponseUser(userDto)));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response
                                            , AuthenticationException failed) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                                                                .timestamp(new Date())
                                                                .status(HttpStatus.UNAUTHORIZED.value())
                                                                .error("Unauthorized")
                                                                .message("Unauthorized")
                                                                .path(request.getRequestURI())
                                                                .build();
        response.getOutputStream()
                .println(objectMapper.writeValueAsString(exceptionResponse));
    }
}
