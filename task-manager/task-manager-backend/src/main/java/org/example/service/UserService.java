package org.example.service;

import org.example.config.JwtUtils;
import org.example.dto.UserDTO;
import org.example.dto.UserOperationDTO;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String login(UserOperationDTO userOperationDTO) {

        Optional<User> userOptional = userRepository.findByUsername(userOperationDTO.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(userOperationDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        JwtUtils jwtUtils = new JwtUtils();
        String token = jwtUtils.generateToken(user.getUsername());

        return token;
    }

    public void register(UserOperationDTO userOperationDTO) {
        User user = new User();
        user.setUsername(userOperationDTO.getUsername());

        User existingUser = userRepository.findByUsername(userOperationDTO.getUsername()).orElse(null);
        if (existingUser != null) {
            throw new RuntimeException("Existing user with this username");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(userOperationDTO.getPassword());

        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDTO(user);
    }

    public void saveUser(UserOperationDTO userOperationDTO) {
        User user = new User(userOperationDTO.getUsername(), userOperationDTO.getPassword());
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserDTO> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            UserDTO userDTO = new UserDTO(user);
            userDTOs.add(userDTO);
        }

        return userDTOs;
    }

    public User findByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return null;
        }
        User user = userOptional.get();
        return user;
    }
}


