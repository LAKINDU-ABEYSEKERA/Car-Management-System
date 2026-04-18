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

        // Note: We usually do NOT update the email/username here as it breaks login history,
        // but we can safely update name and role!
        existingUser.setUserName(userDTO.getUserName());
        existingUser.setRole(userDTO.getRole());

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

    // --- Helper Method ---
    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId().toString());
        dto.setUserName(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}