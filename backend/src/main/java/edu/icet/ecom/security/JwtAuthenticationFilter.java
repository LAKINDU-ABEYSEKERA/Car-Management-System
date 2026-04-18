package edu.icet.ecom.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Look for the "Authorization" header in the HTTP Request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. If there is no header, or it doesn't start with "Bearer ", pass it down the chain (it will likely get rejected later)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the token (chop off the first 7 characters: "Bearer ")
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        // 4. If we found an email, and the user isn't already logged in...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Fetch the user from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 5. Ask the JwtService if the token is valid for this user
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 6. If valid, create an official Security Token and put it in the Security Context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // This line officially "logs the user in" for this specific request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}