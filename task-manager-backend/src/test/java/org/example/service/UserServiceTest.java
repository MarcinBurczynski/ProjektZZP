package org.example.service;

import org.example.config.JwtUtils;
import org.example.dto.PermissionDTO;
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

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ValidCredentials_ReturnsPermissionDTO() {
        // given
        String rawPassword = "password123";
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);

        User user = new User("testuser", encodedPassword, "test@test.com", Role.ADMIN);
        user.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserOperationDTO dto = new UserOperationDTO();
        dto.setUsername("testuser");
        dto.setPassword(rawPassword);

        // when
        PermissionDTO result = userService.login(dto);

        // then
        assertEquals("ADMIN", result.getRole());
        assertNotNull(result.getJwt());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        User user = new User("user", passwordEncoder.encode("correct"), "test@test.com", Role.ADMIN);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        UserOperationDTO dto = new UserOperationDTO();
        dto.setUsername("user");
        dto.setPassword("wrong");

        assertThrows(RuntimeException.class, () -> userService.login(dto));
    }

    @Test
    void register_NewUser_Success() {
        // given
        UserOperationDTO dto = new UserOperationDTO();
        dto.setUsername("newuser");
        dto.setPassword("pass");
        dto.setEmail("email@test.com");
        dto.setRole(Role.USER);

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        // when
        PermissionDTO result = userService.register(dto);

        // then
        verify(userRepository).save(captor.capture());
        verify(emailService).sendSimpleMessage(eq("email@test.com"), anyString(), anyString());

        assertEquals("newuser", captor.getValue().getUsername());
        assertNotNull(result.getJwt());
    }

    @Test
    void register_ExistingUser_ThrowsException() {
        User existing = new User("user", "pass", "mail", Role.USER);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(existing));

        UserOperationDTO dto = new UserOperationDTO();
        dto.setUsername("user");
        dto.setPassword("newpass");

        assertThrows(RuntimeException.class, () -> userService.register(dto));
    }

    // Możesz dodać kolejne testy do createUser, updateUser, getUserById itd.
}
