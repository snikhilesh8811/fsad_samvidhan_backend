package com.samvidhan.controller;

import com.samvidhan.entity.ContactQuery;
import com.samvidhan.repository.ContactQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private ContactQueryRepository contactQueryRepository;

    // PUBLIC: Submit a new contact query
    @PostMapping
    public ContactQuery submitQuery(@RequestBody ContactQuery query) {
        return contactQueryRepository.save(query);
    }

    // ADMIN: View all queries
    @GetMapping
    public List<ContactQuery> getAllQueries() {
        return contactQueryRepository.findAllByOrderByCreatedAtDesc();
    }

    // ADMIN: Delete a handled query
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuery(@PathVariable Long id) {
        contactQueryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
