package com.taskmanagement.service;

import com.taskmanagement.dto.AuthRequest;
import com.taskmanagement.dto.AuthResponse;
import com.taskmanagement.dto.UserDTO;
import com.taskmanagement.entity.Role;
import com.taskmanagement.entity.RoleName;
import com.taskmanagement.entity.User;
import com.taskmanagement.mapper.EntityMapper;
import com.taskmanagement.repository.RoleRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;
    private User user;
    private Role userRole;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");
        authRequest.setEmail("test@example.com");
        authRequest.setFullName("Test User");

        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleName.ROLE_USER);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setFullName("Test User");
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setFullName("Test User");
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(entityMapper.toUserDTO(any(User.class))).thenReturn(userDTO);

        // Act
        UserDTO result = authService.registerUser(authRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerUser(authRequest);
        });
        assertEquals("Username is already taken!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerUser(authRequest);
        });
        assertEquals("Email is already in use!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticateUser_Success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(any(Authentication.class))).thenReturn("test-jwt-token");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // Act
        AuthResponse result = authService.authenticateUser(authRequest);

        // Assert
        assertNotNull(result);
        assertEquals("test-jwt-token", result.getToken());
        assertEquals("testuser", result.getUsername());
        assertEquals(1L, result.getUserId());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void testAuthenticateUser_UserNotFound() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(any(Authentication.class))).thenReturn("test-jwt-token");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(authRequest);
        });
    }

    @Test
    void testValidateToken_Success() {
        // Arrange
        when(jwtUtil.validateToken(anyString())).thenReturn(true);

        // Act
        boolean result = authService.validateToken("valid-token");

        // Assert
        assertTrue(result);
        verify(jwtUtil, times(1)).validateToken("valid-token");
    }

    @Test
    void testValidateToken_Invalid() {
        // Arrange
        when(jwtUtil.validateToken(anyString())).thenReturn(false);

        // Act
        boolean result = authService.validateToken("invalid-token");

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetUsernameFromToken() {
        // Arrange
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("testuser");

        // Act
        String result = authService.getUsernameFromToken("test-token");

        // Assert
        assertEquals("testuser", result);
        verify(jwtUtil, times(1)).getUsernameFromToken("test-token");
    }
}
