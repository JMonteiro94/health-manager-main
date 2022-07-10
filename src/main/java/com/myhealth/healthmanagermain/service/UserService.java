package com.myhealth.healthmanagermain.service;

import com.myhealth.healthmanagermain.domain.User;
import com.myhealth.healthmanagermain.service.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl);

    Optional<UserDTO> updateUser(UserDTO userDTO);

    void deleteUser(String login);

    Page<UserDTO> getAllManagedUsers(Pageable pageable);

    Optional<User> getUserWithAuthoritiesByLogin(String login);

    Optional<User> getUserWithAuthorities(Long id);

    Optional<User> getUserWithAuthorities();

    List<String> getAuthorities();

    Optional<User> getUserByEmail(String email);

    User createUser(UserDTO userDTO);
}
