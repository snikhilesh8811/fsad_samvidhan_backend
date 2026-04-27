package com.samvidhan.repository;

import com.samvidhan.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByApprovalStatus(com.samvidhan.entity.Status status);
}
