package com.recipefind.backend.controller;

import com.recipefind.backend.entity.User;
import com.recipefind.backend.entity.UserDTO;
import com.recipefind.backend.service.UserService;
import com.recipefind.backend.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    private final JwtUtil jwtUtil;

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

                return ResponseEntity.ok(new UserDTO(userLoggedIn.getId().toString(), userLoggedIn.getUsername(), userLoggedIn.getEmail()));
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        // Extract the JWT token from the cookies
        String token = Arrays.stream(request.getCookies())
                .filter(cookie -> "token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String userId = jwtUtil.extractUserId(token);

        // Fetch the user using the ID
        Optional<User> optionalUser = Optional.ofNullable(userService.getUserById(Integer.valueOf(userId)));

        // If the user is not found, throw an exception
        User user = optionalUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(new UserDTO(user.getId().toString(), user.getUsername(), user.getEmail()));
    }
}
