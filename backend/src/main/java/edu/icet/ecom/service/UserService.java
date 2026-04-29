package edu.icet.ecom.service;

import edu.icet.ecom.model.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO updateUser(Long userId, UserDTO userDTO);
    void deleteUser(Long userId);
    List<UserDTO> getAllUsers();
}