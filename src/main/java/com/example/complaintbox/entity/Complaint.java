package com.example.complaintbox.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaint")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Complaint category
    @ManyToOne
    private Category category;

    // Room number
    private String roomNumber;

    // Complaint description
    private String description;

    // OPEN / IN_PROGRESS / RESOLVED
    private String status;

    // Remark added when status is changed
    private String statusRemark;

    // Complaint creation time
    private LocalDateTime createdAt;

    // Automatically marked true after 5 days
    private Boolean overdue = false;

    // Resident who created the complaint
    @ManyToOne
    private Hosteller hosteller;

    // Staff assigned to handle the complaint
    @ManyToOne
    private Staff assignedStaff;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public Complaint() {
    }


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Complaint(
            Category category,
            String roomNumber,
            String description,
            String status,
            String statusRemark,
            LocalDateTime createdAt,
            Boolean overdue,
            Hosteller hosteller,
            Staff assignedStaff) {

        this.category = category;
        this.roomNumber = roomNumber;
        this.description = description;
        this.status = status;
        this.statusRemark = statusRemark;
        this.createdAt = createdAt;
        this.overdue = overdue;
        this.hosteller = hosteller;
        this.assignedStaff = assignedStaff;
    }


    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getStatusRemark() {
        return statusRemark;
    }

    public void setStatusRemark(String statusRemark) {
        this.statusRemark = statusRemark;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public boolean isOverdue() {
        return Boolean.TRUE.equals(overdue);
    }

    public void setOverdue(Boolean overdue) {
        this.overdue = overdue;
    }


    public Hosteller getHosteller() {
        return hosteller;
    }

    public void setHosteller(Hosteller hosteller) {
        this.hosteller = hosteller;
    }


    public Staff getAssignedStaff() {
        return assignedStaff;
    }

    public void setAssignedStaff(Staff assignedStaff) {
        this.assignedStaff = assignedStaff;
    }
}