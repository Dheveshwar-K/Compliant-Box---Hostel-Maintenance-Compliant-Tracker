package com.example.complaintbox.service;

import com.example.complaintbox.entity.Staff;
import com.example.complaintbox.repository.StaffRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    // Create staff
    public Staff createStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    // Get all staff
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    // Get staff by ID
    public Staff getStaffById(Long id) {

        return staffRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Staff not found with id: " + id));
    }

    // Update staff
    public Staff updateStaff(Long id, Staff updatedStaff) {

        Staff existingStaff = staffRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Staff not found with id: " + id));

        existingStaff.setName(updatedStaff.getName());
        existingStaff.setRole(updatedStaff.getRole());

        return staffRepository.save(existingStaff);
    }

    // Delete staff
    public String deleteStaff(Long id) {

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Staff not found with id: " + id));

        staffRepository.delete(staff);

        return "Staff deleted successfully";
    }
}
