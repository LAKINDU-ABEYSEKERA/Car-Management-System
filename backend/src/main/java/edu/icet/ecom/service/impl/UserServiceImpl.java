package edu.icet.ecom.service.impl;

import edu.icet.ecom.exception.BusinessException;
import edu.icet.ecom.model.dto.UserDTO;
import edu.icet.ecom.model.entity.User;
import edu.icet.ecom.repository.UserRepository;
import edu.icet.ecom.service.UserService;
import edu.icet.ecom.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        log.info("Attempting to update user ID: {}", userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found with ID: " + userId));

        // 1. Update the safe fields
        existingUser.setUserName(userDTO.getUserName());
        existingUser.setRole(userDTO.getRole());

        // 2. SECURE EMAIL UPDATE LOGIC
        // Only update if the email is actually changing
        if (!existingUser.getEmail().equalsIgnoreCase(userDTO.getEmail())) {

            // Check if the new email is already taken by someone else
            if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new BusinessException("Update Failed: That email is already registered to another user!");
            }

            // If it's free, apply the new email
            existingUser.setEmail(userDTO.getEmail());
            log.info("User ID {} email updated to {}", userId, userDTO.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return mapToDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Attempting to delete user ID: {}", userId);

        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found with ID: " + userId));

        // ENTERPRISE SECURITY: Prevent self-deletion!
        String currentLoggedInEmail = SecurityUtil.getCurrentUserEmail();
        if (userToDelete.getEmail().equalsIgnoreCase(currentLoggedInEmail)) {
            throw new BusinessException("CRITICAL: You cannot delete your own Admin account!");
        }

        userRepository.delete(userToDelete);
        log.info("User {} deleted successfully.", userToDelete.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all personnel records from the database");
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    // --- Helper Method ---
    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId().toString());
        dto.setUserName(user.getuserName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}