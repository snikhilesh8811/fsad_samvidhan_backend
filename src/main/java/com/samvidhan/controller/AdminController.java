package com.samvidhan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.samvidhan.entity.User;
import com.samvidhan.service.UserService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserService userService;

    // ✅ GET ALL USERS FOR NEW ADMIN DASHBOARD
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // ✅ PENDING APPROVALS
    @GetMapping("/pending-approvals")
    public List<User> getPendingUsers() {
        return userService.getAllUsers().stream()
                .filter(u -> com.samvidhan.entity.Status.PENDING.equals(u.getStatus()))
                .toList();
    }

    // ✅ UPDATE APPROVAL STATUS (Approve or Reject via PATCH)
    @PatchMapping("/approvals/{id}")
    public ResponseEntity<?> updateApprovalStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String status = body.get("status");
        if ("APPROVED".equalsIgnoreCase(status)) {
            userService.approveUser(id);
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            userService.rejectUser(id);
        }
        return ResponseEntity.ok(java.util.Map.of("message", "Status updated to " + status));
    }
}