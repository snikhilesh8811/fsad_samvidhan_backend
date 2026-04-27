package com.samvidhan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.samvidhan.entity.*;
import com.samvidhan.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // SIGNUP
    public String registerUser(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "User already exists!";
        }

        if ("nikhilesh6757@gmail.com".equalsIgnoreCase(user.getEmail())) {
            user.setRole(Role.ADMIN);
            user.setStatus(Status.APPROVED);
        } else if (user.getRole() == Role.CITIZEN) {
            user.setStatus(Status.APPROVED);
        } else {
            user.setStatus(Status.PENDING);
        }

        userRepository.save(user);

        return "User registered successfully!";
    }

    // LOGIN
    public String loginUser(User user) {

        User existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (existingUser == null) {
            return "User not found!";
        }

        if (!existingUser.getPassword().equals(user.getPassword())) {
            return "Invalid password!";
        }

        if (existingUser.getRole() != Role.CITIZEN &&
                existingUser.getStatus() == Status.PENDING) {
            return "Waiting for admin approval!";
        }

        if (existingUser.getStatus() == Status.REJECTED) {
            return "Your request was rejected!";
        }

        return "Login successful! Role: " + existingUser.getRole();
    }

    // DECODE GOOGLE JWT
    private Map<String, Object> decodeGoogleJwt(String credential) {
        try {
            String[] parts = credential.split("\\.");
            if (parts.length < 2)
                throw new Exception("Invalid token structure");

            // Google JWTs truncate their base64 padding, so we must compensate.
            String base64Payload = parts[1];
            while (base64Payload.length() % 4 != 0) {
                base64Payload += "=";
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(base64Payload));
            return new ObjectMapper().readValue(payloadJson, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Could not verify Google credential", e);
        }
    }

    // GOOGLE SIGN IN (JSON Response)
    public User loginGoogleUserV2(String credential) throws Exception {
        Map<String, Object> payload = decodeGoogleJwt(credential);
        String email = (String) payload.get("email");
        String picture = (String) payload.get("picture");

        if (email == null) {
            throw new Exception("Google did not return an email!");
        }

        User existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser == null) {
            throw new Exception("Account not found. Please sign up.");
        }

        if (existingUser.getRole() != Role.CITIZEN && existingUser.getStatus() == Status.PENDING) {
            throw new Exception("Waiting for admin approval!");
        }

        if (existingUser.getStatus() == Status.REJECTED) {
            throw new Exception("Your request was rejected!");
        }

        if (picture != null && existingUser.getProfilePicture() == null) {
            existingUser.setProfilePicture(picture);
            userRepository.save(existingUser);
        }

        return existingUser;
    }

    // GOOGLE SIGN UP (JSON Response)
    public User registerGoogleUserV2(String credential, Role role) throws Exception {
        Map<String, Object> payload = decodeGoogleJwt(credential);
        String email = (String) payload.get("email");
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        if (email == null) {
            throw new Exception("Google did not return an email!");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Account already exists. Please sign in.");
        }

        if ("nikhilesh6757@gmail.com".equalsIgnoreCase(email)) {
            role = Role.ADMIN;
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name != null ? name : email.split("@")[0]);
        newUser.setPassword("__oauth_google__");
        newUser.setRole(role);

        if (role == Role.CITIZEN || role == Role.ADMIN) {
            newUser.setStatus(Status.APPROVED);
        } else {
            newUser.setStatus(Status.PENDING);
        }

        if (picture != null) {
            newUser.setProfilePicture(picture);
        }

        return userRepository.save(newUser);
    }

    // GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // APPROVE
    public String approveUser(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null)
            return "User not found!";

        user.setStatus(Status.APPROVED);
        userRepository.save(user);

        return "User approved successfully!";
    }

    // REJECT
    public String rejectUser(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null)
            return "User not found!";

        user.setStatus(Status.REJECTED);
        userRepository.save(user);

        return "User rejected!";
    }
}