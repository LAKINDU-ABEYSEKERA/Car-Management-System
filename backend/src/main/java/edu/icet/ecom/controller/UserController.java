package edu.icet.ecom.controller;

import edu.icet.ecom.model.dto.UserDTO;
import edu.icet.ecom.service.UserService;
import edu.icet.ecom.util.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<StandardResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserDTO userDTO) {

        log.info("Admin requesting update for User ID: {}", userId);
        UserDTO updatedUser = userService.updateUser(userId, userDTO);

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "User updated successfully", updatedUser)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<StandardResponse> deleteUser(@PathVariable Long userId) {

        log.info("Admin requesting deletion for User ID: {}", userId);
        userService.deleteUser(userId);

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "User deleted successfully", null)
        );
    }
}