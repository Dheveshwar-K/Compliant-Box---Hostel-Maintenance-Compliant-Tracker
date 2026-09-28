package com.example.complaintbox.controller;

import com.example.complaintbox.entity.Complaint;
import com.example.complaintbox.service.ComplaintService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // Resident raises complaint
    @PostMapping
    public ResponseEntity<Complaint> createComplaint(
            @Valid @RequestBody Complaint complaint) {

        Complaint created =
                complaintService.createComplaint(complaint);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // Get all complaints
    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {

        return ResponseEntity.ok(
                complaintService.getAllComplaints()
        );
    }

    // Get complaint by ID
    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                complaintService.getComplaintById(id)
        );
    }

    // Resident views own complaints
    @GetMapping("/hosteller/{hostellerId}")
    public ResponseEntity<List<Complaint>>
    getComplaintsByHosteller(
            @PathVariable Long hostellerId) {

        return ResponseEntity.ok(
                complaintService
                        .getComplaintsByHosteller(hostellerId)
        );
    }

    // Warden views open complaints sorted by age
    @GetMapping("/open")
    public ResponseEntity<List<Complaint>>
    getOpenComplaints() {

        return ResponseEntity.ok(
                complaintService.getOpenComplaints()
        );
    }

    // Get overdue complaints
    @GetMapping("/overdue")
    public ResponseEntity<List<Complaint>>
    getOverdueComplaints() {

        return ResponseEntity.ok(
                complaintService.getOverdueComplaints()
        );
    }

    // Assign staff
    @PutMapping("/{complaintId}/assign/{staffId}")
    public ResponseEntity<Complaint> assignStaff(
            @PathVariable Long complaintId,
            @PathVariable Long staffId) {

        return ResponseEntity.ok(
                complaintService.assignStaff(
                        complaintId,
                        staffId
                )
        );
    }

    // Staff/Warden updates status
    @PutMapping("/{complaintId}/status")
    public ResponseEntity<Complaint> updateStatus(

            @PathVariable Long complaintId,

            @RequestParam Long staffId,

            @RequestParam String status,

            @RequestParam(required = false) String remark) {

        return ResponseEntity.ok(
                complaintService.updateComplaintStatus(
                        complaintId,
                        staffId,
                        status,
                        remark
                )
        );
    }

    // Delete complaint
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComplaint(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                complaintService.deleteComplaint(id)
        );
    }
    @GetMapping("/hello")
    public String hello() {
    return "ComplaintBox is running";
}
} 
