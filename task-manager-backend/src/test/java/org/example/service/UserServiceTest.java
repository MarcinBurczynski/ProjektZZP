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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    @Test
    void createUser_nonAdminUser_throwsSecurityException() {
        User nonAdmin = new User("user", "", "user@example.com", Role.USER);
        nonAdmin.setId(2L);

        UserOperationDTO dto = new UserOperationDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setEmail("newuser@example.com");
        dto.setRole(Role.USER);

        assertThrows(SecurityException.class, () -> userService.createUser(nonAdmin, dto));
    }

    @Test
    void createUser_existingUsername_throwsIllegalArgumentException() {
        User admin = new User("admin", "", "admin@example.com", Role.ADMIN);
        admin.setId(1L);

        User existingUser = new User("existing", "", "existing@example.com", Role.USER);
        existingUser.setId(3L);

        UserOperationDTO dto = new UserOperationDTO();
        dto.setUsername("existing");
        dto.setPassword("password123");
        dto.setEmail("duplicate@example.com");
        dto.setRole(Role.USER);

        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(admin, dto));
    }

    @Test
    void register_newUser_returnsPermissionDTO() {
        User newUser = new User("user", "password", "user@example.com", Role.USER);
        newUser.setId(1L);

        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserOperationDTO dto = new UserOperationDTO(newUser);
        PermissionDTO result = userService.register(dto);

        assertEquals("USER", result.getRole());
        assertNotNull(result.getJwt());
        assertFalse(result.getJwt().isEmpty());
    }

    @Test
    void register_existingUsername_throwsRuntimeException() {
        User existing = new User("user", "pass", "user@example.com", Role.USER);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(existing));

        UserOperationDTO dto = new UserOperationDTO(existing);

        assertThrows(RuntimeException.class, () -> {
            userService.register(dto);
        });
    }

    @Test
    void login_validCredentials_returnsPermissionDTO() {
        String rawPassword = "password";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User existingUser = new User("user", encodedPassword, "user@example.com", Role.USER);
        existingUser.setId(1L);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(existingUser));
        when(jwtUtils.generateToken(existingUser)).thenReturn("mocked.jwt.token");

        UserOperationDTO dto = new UserOperationDTO();
        dto.setUsername("user");
        dto.setPassword(rawPassword);

        PermissionDTO result = userService.login(dto);

        assertEquals("USER", result.getRole());
        assertNotNull(result.getJwt());
        assertFalse(result.getJwt().isEmpty());
    }

    @Test
    void login_invalidPassword_throwsRuntimeException() {
        String rawPassword = "wrongPassword";
        String encodedPassword = new BCryptPasswordEncoder().encode("correctPassword");

        User user = new User("user", encodedPassword, "user@example.com", Role.USER);
        user.setId(1L);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        UserOperationDTO dto = new UserOperationDTO();
        dto.setUsername("user");
        dto.setPassword(rawPassword);

        assertThrows(RuntimeException.class, () -> {
            userService.login(dto);
        });
    }

    @Test
    void login_userNotFound_throwsException() {
        String rawPassword = "password";
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UserOperationDTO dto = new UserOperationDTO();
        dto.setUsername("user");
        dto.setPassword(rawPassword);

        assertThrows(RuntimeException.class, () -> {
            userService.login(dto);
        });
    }


    @Test
    void updateUser_validUpdate_updatesUser() {
        User admin = new User("admin", "", "admin@example.com", Role.ADMIN);
        admin.setId(1L);

        User data = new User("updatedUser", "newpass", "updated@example.com", Role.USER);
        data.setId(2L);
        UserOperationDTO dto = new UserOperationDTO(data);

        User userFromDb = new User("user", passwordEncoder.encode("oldpass"), "user@example.com", Role.USER);
        userFromDb.setId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(userFromDb));
        when(userRepository.findByUsername("updatedUser")).thenReturn(Optional.empty()); // Ensure no conflict here
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.updateUser(admin, dto);

        verify(userRepository).save(argThat(updatedUser ->
                updatedUser.getUsername().equals("updatedUser") &&
                        updatedUser.getEmail().equals("updated@example.com") &&
                        passwordEncoder.matches("newpass", updatedUser.getPassword()) &&
                        updatedUser.getRole() == Role.USER
        ));
    }

    @Test
    void updateUser_userTriesToUpdateAnotherUser_throwsSecurityException() {
        User user = new User("user", "", "user@example.com", Role.USER);
        user.setId(1L);

        UserOperationDTO dto = new UserOperationDTO();
        dto.setId(2L);
        dto.setUsername("newname");
        dto.setEmail("new@example.com");
        dto.setPassword("pass");
        dto.setRole(Role.USER);

        assertThrows(SecurityException.class, () -> userService.updateUser(user, dto));
    }

    @Test
    void updateUser_userTriesToChangeOwnRole_throwsSecurityException() {
        User user = new User("user", "", "user@example.com", Role.USER);
        user.setId(2L);

        UserOperationDTO dto = new UserOperationDTO();
        dto.setId(2L);
        dto.setUsername("user");
        dto.setEmail("user@example.com");
        dto.setPassword("pass");
        dto.setRole(Role.ADMIN); // próba podniesienia roli

        assertThrows(SecurityException.class, () -> userService.updateUser(user, dto));
    }

    @Test
    void updateUser_usernameAlreadyTaken_throwsIllegalArgumentException() {
        User admin = new User("admin", "", "admin@example.com", Role.ADMIN);
        admin.setId(1L);

        User existing = new User("taken", "", "taken@example.com", Role.USER);
        existing.setId(3L);

        UserOperationDTO dto = new UserOperationDTO();
        dto.setId(2L);
        dto.setUsername("taken"); // próba nadpisania czyjegoś username
        dto.setEmail("new@example.com");
        dto.setPassword("pass");
        dto.setRole(Role.USER);

        when(userRepository.findByUsername("taken")).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(admin, dto));
    }

    @Test
    void updateUser_userNotFound_throwsRuntimeException() {
        User admin = new User("admin", "", "admin@example.com", Role.ADMIN);
        admin.setId(1L);

        UserOperationDTO dto = new UserOperationDTO();
        dto.setId(2L);
        dto.setUsername("newname");
        dto.setEmail("new@example.com");
        dto.setPassword("pass");
        dto.setRole(Role.USER);

        when(userRepository.findByUsername("newname")).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(admin, dto));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void deleteUser_asAdmin_deletesAnotherUser() {
        User admin = new User("admin", "", "admin@example.com", Role.ADMIN);
        admin.setId(1L);

        Long idToDelete = 2L;

        userService.deleteUser(admin, idToDelete);

        verify(userRepository).deleteById(idToDelete);
    }

    @Test
    void deleteUser_userTriesToDeleteSelf_throwsSecurityException() {
        User user = new User("user", "", "user@example.com", Role.USER);
        user.setId(2L);

        assertThrows(SecurityException.class, () -> {
            userService.deleteUser(user, 2L);
        });

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deleteUser_moderatorTriesToDeleteUser_throwsSecurityException() {
        User moderator = new User("mod", "", "mod@example.com", Role.MODERATOR);
        moderator.setId(1L);

        assertThrows(SecurityException.class, () -> {
            userService.deleteUser(moderator, 3L);
        });

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getUserById_asAdmin_returnsUserDTO() {
        User admin = new User("admin", "", "admin@example.com", Role.ADMIN);
        admin.setId(1L);

        User user = new User("user", "", "user@example.com", Role.USER);
        user.setId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(admin, 2L);

        assertEquals("user", result.getUsername());
        assertEquals("user@example.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void getUserById_userTriesToAccessOtherUser_throwsSecurityException() {
        User user = new User("user", "", "user@example.com", Role.USER);
        user.setId(2L);

        assertThrows(SecurityException.class, () -> {
            userService.getUserById(user, 3L);
        });
    }

    @Test
    void getUserObjectById_userGetsOwnData_returnsUser() {
        User user = new User("user", "", "user@example.com", Role.USER);
        user.setId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        User result = userService.getUserObjectById(user, 2L);

        assertEquals("user", result.getUsername());
    }

    @Test
    void getUserObjectById_adminGetsOtherUser_returnsUser() {
        User admin = new User("admin", "", "admin@example.com", Role.ADMIN);
        admin.setId(1L);

        User otherUser = new User("other", "", "other@example.com", Role.USER);
        otherUser.setId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));

        User result = userService.getUserObjectById(admin, 2L);

        assertEquals("other", result.getUsername());
    }

    @Test
    void getUserObjectById_userTriesToGetOtherUser_throwsSecurityException() {
        User user = new User("user", "", "user@example.com", Role.USER);
        user.setId(2L);

        assertThrows(SecurityException.class, () -> {
            userService.getUserObjectById(user, 3L);
        });
    }

    @Test
    void getUserObjectById_userNotFound_throwsIllegalArgumentException() {
        User admin = new User("admin", "", "admin@example.com", Role.ADMIN);
        admin.setId(1L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserObjectById(admin, 99L);
        });
    }

    @Test
    void getUsers_adminCanViewUsers_returnsListOfUserDTOs() {
        User admin = new User("admin", "", "admin@example.com", Role.ADMIN);
        admin.setId(1L);

        List<User> users = List.of(
                new User("user1", "", "u1@example.com", Role.USER),
                new User("user2", "", "u2@example.com", Role.MODERATOR)
        );

        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> result = userService.getUsers(admin);

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
    }

    @Test
    void getUsers_userCannotViewUsers_throwsSecurityException() {
        User user = new User("user", "", "user@example.com", Role.USER);
        user.setId(2L);

        assertThrows(SecurityException.class, () -> {
            userService.getUsers(user);
        });
    }

}
