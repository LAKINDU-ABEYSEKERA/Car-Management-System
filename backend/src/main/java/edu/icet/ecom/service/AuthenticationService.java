package edu.icet.ecom.service;

import edu.icet.ecom.model.dto.AuthenticationRequest;
import edu.icet.ecom.model.dto.AuthenticationResponse;
import edu.icet.ecom.model.dto.RegisterRequest;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}