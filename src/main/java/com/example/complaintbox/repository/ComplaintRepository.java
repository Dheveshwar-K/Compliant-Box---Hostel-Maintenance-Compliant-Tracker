package com.example.complaintbox.repository;

import com.example.complaintbox.entity.Complaint;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository
        extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStatusOrderByCreatedAtAsc(String status);

    List<Complaint> findByHostellerId(Long hostellerId);

    List<Complaint> findByOverdueTrue();
}
