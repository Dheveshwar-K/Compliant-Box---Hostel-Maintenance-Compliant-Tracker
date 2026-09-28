package com.example.complaintbox.service;

import com.example.complaintbox.entity.Complaint;
import com.example.complaintbox.entity.Staff;
import com.example.complaintbox.repository.ComplaintRepository;
import com.example.complaintbox.repository.StaffRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StaffRepository staffRepository;

    public ComplaintService(
            ComplaintRepository complaintRepository,
            StaffRepository staffRepository) {

        this.complaintRepository = complaintRepository;
        this.staffRepository = staffRepository;
    }

    // --------------------------------------------------
    // CREATE COMPLAINT
    // --------------------------------------------------

    public Complaint createComplaint(Complaint complaint) {

        complaint.setStatus("OPEN");

        complaint.setCreatedAt(LocalDateTime.now());

        complaint.setOverdue(false);

        return complaintRepository.save(complaint);
    }

    // --------------------------------------------------
    // GET ALL COMPLAINTS
    // --------------------------------------------------

    public List<Complaint> getAllComplaints() {

        updateOverdueComplaints();

        return complaintRepository.findAll();
    }

    // --------------------------------------------------
    // GET COMPLAINT BY ID
    // --------------------------------------------------

    public Complaint getComplaintById(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Complaint not found with id: " + id));

        checkOverdue(complaint);

        return complaint;
    }

    // --------------------------------------------------
    // RESIDENT VIEWS OWN COMPLAINTS
    // --------------------------------------------------

    public List<Complaint> getComplaintsByHosteller(Long hostellerId) {

        updateOverdueComplaints();

        return complaintRepository
                .findByHostellerId(hostellerId);
    }

    // --------------------------------------------------
    // WARDEN VIEWS OPEN COMPLAINTS
    // SORTED BY AGE
    // --------------------------------------------------

    public List<Complaint> getOpenComplaints() {

        updateOverdueComplaints();

        return complaintRepository
                .findByStatusOrderByCreatedAtAsc("OPEN");
    }

    // --------------------------------------------------
    // GET OVERDUE COMPLAINTS
    // --------------------------------------------------

    public List<Complaint> getOverdueComplaints() {

        updateOverdueComplaints();

        return complaintRepository.findByOverdueTrue();
    }

    // --------------------------------------------------
    // UPDATE COMPLAINT STATUS
    // --------------------------------------------------

    public Complaint updateComplaintStatus(
            Long complaintId,
            Long staffId,
            String newStatus,
            String remark) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Complaint not found with id: " + complaintId));

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Staff not found with id: " + staffId));

        String oldStatus = complaint.getStatus();

        validateStatus(newStatus);

        validateStatusTransition(
                oldStatus,
                newStatus,
                remark
        );

        // Only assigned staff or warden can resolve
        if ("RESOLVED".equals(newStatus)) {

            boolean isWarden =
                    "WARDEN".equalsIgnoreCase(staff.getRole());

            boolean isAssignedStaff =
                    complaint.getAssignedStaff() != null &&
                    complaint.getAssignedStaff()
                            .getId()
                            .equals(staff.getId());

            if (!isWarden && !isAssignedStaff) {

                throw new RuntimeException(
                        "Only the assigned staff or warden can resolve this complaint"
                );
            }
        }

        complaint.setStatus(newStatus);

        if (remark != null && !remark.trim().isEmpty()) {
            complaint.setStatusRemark(remark);
        }

        if ("RESOLVED".equals(newStatus)) {
            complaint.setOverdue(false);
        }

        return complaintRepository.save(complaint);
    }

    // --------------------------------------------------
    // ASSIGN STAFF TO COMPLAINT
    // --------------------------------------------------

    public Complaint assignStaff(
            Long complaintId,
            Long staffId) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Complaint not found with id: " + complaintId));

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Staff not found with id: " + staffId));

        complaint.setAssignedStaff(staff);

        return complaintRepository.save(complaint);
    }

    // --------------------------------------------------
    // DELETE COMPLAINT
    // --------------------------------------------------

    public String deleteComplaint(Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Complaint not found with id: " + id));

        complaintRepository.delete(complaint);

        return "Complaint deleted successfully";
    }

    // --------------------------------------------------
    // STATUS VALIDATION
    // --------------------------------------------------

    private void validateStatus(String status) {

        if (!"OPEN".equals(status) &&
            !"IN_PROGRESS".equals(status) &&
            !"RESOLVED".equals(status)) {

            throw new RuntimeException(
                    "Invalid status. Allowed values are: " +
                    "OPEN, IN_PROGRESS, RESOLVED");
        }
    }

    // --------------------------------------------------
    // STATUS TRANSITION RULE
    // --------------------------------------------------

    private void validateStatusTransition(
            String oldStatus,
            String newStatus,
            String remark) {

        if (java.util.Objects.equals(oldStatus, newStatus)) {
            return;
        }

        // OPEN -> IN_PROGRESS
        if ("OPEN".equals(oldStatus) &&
                "IN_PROGRESS".equals(newStatus)) {
            return;
        }

        // IN_PROGRESS -> RESOLVED
        if ("IN_PROGRESS".equals(oldStatus) &&
                "RESOLVED".equals(newStatus)) {
            return;
        }

        // Any backward movement requires a remark
        if (isBackwardTransition(oldStatus, newStatus)) {

            if (remark == null ||
                    remark.trim().isEmpty()) {

                throw new RuntimeException(
                        "A remark is required when moving a complaint backward"
                );
            }

            return;
        }

        throw new RuntimeException(
                "Invalid status transition: " +
                oldStatus + " -> " + newStatus +
                ". Allowed sequence is OPEN -> IN_PROGRESS -> RESOLVED"
        );
    }

    // --------------------------------------------------
    // CHECK BACKWARD MOVEMENT
    // --------------------------------------------------

    private boolean isBackwardTransition(
            String oldStatus,
            String newStatus) {

        int oldValue = getStatusValue(oldStatus);
        int newValue = getStatusValue(newStatus);

        return newValue < oldValue;
    }

    private int getStatusValue(String status) {

        switch (status) {

            case "OPEN":
                return 1;

            case "IN_PROGRESS":
                return 2;

            case "RESOLVED":
                return 3;

            default:
                return 0;
        }
    }

    // --------------------------------------------------
    // CHECK OVERDUE
    // --------------------------------------------------

    private void checkOverdue(Complaint complaint) {

        if (complaint.getCreatedAt() == null) {
            return;
        }

        if ("RESOLVED".equals(complaint.getStatus())) {
            return;
        }

        LocalDateTime fiveDaysAgo =
                LocalDateTime.now().minusDays(5);

        if (complaint.getCreatedAt().isBefore(fiveDaysAgo)) {

            complaint.setOverdue(true);

            complaintRepository.save(complaint);
        }
    }

    // --------------------------------------------------
    // UPDATE ALL OVERDUE COMPLAINTS
    // --------------------------------------------------

    public void updateOverdueComplaints() {

        List<Complaint> complaints =
                complaintRepository.findAll();

        LocalDateTime fiveDaysAgo =
                LocalDateTime.now().minusDays(5);

        for (Complaint complaint : complaints) {

            if (complaint.getCreatedAt() == null) {
                continue;
            }

            if ("RESOLVED".equals(complaint.getStatus())) {
                continue;
            }

            if (complaint.getCreatedAt()
                    .isBefore(fiveDaysAgo)) {

                if (!complaint.isOverdue()) {

                    complaint.setOverdue(true);

                    complaintRepository.save(complaint);
                }
            }
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void automaticallyFlagOverdueComplaints() {
    updateOverdueComplaints();
}
}
