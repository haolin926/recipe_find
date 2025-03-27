package com.recipefind.backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.controller.UserController;
import com.recipefind.backend.entity.User;
import com.recipefind.backend.entity.UserDTO;
import com.recipefind.backend.service.UserService;
import com.recipefind.backend.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerUser_ShouldReturnSuccess() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        when(userService.createUser(userDTO)).thenReturn(true);

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registered Successfully"));
    }

    @Test
    public void registerUser_ShouldReturnConflict() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        when(userService.createUser(userDTO)).thenThrow(new DataIntegrityViolationException("User already exists"));

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already exists."));
    }

    @Test
    public void loginUser_ShouldReturnUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userService.loginUser(userDTO)).thenReturn(user);
        when(jwtUtil.generateToken(user.getId())).thenReturn("token");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(cookie().value("token", "token"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void loginUser_ShouldReturnUnauthorized() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");

        when(userService.loginUser(userDTO)).thenReturn(null);

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid Username/Password"));
    }

    @Test
    public void logout_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/user/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"))
                .andExpect(cookie().maxAge("token", 0));
    }

    @Test
    public void updateUserInfo_ShouldReturnUpdatedUser() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("updateduser");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(jwtUtil.validateToken("token")).thenReturn(true);
        when(jwtUtil.extractUserId("token")).thenReturn("1");
        when(userService.getUserById(1)).thenReturn(user);
        when(userService.updateUser(user, userDTO)).thenReturn(user);

        mockMvc.perform(put("/api/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .cookie(new Cookie("token", "token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void changeUserPassword_ShouldReturnUpdatedUser() throws Exception {
        Map<String, String> passwordUpdate = Map.of("oldPassword", "oldpass", "newPassword", "newpass");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(jwtUtil.validateToken("token")).thenReturn(true);
        when(jwtUtil.extractUserId("token")).thenReturn("1");
        when(userService.getUserById(1)).thenReturn(user);
        when(userService.updatePassword(user, "oldpass", "newpass")).thenReturn(user);

        mockMvc.perform(put("/api/user/changepassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordUpdate))
                        .cookie(new Cookie("token", "token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void getUserInfo_ShouldReturnUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(jwtUtil.validateToken("token")).thenReturn(true);
        when(jwtUtil.extractUserId("token")).thenReturn("1");
        when(userService.getUserById(1)).thenReturn(user);

        mockMvc.perform(get("/api/user/info")
                        .cookie(new Cookie("token", "token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }
}
