package com.microblog.api.blog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microblog.api.blog.dto.request.AuthenticationRequest;
import com.microblog.api.blog.dto.request.RegisterRequest;
import com.microblog.api.blog.dto.response.AuthenticationResponse;
import com.microblog.api.blog.model.User;
import com.microblog.api.blog.repository.TokenRepository;
import com.microblog.api.blog.repository.UserRepository;
import com.microblog.api.blog.security.Role;
import com.microblog.api.blog.security.token.Token;
import com.microblog.api.blog.security.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    // Injecting dependencies via constructor using lombok's @RequiredArgsConstructor
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Retrieve the currently authenticated user based on the security context
    public User getCurrentAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return repository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Register a new user and generate authentication and refresh tokens
    public AuthenticationResponse register(RegisterRequest request) {
        // Build user object from the registration request
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        // Save the user to the database
        var savedUser = repository.save(user);

        // Generate authentication and refresh tokens
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        //  Save the refresh token to the database
        saveUserToken(savedUser, jwtToken);

        // Return the authentication response
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Authenticate a user and generate authentication and refresh tokens
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Use Spring's AuthenticationManager to authenticate the user credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Retrieve the user from the database
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        // Generate new JWT (access token) and refresh token for the user
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Revoke all the user's previous refresh tokens
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        // Return the authentication response
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Save the user token to the database
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    // Revoke all valid user tokens (set them as expired and revoked)
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;

        // Iterate over valid user tokens and revoke them
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        // Save the revoked tokens to the database
        tokenRepository.saveAll(validUserTokens);
    }

    // Refresh the access token using the provided refresh token
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        // Get the refresh token from the Authorization header
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        // If the user email is valid, generate a new access token and save it
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);

                // Build the authentication response with the new access token
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

                // Write the authentication response as JSON to the response output stream
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}