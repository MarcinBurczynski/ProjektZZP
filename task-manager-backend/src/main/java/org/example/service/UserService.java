package org.example.service;

import org.example.config.JwtUtils;
import org.example.dto.PermissionDTO;
import org.example.dto.UserDTO;
import org.example.dto.UserOperationDTO;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements org.springframework.security.core.userdetails.UserDetailsService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public PermissionDTO login(UserOperationDTO userOperationDTO) {
        Optional<User> userOptional = userRepository.findByUsername(userOperationDTO.getUsername());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid login or password");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(userOperationDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid login or password");
        }

        JwtUtils jwtUtils = new JwtUtils();
        return new PermissionDTO(user.getId(),user.getRole(),jwtUtils.generateToken(user));
    }

    public PermissionDTO register(UserOperationDTO userOperationDTO) {
        User existingUser = userRepository.findByUsername(userOperationDTO.getUsername()).orElse(null);
        if (existingUser != null) {
            throw new RuntimeException("Existing user with this username");
        }

        String encodedPassword = passwordEncoder.encode(userOperationDTO.getPassword());

        User user = new User(userOperationDTO.getUsername(),encodedPassword,userOperationDTO.getEmail(),userOperationDTO.getRole());
        userRepository.save(user);

        emailService.sendSimpleMessage(userOperationDTO.getEmail(), "Witaj!", "Dziękujemy za rejestrację w serwisie Taskero!!!");

        JwtUtils jwtUtils = new JwtUtils();
        return new PermissionDTO(user.getId(),user.getRole(),jwtUtils.generateToken(user));
    }

    public UserDTO createUser(User permUser,UserOperationDTO userOperationDTO) {
        if(permUser.getRole().ordinal()>0){
            throw new SecurityException("You can't create users!!!");
        }
        String encodedPassword = passwordEncoder.encode(userOperationDTO.getPassword());
        User user = new User(userOperationDTO.getUsername(), encodedPassword,userOperationDTO.getEmail(), userOperationDTO.getRole());
        user = userRepository.save(user);
        return new UserDTO(user);
    }

    public void updateUser(User permUser,UserOperationDTO userOperationDTO) {
        if(!permUser.getId().equals(userOperationDTO.getId()) && permUser.getRole().ordinal()>0){
            throw new SecurityException("You can't update other users!!!");
        }
        if(permUser.getId().equals(userOperationDTO.getId()) && !permUser.getRole().equals(userOperationDTO.getRole())){
            throw new SecurityException("You can't change your privileges!!!");
        }

        userRepository.findByUsername(userOperationDTO.getUsername()).ifPresent(u ->{
            if(!userOperationDTO.getId().equals(u.getId())){
                throw new IllegalArgumentException("Given username is already taken!!!");
            }
        });

        Optional<User> optionalUser = userRepository.findById(userOperationDTO.getId());
        if(optionalUser.isEmpty()){
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();

        String encodedPassword = passwordEncoder.encode(userOperationDTO.getPassword());
        user.setUsername(userOperationDTO.getUsername());
        user.setPassword(encodedPassword);
        user.setEmail(userOperationDTO.getEmail());
        user.setRole(userOperationDTO.getRole());
        userRepository.save(user);
    }

    public void deleteUser(User permUser,Long id) {
        if(permUser.getRole().ordinal()>0){
            throw new SecurityException("You can't delete users!!!");
        }
        userRepository.deleteById(id);
    }

    public UserDTO getUserById(User permUser,Long id) {
        return new UserDTO(getUserObjectById(permUser,id));
    }

    public User getUserObjectById(User permUser,Long id) {
        if(!permUser.getId().equals(id) && permUser.getRole().ordinal()>1){
            throw new SecurityException("You can't view given user!!!");
        }

        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<UserDTO> getUsers(User permUser) {
        if(permUser.getRole().ordinal()>1){
            throw new SecurityException("You can't view users!!!");
        }

        return userRepository.findAll()
            .stream()
            .map(UserDTO::new)
            .toList();
    }

    //authorization methods
    public User getUserFromDetails(UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        return userOptional.get();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRole().toString())
            .build();
    }
}


