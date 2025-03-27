package com.recipefind.backend.controller;

import com.recipefind.backend.entity.User;
import com.recipefind.backend.entity.UserDTO;
import com.recipefind.backend.service.UserService;
import com.recipefind.backend.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    private final JwtUtil jwtUtil;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            Boolean userCreated = userService.createUser(userDTO);
            if (userCreated) {
                return ResponseEntity.ok("Registered Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registration failed.");
            }
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            User userLoggedIn = userService.loginUser(userDTO);
            if (userLoggedIn != null && userLoggedIn.getId() != null) {
                String token = jwtUtil.generateToken(userLoggedIn.getId());

                Cookie cookie = new Cookie("token", token);
                cookie.setHttpOnly(true);
                cookie.setMaxAge(60 * 60); // 1 hour
                cookie.setPath("/");

                response.addCookie(cookie);

                return ResponseEntity.ok(userLoggedIn.convertToDTO());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username/Password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie("token", null);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            cookie.setPath("/");

            response.addCookie(cookie);
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserInfo(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        User user = validateTokenAndGetUser(request);

        try {
            User userUpdated = userService.updateUser(user, userDTO);
            if (userUpdated != null) {
                return ResponseEntity.ok(userUpdated.convertToDTO());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User update failed.");
            }
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PutMapping("/changepassword")
    public ResponseEntity<?> changeUserPassword(@RequestBody Map<String, String> passwordUpdate, HttpServletRequest request) {
        System.out.println("Received password update request: " + passwordUpdate);
        User user = validateTokenAndGetUser(request);

        String originalPassword = passwordUpdate.get("oldPassword");
        String newPassword = passwordUpdate.get("newPassword");

        try {
            User userUpdated = userService.updatePassword(user, originalPassword, newPassword);
            if (userUpdated != null) {
                return ResponseEntity.ok(userUpdated.convertToDTO());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Password change failed.");
            }
        } catch (DataIntegrityViolationException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Original Password Incorrect");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        User user = validateTokenAndGetUser(request);

        return ResponseEntity.ok(user.convertToDTO());
    }

    private User validateTokenAndGetUser(HttpServletRequest request) {
        // Extract the JWT token from the cookies
        String token = Arrays.stream(request.getCookies())
                .filter(cookie -> "token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (token == null || !jwtUtil.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String userId = jwtUtil.extractUserId(token);

        // Fetch the user using the ID
        Optional<User> optionalUser = Optional.ofNullable(userService.getUserById(Integer.valueOf(userId)));

        // If the user is not found, throw an exception
        return optionalUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
