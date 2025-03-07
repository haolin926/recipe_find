package com.recipefind.backend.service;

import com.recipefind.backend.entity.User;
import com.recipefind.backend.entity.UserDTO;

public interface UserService {
    Boolean createUser(UserDTO userDTO);
    User loginUser(UserDTO userDTO);

    User getUserById(Integer id);
}
