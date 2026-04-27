package com.samvidhan.controller;

import com.samvidhan.entity.Lesson;
import com.samvidhan.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "*")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    // 1. PUBLIC / CITIZEN ENDPOINT - ONLY GET APPROVED LESSONS
    @GetMapping
    public List<Lesson> getPublishedLessons() {
        return lessonService.getPublishedLessons();
    }

    // 2. ADMIN ENDPOINT - GET ALL LESSONS
    @GetMapping("/all")
    public List<Lesson> getAllLessons() {
        return lessonService.getAllLessons();
    }

    // 3. LEGAL EXPERT ENDPOINT - GET PENDING LESSONS
    @GetMapping("/pending")
    public List<Lesson> getPendingLessons() {
        return lessonService.getPendingLessons();
    }

    // 4. LEGAL EXPERT ENDPOINT - APPROVE LESSON
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Lesson> approveLesson(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(lessonService.approveLesson(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. LEGAL EXPERT ENDPOINT - GET REJECTED LESSONS
    @GetMapping("/rejected")
    public List<Lesson> getRejectedLessons() {
        return lessonService.getRejectedLessons();
    }

    // 6. LEGAL EXPERT ENDPOINT - REJECT LESSON
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Lesson> rejectLesson(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        try {
            String reason = body.get("reason");
            return ResponseEntity.ok(lessonService.rejectLesson(id, reason));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Long id) {
        return lessonService.getLessonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // EDUCATOR ENDPOINT - GET EXPERT APPROVED LESSONS
    @GetMapping("/expert-approved")
    public List<Lesson> getExpertApprovedLessons() {
        return lessonService.getExpertApprovedLessons();
    }

    // EDUCATOR ENDPOINT - PUBLISH LESSON
    @PatchMapping("/{id}/publish")
    public ResponseEntity<Lesson> publishLesson(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(lessonService.publishLesson(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // EDUCATOR / ADMIN ENDPOINT
    @PostMapping
    public Lesson createLesson(@RequestBody Lesson lesson) {
        return lessonService.createLesson(lesson);
    }

    // EDUCATOR / ADMIN ENDPOINT
    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long id, @RequestBody Lesson lesson) {
        try {
            return ResponseEntity.ok(lessonService.updateLesson(id, lesson));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}
