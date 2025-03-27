package com.recipefind.backend.service;

import com.recipefind.backend.dao.UserRepository;
import com.recipefind.backend.entity.User;
import com.recipefind.backend.entity.UserDTO;
import com.recipefind.backend.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        userDTO.setEmail("testuser@example.com");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setEmail("testuser@example.com");

        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        Boolean result = userService.createUser(userDTO);

        // Assert
        assertTrue(result);
        verify(passwordEncoder, times(1)).encode(userDTO.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_DatabaseError() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        userDTO.setEmail("testuser@example.com");

        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new DataAccessException("...") {});

        // Act
        Boolean result = userService.createUser(userDTO);

        // Assert
        assertFalse(result);
        verify(passwordEncoder, times(1)).encode(userDTO.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_UnexpectedError() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        userDTO.setEmail("testuser@example.com");

        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        Boolean result = userService.createUser(userDTO);

        // Assert
        assertFalse(result);
        verify(passwordEncoder, times(1)).encode(userDTO.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoginUser_Success() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(userDTO.getPassword(), user.getPassword())).thenReturn(true);

        // Act
        User result = userService.loginUser(userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername(userDTO.getUsername());
        verify(passwordEncoder, times(1)).matches(userDTO.getPassword(), user.getPassword());
    }

    @Test
    void testLoginUser_PasswordMismatch() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(userDTO.getPassword(), user.getPassword())).thenReturn(false);

        // Act
        User result = userService.loginUser(userDTO);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findByUsername(userDTO.getUsername());
        verify(passwordEncoder, times(1)).matches(userDTO.getPassword(), user.getPassword());
    }

    @Test
    void testLoginUser_UserNotFound() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(null);

        // Act
        User result = userService.loginUser(userDTO);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findByUsername(userDTO.getUsername());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testGetUserById_UserFound() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        User result = userService.getUserById(1);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("oldUsername");
        user.setEmail("oldEmail@example.com");

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newUsername");
        userDTO.setEmail("newEmail@example.com");

        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.updateUser(user, userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("newUsername", result.getUsername());
        assertEquals("newEmail@example.com", result.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser_DatabaseError() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("oldUsername");
        user.setEmail("oldEmail@example.com");

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newUsername");
        userDTO.setEmail("newEmail@example.com");

        when(userRepository.save(user)).thenThrow(new DataAccessException("...") {});

        // Act
        User result = userService.updateUser(user, userDTO);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void testUpdatePassword_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPassword");

        String originalPassword = "oldPassword";
        String newPassword = "newPassword";

        when(passwordEncoder.matches(originalPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.updatePassword(user, originalPassword, newPassword);

        // Assert
        assertNotNull(result);
        assertEquals("encodedNewPassword", result.getPassword());
        verify(passwordEncoder, times(1)).matches(originalPassword, "encodedOldPassword");
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdatePassword_OriginalPasswordIncorrect() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPassword");

        String originalPassword = "wrongPassword";
        String newPassword = "newPassword";

        when(passwordEncoder.matches(originalPassword, user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> userService.updatePassword(user, originalPassword, newPassword));
        verify(passwordEncoder, times(1)).matches(originalPassword, user.getPassword());
        verify(passwordEncoder, never()).encode(newPassword);
        verify(userRepository, never()).save(user);
    }

    @Test
    void testUpdatePassword_DatabaseError() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedOldPassword");

        String originalPassword = "oldPassword";
        String newPassword = "newPassword";

        when(passwordEncoder.matches(originalPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenThrow(new DataAccessException("...") {});

        // Act
        User result = userService.updatePassword(user, originalPassword, newPassword);

        // Assert
        assertNull(result);
        verify(passwordEncoder, times(1)).matches(originalPassword, "encodedOldPassword");
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(user);
    }
}
