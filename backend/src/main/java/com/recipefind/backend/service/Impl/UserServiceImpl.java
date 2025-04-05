package com.recipefind.backend.service.Impl;

import com.recipefind.backend.dao.UserRepository;
import com.recipefind.backend.entity.User;
import com.recipefind.backend.entity.UserDTO;
import com.recipefind.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    @Transactional
    public Boolean createUser(UserDTO userDTO) {
        User user = new User();
        user.setUserPhoto(userDTO.getUserPhoto());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());

        try {
            User isUserExists = userRepository.findByUsername(userDTO.getUsername());
            if (isUserExists != null) {
                throw new DataIntegrityViolationException("User already exists");
            }

            User saveduser = userRepository.save(user);
            return saveduser.getId() != null;
        } catch (DataIntegrityViolationException e) {
            logger.error("Database error while saving user: {}", e.getMessage());
            throw new DataIntegrityViolationException("User already exists");
        } catch (Exception e) {
            logger.error("Unexpected error while saving user: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public User loginUser(UserDTO userDTO) {
        User user = userRepository.findByUsername(userDTO.getUsername());
        if (user != null) {
            if (passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                return user;
            }else {
                logger.error("Password mismatch for user {}, password hash: {}, stored password: {}", userDTO.getUsername(), passwordEncoder.encode(userDTO.getPassword()), user.getPassword());
            }
        }
        return null;
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id.longValue()).orElse(null);
    }

    @Override
    public User updateUser(User user, UserDTO userDTO) {
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getUserPhoto() != null) {
            user.setUserPhoto(userDTO.getUserPhoto());
        }
        try {
            return userRepository.save(user);
        } catch (DataAccessException e) {
            logger.error("Database error while updating user: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while updating user: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public User updatePassword(User user, String originalPassword, String newPassword) {
        if (passwordEncoder.matches(originalPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            try {
                return userRepository.save(user);
            } catch (DataAccessException e) {
                logger.error("Database error while updating password: {}", e.getMessage());
            } catch (Exception e) {
                logger.error("Unexpected error while updating password: {}", e.getMessage());
            }
        } else {
            throw new DataIntegrityViolationException("Original password incorrect");
        }
        return null;
    }
}
