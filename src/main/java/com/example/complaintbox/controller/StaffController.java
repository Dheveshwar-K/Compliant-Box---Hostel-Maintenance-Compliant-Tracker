package com.example.complaintbox.controller;

import com.example.complaintbox.entity.Staff;
import com.example.complaintbox.service.StaffService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    // =========================================================
    // CREATE STAFF
    // POST /api/staff
    // =========================================================

    @PostMapping
    public ResponseEntity<Staff> createStaff(
            @Valid @RequestBody Staff staff) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        staffService.createStaff(staff)
                );
    }


    // =========================================================
    // GET ALL STAFF
    // GET /api/staff
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {

        return ResponseEntity.ok(
                staffService.getAllStaff()
        );
    }


    // =========================================================
    // GET STAFF BY ID
    // GET /api/staff/{id}
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaffById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                staffService.getStaffById(id)
        );
    }


    // =========================================================
    // UPDATE STAFF
    // PUT /api/staff/{id}
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody Staff staff) {

        return ResponseEntity.ok(
                staffService.updateStaff(
                        id,
                        staff
                )
        );
    }


    // =========================================================
    // DELETE STAFF
    // DELETE /api/staff/{id}
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStaff(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                staffService.deleteStaff(id)
        );
    }
}