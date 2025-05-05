package org.example.service;

import org.example.dto.PermissionDTO;
import org.example.dto.UserOperationDTO;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest2 {

    private UserRepository userRepository;
    private EmailService emailService;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        emailService = mock(EmailService.class);
        userService = new UserService(userRepository, emailService);
    }

    @Test
    public void testRegisterSuccess() {
        // given
        UserOperationDTO dto = new UserOperationDTO();
        dto.setUsername("nowy");
        dto.setPassword("tajnehaslo");
        dto.setEmail("nowy@example.com");
        dto.setRole(Role.USER);

        // Mocks
        when(userRepository.findByUsername("nowy")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(2L); // Nadajemy sztuczne ID użytkownikowi
            return u;
        });

        // when
        PermissionDTO result = userService.register(dto);

        // then
        verify(userRepository).save(any(User.class)); // Sprawdzamy, czy zapisaliśmy użytkownika
        assertNotNull(result.getJwt()); // Token JWT nie powinien być null
        assertEquals("nowy", result.getUserId()); // Sprawdzamy, czy ID użytkownika jest poprawne
        assertEquals(Role.USER.toString(), result.getRole()); // Sprawdzamy, czy rola jest poprawna
        assertEquals("nowy@example.com", result.getJwt()); // Sprawdzamy, czy e-mail jest poprawny (sprawdzamy JWT z e-mailem w teście, ale normalnie powinien być token)
    }
}
