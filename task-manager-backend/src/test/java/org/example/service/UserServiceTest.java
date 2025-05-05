package org.example.service;

import org.example.config.JwtUtils;
import org.example.dto.PermissionDTO;
import org.example.dto.UserDTO;
import org.example.dto.UserOperationDTO;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void createUser_adminCreatesUser_returnsUserDTO() {
        User admin = new User("admin", "", "admin@example.com", Role.ADMIN);
        admin.setId(1L);

        User newUser = new User("user", "password", "user@example.com", Role.USER);
        newUser.setId(2L);

        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(newUser);


        UserOperationDTO dto = new UserOperationDTO(newUser);
        UserDTO result = userService.createUser(admin, dto);

        assertEquals(newUser.getUsername(), result.getUsername());
        assertEquals(newUser.getEmail(), result.getEmail());
        assertEquals(newUser.getRole(), result.getRole());
    }

}
