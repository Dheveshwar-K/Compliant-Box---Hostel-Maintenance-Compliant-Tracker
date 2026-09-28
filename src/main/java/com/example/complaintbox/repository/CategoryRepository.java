package com.example.complaintbox.repository;

import com.example.complaintbox.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

} 
