package com.samvidhan.service;

import com.samvidhan.entity.Lesson;
import com.samvidhan.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public List<Lesson> getPublishedLessons() {
        return lessonRepository.findByApprovalStatus(com.samvidhan.entity.Status.PUBLISHED);
    }

    public List<Lesson> getPendingLessons() {
        return lessonRepository.findByApprovalStatus(com.samvidhan.entity.Status.PENDING);
    }

    public List<Lesson> getRejectedLessons() {
        return lessonRepository.findByApprovalStatus(com.samvidhan.entity.Status.REJECTED);
    }

    public Optional<Lesson> getLessonById(Long id) {
        return lessonRepository.findById(id);
    }

    public Lesson createLesson(Lesson lesson) {
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());
        lesson.setApprovalStatus(com.samvidhan.entity.Status.PENDING);
        return lessonRepository.save(lesson);
    }

    public Lesson updateLesson(Long id, Lesson updatedLesson) {
        return lessonRepository.findById(id).map(lesson -> {
            lesson.setTitle(updatedLesson.getTitle());
            lesson.setContent(updatedLesson.getContent());
            lesson.setModuleName(updatedLesson.getModuleName());
            lesson.setUpdatedAt(LocalDateTime.now());
            // Edits send it back to PENDING for re-approval
            lesson.setApprovalStatus(com.samvidhan.entity.Status.PENDING);
            return lessonRepository.save(lesson);
        }).orElseThrow(() -> new RuntimeException("Lesson not found"));
    }

    public List<Lesson> getExpertApprovedLessons() {
        return lessonRepository.findByApprovalStatus(com.samvidhan.entity.Status.EXPERT_APPROVED);
    }

    public Lesson approveLesson(Long id) {
        return lessonRepository.findById(id).map(lesson -> {
            lesson.setApprovalStatus(com.samvidhan.entity.Status.EXPERT_APPROVED);
            lesson.setUpdatedAt(LocalDateTime.now());
            return lessonRepository.save(lesson);
        }).orElseThrow(() -> new RuntimeException("Lesson not found"));
    }

    public Lesson publishLesson(Long id) {
        return lessonRepository.findById(id).map(lesson -> {
            lesson.setApprovalStatus(com.samvidhan.entity.Status.PUBLISHED);
            lesson.setUpdatedAt(LocalDateTime.now());
            return lessonRepository.save(lesson);
        }).orElseThrow(() -> new RuntimeException("Lesson not found"));
    }

    public Lesson rejectLesson(Long id, String reason) {
        return lessonRepository.findById(id).map(lesson -> {
            lesson.setApprovalStatus(com.samvidhan.entity.Status.REJECTED);
            lesson.setRejectionReason(reason);
            lesson.setUpdatedAt(LocalDateTime.now());
            return lessonRepository.save(lesson);
        }).orElseThrow(() -> new RuntimeException("Lesson not found"));
    }

    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }
}
