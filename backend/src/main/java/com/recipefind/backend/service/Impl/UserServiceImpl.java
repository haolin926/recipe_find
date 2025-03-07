package com.recipefind.backend.service.Impl;

import com.recipefind.backend.dao.UserRepository;
import com.recipefind.backend.entity.User;
import com.recipefind.backend.entity.UserDTO;
import com.recipefind.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Override
    @Transactional
    public Boolean createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());

        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        System.out.println(user.getEmail());
        try {
            User saveduser = userRepository.save(user);
            System.out.println(saveduser.getId());
            return saveduser.getId() != null;
        } catch (DataAccessException e) {
            logger.error("Database error while saving user: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while saving user: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public User loginUser(UserDTO userDTO) {
        User user = userRepository.findByUsername(userDTO.getUsername());
        if (user != null) {
            if (user.getPassword().equals(userDTO.getPassword())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id.longValue()).orElse(null);
    }
}
