package edu.icet.ecom.controller;

import edu.icet.ecom.model.dto.AuthenticationRequest;
import edu.icet.ecom.model.dto.AuthenticationResponse;
import edu.icet.ecom.model.dto.RegisterRequest;
import edu.icet.ecom.service.AuthenticationService;
import edu.icet.ecom.util.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {

    // Look how clean this is! No Repositories, no PasswordEncoders. Just the Service.
    private final AuthenticationService authenticationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<StandardResponse> register(@Valid @RequestBody RegisterRequest request) {
        // The service handles ALL the hashing, checking, and saving.
        AuthenticationResponse authResponse = authenticationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new StandardResponse(HttpStatus.CREATED.value(), "Staff registered successfully", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        // The service handles ALL the checking and token generation.
        AuthenticationResponse authResponse = authenticationService.authenticate(request);

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Login successful", authResponse)
        );
    }
}