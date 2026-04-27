package com.samvidhan.controller;

import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.samvidhan.entity.User;
import com.samvidhan.service.UserService;
import com.samvidhan.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // ✅ SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            String result = userService.registerUser(user);
            
            // Standard signup result strings from UserService check e.g. "User registered successfully!", "User already exists!"
            if (result.contains("successfully")) {
                com.samvidhan.entity.User dbUser = userService.getAllUsers().stream()
                        .filter(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))
                        .findFirst()
                        .orElse(user);
                        
                Map<String, Object> response = new HashMap<>();
                response.put("message", result);
                response.put("user", dbUser);
                response.put("token", "mock_token_" + java.util.UUID.randomUUID().toString());
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", result);
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ✅ LOGIN
    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        try {
            String result = userService.loginUser(user);
            if (result.startsWith("Login successful!")) {
                // Fetch the actual user to return
                com.samvidhan.entity.User dbUser = userService.getAllUsers().stream()
                        .filter(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))
                        .findFirst()
                        .orElseThrow(() -> new Exception("User not found"));

                Map<String, Object> response = new HashMap<>();
                response.put("message", result);
                response.put("user", dbUser);
                response.put("token", "mock_token_" + java.util.UUID.randomUUID().toString());
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", result);
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ✅ GOOGLE LOGIN
    @PostMapping("/google")
    public ResponseEntity<?> loginGoogleUser(@RequestBody Map<String, String> body) {
        String credential = body.get("credential");
        try {
            User user = userService.loginGoogleUserV2(credential);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", user);
            response.put("token", "mock_token_" + java.util.UUID.randomUUID().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ✅ GOOGLE SIGNUP
    @PostMapping("/google-signup")
    public ResponseEntity<?> registerGoogleUser(@RequestBody Map<String, String> body) {
        String credential = body.get("credential");
        String roleStr = body.get("role");
        
        com.samvidhan.entity.Role role = com.samvidhan.entity.Role.CITIZEN;
        if (roleStr != null) {
            try {
                role = com.samvidhan.entity.Role.valueOf(roleStr.toUpperCase());
            } catch (Exception e) {
                // ignore
            }
        }

        try {
            User user = userService.registerGoogleUserV2(credential, role);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Signup successful");
            response.put("user", user);
            response.put("token", "mock_token_" + java.util.UUID.randomUUID().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ✅ UPLOAD PROFILE PICTURE
    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String base64Image = body.get("profilePicture");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        user.setProfilePicture(base64Image);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Profile picture updated successfully!", "user", user));
    }
}