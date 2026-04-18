package edu.icet.ecom.service.impl;

import edu.icet.ecom.exception.BusinessException;
import edu.icet.ecom.model.dto.AuthenticationRequest;
import edu.icet.ecom.model.dto.AuthenticationResponse;
import edu.icet.ecom.model.dto.RegisterRequest;
import edu.icet.ecom.model.entity.User;
import edu.icet.ecom.repository.UserRepository;
import edu.icet.ecom.security.JwtService;
import edu.icet.ecom.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional // Protects the database write
    public AuthenticationResponse register(RegisterRequest request) {
        log.info("Attempting to register new staff member: {}", request.getEmail());

        // 1. Check if the email is already in use
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Email is already registered in the system.");
        }

        // 2. Build the User entity
        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        // 3. ENCRYPT THE PASSWORD
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Save to Database
        userRepository.save(user);

        // 5. Generate and return the JWT
        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Attempting login for user: {}", request.getEmail());

        // 1. Let Spring Security verify the raw password against the hashed database password
        // If it fails, Spring throws an exception automatically!
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. If we reach this line, the password was correct. Load the user.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        // 3. Generate a fresh 24-hour token
        String jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
    }
}